package shattered.lib.registry;

import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import com.google.gson.reflect.TypeToken;
import it.unimi.dsi.fastutil.objects.ObjectObjectImmutablePair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import preboot.AnnotationRegistry;
import shattered.core.event.EventBusSubscriber;
import shattered.core.event.MessageEvent;
import shattered.core.event.MessageListener;
import shattered.Shattered;
import shattered.lib.ResourceLocation;
import shattered.lib.json.JsonUtils;

@EventBusSubscriber(Shattered.SYSTEM_BUS_NAME)
final class RegistryLoader {

	static final Logger LOGGER = LogManager.getLogger("Registries");

	private static void loadRegistries() {
		final Map<ResourceLocation, List<ResourceLocation>> registries = Registry.REGISTRIES.keySet().stream()
				.filter(resource -> !resource.equals(new ResourceLocation("assets")))
				.filter(resource -> !resource.equals(new ResourceLocation("asset_types")))
				.map(resource -> {
					Shattered.LOGGER.debug("Loading json registry for registry: {}", resource);
					final String location = RegistryLoader.getResourcePath(resource, null);
					try (final InputStreamReader reader = new InputStreamReader(RegistryLoader.class.getResourceAsStream(location))) {
						final Type reflectType = TypeToken.getParameterized(ArrayList.class, ResourceLocation.class).getType();
						return new ObjectObjectImmutablePair<>(resource, JsonUtils.GSON.<ArrayList<ResourceLocation>>fromJson(reader, reflectType));
					} catch (final IOException | NullPointerException e) {
						Shattered.crash("Could not load json registry for registry: " + resource, e);
						return null;
					}
				})
				.filter(Objects::nonNull)
				.collect(Collectors.toMap(ObjectObjectImmutablePair::left, ObjectObjectImmutablePair::right));
		registries.forEach(RegistryLoader::loadRegistry);
	}

	@SuppressWarnings("unchecked")
	private static void loadRegistry(@NotNull final ResourceLocation resource, @NotNull final List<ResourceLocation> list) {
		Shattered.LOGGER.info("Loading registry: {}", resource);
		final Registry<?> registry = Registry.REGISTRIES.get(resource);

		final List<Class<?>> classes = AnnotationRegistry.getAnnotatedClasses(RegistryParser.RegistryParserMetadata.class);
		@SuppressWarnings("unchecked") final Class<? extends RegistryParser<?>> parserClazz = (Class<? extends RegistryParser<?>>) classes.stream()
				.filter(clazz -> clazz.getAnnotation(RegistryParser.RegistryParserMetadata.class).value() == registry.typeClazz)
				.filter(RegistryParser.class::isAssignableFrom)
				.findFirst().orElse(null);
		if (parserClazz == null) {
			Shattered.crash("Could not find parser for registry: " + resource, new ClassNotFoundException(
					RegistryParser.class.getName() + '<' + registry.typeClazz.getName() + '>'
			));
		}
		assert parserClazz != null;

		RegistryParser<?> parser = null;
		try {
			parser = parserClazz.getDeclaredConstructor().newInstance();
		} catch (final Throwable e) {
			Shattered.crash("Could not instantiate parser", e);
		}
		assert parser != null;

		for (final ResourceLocation subResource : list) {
			final String path = RegistryLoader.getResourcePath(subResource, resource.getResource());
			try (final InputStreamReader reader = new InputStreamReader(RegistryLoader.class.getResourceAsStream(path))) {
				final Object parsedData = JsonUtils.deserialize(reader, parser.getWrapperClass());
				if (parsedData == null) {
					Shattered.crash("Could not load resource: " + subResource, null);
				}
				assert parsedData != null;
				final Map<ResourceLocation, ?> variantMapping = parser.parse(subResource, parsedData);
				for (final Map.Entry<ResourceLocation, ?> entry : variantMapping.entrySet()) {
					final ResourceLocation variantResource = entry.getKey();
					if (!variantResource.getNamespace().equals(subResource.getNamespace()) || !variantResource.getResource().equals(subResource.getResource())) {
						Shattered.crash("Parsing resource " + subResource + " returned invalid variant resource: " + variantResource, null);
					}
					final Object finalType = entry.getValue();
					if (!registry.typeClazz.isAssignableFrom(finalType.getClass())) {
						final StringBuilder reasonBuilder = new StringBuilder("Could not parse resource: ");
						reasonBuilder.append(subResource);
						if (variantMapping.size() > 1) {
							reasonBuilder.append(" (variant: ").append(variantResource.getVariant()).append(')');
						}
						reasonBuilder.append(". ");
						reasonBuilder.append("Expected type: ").append(registry.typeClazz.getName());
						reasonBuilder.append(", got: ").append(finalType.getClass().getName());
						Shattered.crash(reasonBuilder.toString(), null);
					}
					((Registry<Object>) registry).register(variantResource, finalType);
				}
			} catch (final IOException | NullPointerException e) {
				Shattered.crash("Resource does not exist: " + subResource, e);
			} catch (final Throwable e) {
				Shattered.crash("Could not load resource: " + subResource, e);
			}
		}
	}

	@NotNull
	private static String getResourcePath(@NotNull final ResourceLocation resource, @Nullable final String type) {
		return String.format("/assets/%s/%s%s.json", resource.getNamespace(), type != null ? type + "/" : "", resource.getResource());
	}

	@MessageListener("load_registries")
	private static void onLoadRegistries(final MessageEvent ignored) {
		RegistryLoader.loadRegistries();
	}
}