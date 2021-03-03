package shattered.lib.asset;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import com.google.gson.reflect.TypeToken;
import it.unimi.dsi.fastutil.objects.ObjectObjectImmutablePair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import shattered.core.event.EventBusSubscriber;
import shattered.core.event.EventListener;
import shattered.core.event.MessageEvent;
import shattered.core.event.MessageListener;
import shattered.Shattered;
import shattered.lib.Localizer;
import shattered.lib.ReflectionHelper;
import shattered.lib.ResourceLocation;
import shattered.lib.json.JsonUtils;
import shattered.lib.math.Dimension;
import shattered.lib.math.Rectangle;
import shattered.lib.registry.CreateRegistryEvent;
import shattered.lib.registry.ResourceSingletonRegistry;

@EventBusSubscriber(Shattered.SYSTEM_BUS_NAME)
public final class AssetRegistry {

	private static ResourceSingletonRegistry<IAsset> ASSETS;
	private static ResourceSingletonRegistry<AssetTypes> TYPES;

	static final Logger LOGGER = LogManager.getLogger("Assets");
	static final AtlasStitcher ATLAS = new AtlasStitcher(false);

	@MessageListener("init_assets")
	private static void onInitAssets(final MessageEvent ignored) {
		//Loading json based registries
		final Map<AssetTypes, List<ResourceLocation>> registries = Arrays.stream(AssetTypes.values())
				.map(type -> {
					AssetRegistry.LOGGER.debug("Loading json registry for asset type: {}", type);
					//TODO handle custom namespaces
					final ResourceLocation resource = new ResourceLocation(type.toString());
					final String location = AssetRegistry.getResourcePath(resource, AssetTypes.BINARY, "json");
					try (final InputStreamReader reader = new InputStreamReader(AssetRegistry.class.getResourceAsStream(location))) {
						final Type reflectType = TypeToken.getParameterized(ArrayList.class, ResourceLocation.class).getType();
						return new ObjectObjectImmutablePair<>(type, JsonUtils.GSON.<ArrayList<ResourceLocation>>fromJson(reader, reflectType));
					} catch (final IOException | NullPointerException e) {
						AssetRegistry.LOGGER.error("Could not load json registry: {}", resource);
						return null;
					}
				})
				.filter(Objects::nonNull)
				.collect(Collectors.toMap(ObjectObjectImmutablePair::left, ObjectObjectImmutablePair::right));
		//Parsing json registries
		Arrays.stream(AssetTypes.values()).forEach(type -> {
			final List<ResourceLocation> list = registries.get(type);
			if (list == null) {
				return;
			}
			Consumer<ResourceLocation> action = null;
			switch (type) {
				case LANGUAGE:
					action = AssetRegistry::loadLanguage;
					break;
				case TEXTURE:
					action = AssetRegistry::loadTexture;
					break;
				case AUDIO:
					action = AssetRegistry::loadAudio;
					break;
				case FONT:
					action = AssetRegistry::loadFont;
					break;
				case LUA:
					action = AssetRegistry::loadLua;
					break;
				case BINARY:
					action = AssetRegistry::loadBinary;
					break;
			}
			if (action != null) {
				list.forEach(action);
			}
		});
		Shattered.SYSTEM_BUS.post(new MessageEvent("reload_localizer"));
	}

	@EventListener
	private static void onCreateRegistry(final CreateRegistryEvent event) {
		AssetRegistry.ASSETS = event.newResourceSingletonRegistry(new ResourceLocation("assets"), IAsset.class);
		AssetRegistry.TYPES = event.newResourceSingletonRegistry(new ResourceLocation("asset_types"), AssetTypes.class);
	}

	@Nullable
	public static IAsset getAsset(final ResourceLocation resource) {
		return AssetRegistry.ASSETS.get(resource);
	}

	private static void loadLanguage(@NotNull final ResourceLocation resource) {
		Localizer.addLanguage(
				AssetRegistry.getPathUrl(AssetRegistry.getResourcePath(resource, AssetTypes.LANGUAGE, "lang")),
				resource
		);
	}

	private static void loadTexture(@NotNull final ResourceLocation resource) {
		final JsonTextureData data = Optional
				.ofNullable(TextureLoader.loadJsonData(resource))
				.orElseGet(() -> {
					final JsonTextureData dummy = new JsonTextureData();
					dummy.textureType = TextureType.DEFAULT;
					return dummy;
				});
		if (data.variants == null) {
			data.variants = new HashMap<>();
			data.variants.put(ResourceLocation.DEFAULT_VARIANT, resource);
		}
		data.variants.forEach((variant, variantTextureLocation) -> {
			final ResourceLocation variantResource = resource.toVariant(variant);
			JsonTextureData variantMetadata = TextureLoader.loadVariantJsonData(variantResource, variantTextureLocation);
			if (variantMetadata == null) {
				variantMetadata = data;
			}
			final IAsset[] Textures = AssetRegistry.createTexture(variantMetadata, variantResource);
			if (Textures == null) {
				AssetRegistry.registerInternal(variantResource, AssetTypes.TEXTURE, null);
				AssetRegistry.LOGGER.error("Could not load texture: {}", variantResource);
				return;
			}
			Arrays.stream(Textures).forEach(result -> AssetRegistry.registerInternal(result.getResource(), AssetTypes.TEXTURE, result));
			AssetRegistry.LOGGER.debug("Registered texture: {}", variantResource);
		});
	}

	@Nullable
	private static IAsset[] createTexture(@NotNull final JsonTextureData data, @NotNull final ResourceLocation resource) {
		final BufferedImage image = TextureLoader.loadImage(resource);
		if (image == null) {
			return null;
		}
		switch (data.textureType) {
			case DEFAULT:
				return new IAsset[]{TextureLoader.createTextureDefault(resource, image)};
			case STITCHED:
				return TextureLoader.createTextureStitched(resource, image, data);
			case MAPPED:
				return TextureLoader.createTextureMapped(resource, image, data);
			case ANIMATED:
				return new IAsset[]{TextureLoader.createTextureAnimated(resource, image, data)};
		}
		return null;
	}

	@ReflectionHelper.Reflectable
	private static Texture createSimpleTexture(@NotNull final ResourceLocation resource) {
		final TextureSimple result = AssetRegistry.createTextureDirect(resource);
		if (result == null) {
			AssetRegistry.registerInternal(resource, AssetTypes.TEXTURE, null);
			AssetRegistry.LOGGER.error("Could not load texture: {}", resource);
			return null;
		}
		AssetRegistry.registerInternal(resource, AssetTypes.TEXTURE, result);
		AssetRegistry.LOGGER.debug("Registered texture: {}", resource);
		return result;
	}

	@Nullable
	private static TextureSimple createTextureDirect(@NotNull final ResourceLocation resource) {
		final BufferedImage image = TextureLoader.loadImage(resource);
		if (image == null) {
			return null;
		}
		final int[] data = ImageLoader.loadTexture(image);
		final Dimension size = Dimension.create(image.getWidth(), image.getHeight());
		return new TextureSimple(resource, data[0], size, Dimension.create(data[1], data[2]), Rectangle.create(0, 0, size));
	}

	private static void loadAudio(@NotNull final ResourceLocation resource) {
		final JsonAudioData data = Optional
				.ofNullable(AudioLoader.loadJsonData(resource))
				.orElseGet(() -> {
					final JsonAudioData dummy = new JsonAudioData();
					dummy.audioType = AudioLoader.AudioType.OGG;
					return dummy;
				});
		final String path = AssetRegistry.getResourcePath(resource, AssetTypes.AUDIO, data.audioType.toString());
		final URL location = AssetRegistry.getPathUrl(path);
		if (location == null) {
			AssetRegistry.LOGGER.error("Registered audio.json \"{}\" does not exist!", resource);
			AssetRegistry.LOGGER.error("\tExpected filepath: {}", path);
			AssetRegistry.registerInternal(resource, AssetTypes.AUDIO, null);
			return;
		}
		final Audio result = new Audio(resource, data);
		AssetRegistry.registerInternal(resource, AssetTypes.AUDIO, result);
		AssetRegistry.LOGGER.error("Registered audio: {}", resource);
	}

	@ReflectionHelper.Reflectable
	private static FontGroup loadFont(@NotNull final ResourceLocation resource) {
		final FontGroup result = AssetRegistry.createFont(resource);
		if (result == null) {
			AssetRegistry.registerInternal(resource, AssetTypes.FONT, null);
			AssetRegistry.LOGGER.error("Could not load font: {}", resource);
			return null;
		}
		AssetRegistry.registerInternal(resource, AssetTypes.FONT, result);
		AssetRegistry.LOGGER.debug("Registered font: {}", resource);
		return result;
	}

	@Nullable
	private static FontGroup createFont(@NotNull final ResourceLocation resource) {
		final java.awt.Font baseFont = FontLoader.loadFontAwt(resource);
		if (baseFont == null) {
			return null;
		}
		final FontGroup result = new FontGroup(resource, baseFont);
		for (final int size : FontGroup.DEFAULT_SIZES) {
			result.addSize(size);
		}
		return result;
	}

	private static void loadLua(@NotNull final ResourceLocation resource) {
		AssetRegistry.loadLua(resource, false);
	}

	@ReflectionHelper.Reflectable
	private static LuaAsset loadLua(@NotNull final ResourceLocation resource, final boolean customHandling) {
		if (AssetRegistry.ASSETS.contains(resource)) {
			return (LuaAsset) AssetRegistry.ASSETS.get(resource);
		}
		final String path = AssetRegistry.getResourcePath(
				resource,
				!customHandling ? AssetTypes.LUA : AssetTypes.BINARY,
				"lua"
		);
		if (AssetRegistry.getPathUrl(path) == null) {
			AssetRegistry.LOGGER.error("Registered script \"{}\" does not exist!", resource);
			AssetRegistry.LOGGER.error("\tExpected filepath: {}", path);
			AssetRegistry.registerInternal(resource, AssetTypes.LUA, null);
			return null;
		}
		final LuaAsset result = new LuaAsset(resource, path);
		if (!customHandling) {

			AssetRegistry.registerInternal(resource, AssetTypes.LUA, result);
		}
		return result;
	}

	private static void loadBinary(@NotNull final ResourceLocation resource) {
		if (AssetRegistry.ASSETS.contains(resource)) {
			return;
		}
		final BinaryAsset asset = new BinaryAsset(resource, AssetRegistry.getResourcePath(resource, AssetTypes.BINARY, ""));
		AssetRegistry.registerInternal(resource, AssetTypes.BINARY, asset);
	}

	private static void registerInternal(@NotNull final ResourceLocation resource, @NotNull final AssetTypes type, @Nullable final IAsset asset) {
		AssetRegistry.TYPES.register(resource, type);
		AssetRegistry.ASSETS.register(resource, asset);
	}

	@NotNull
	static String getResourcePath(@NotNull final ResourceLocation resource, @NotNull final AssetTypes type, @NotNull final String extension) {
		final StringBuilder builder = new StringBuilder("/assets/");
		builder.append(resource.getNamespace()).append('/');
		builder.append(type.getRoot());
		builder.append(resource.getResource());
		if (extension.length() > 0) {
			builder.append('.').append(extension);
		}
		return builder.toString();
	}

	static URL getPathUrl(@NotNull final String path) {
		return AssetRegistry.class.getResource(path);
	}
}