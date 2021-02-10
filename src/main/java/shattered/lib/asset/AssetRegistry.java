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

	static final Logger LOGGER = LogManager.getLogger("AssetRegistry");
	static final AtlasStitcher ATLAS = new AtlasStitcher();

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
			switch (type) {
				case TEXTURE:
					list.forEach(AssetRegistry::loadTexture);
					break;
			}
		});
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

	private static void loadTexture(@NotNull final ResourceLocation resource) {
		final JsonTextureData data;
		final JsonTextureData loadedData = TextureLoader.loadJsonData(resource);
		if (loadedData != null) {
			data = loadedData;
		} else {
			data = new JsonTextureData();
			data.textureType = TextureType.DEFAULT;
		}
		if (data.variants == null) {
			data.variants = new HashMap<>();
			data.variants.put(ResourceLocation.DEFAULT_VARIANT, resource);
		}
		data.variants.forEach((variant, variantResource) -> {
			final IAsset[] Textures = AssetRegistry.createTexture(data, resource);
			if (Textures == null) {
				AssetRegistry.registerInternal(resource, AssetTypes.TEXTURE, null);
				AssetRegistry.LOGGER.error("Could not load texture: {}", resource);
				return;
			}
			Arrays.stream(Textures).forEach(result -> AssetRegistry.registerInternal(result.getResource(), AssetTypes.TEXTURE, result));
			AssetRegistry.LOGGER.debug("Registered texture: {}", resource);
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

	public static void loadTextureDirectly(@NotNull final ResourceLocation resource) {
		final TextureSimple result = AssetRegistry.createTextureDirect(resource);
		if (result == null) {
			AssetRegistry.registerInternal(resource, AssetTypes.TEXTURE, null);
			AssetRegistry.LOGGER.error("Could not load texture: {}", resource);
			return;
		}
		AssetRegistry.registerInternal(resource, AssetTypes.TEXTURE, result);
		AssetRegistry.LOGGER.debug("Registered texture: {}", resource);
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

	public static void loadFont(@NotNull final ResourceLocation resource) {
		final FontGroup result = AssetRegistry.createFont(resource);
		if (result == null) {
			AssetRegistry.registerInternal(resource, AssetTypes.FONT, null);
			AssetRegistry.LOGGER.error("Could not load font: {}", resource);
			return;
		}
		AssetRegistry.registerInternal(resource, AssetTypes.FONT, result);
		AssetRegistry.LOGGER.debug("Registered font: {}", resource);
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

	@Nullable
	public static LuaAsset loadLua(@NotNull final ResourceLocation resource) {
		IAsset result = AssetRegistry.ASSETS.get(resource);
		if (result instanceof LuaAsset) {
			return (LuaAsset) result;
		}
		if (AssetRegistry.ASSETS.contains(resource)) {
			return null;
		}
		final String path = AssetRegistry.getResourcePath(resource, AssetTypes.LUA, "lua");
		final URL location = AssetRegistry.getPathUrl(path);
		if (location == null) {
			AssetRegistry.LOGGER.error("Registered script \"{}\" does not exist!", resource);
			AssetRegistry.LOGGER.error("\tExpected filepath: {}", path);
			AssetRegistry.registerInternal(resource, AssetTypes.LUA, null);
			return null;
		}
		result = new LuaAsset(resource, path);
		AssetRegistry.registerInternal(resource, AssetTypes.LUA, result);
		return (LuaAsset) result;
	}

	@Nullable
	public static BinaryAsset loadBinary(@NotNull final ResourceLocation resource, @NotNull final String extension) {
		IAsset result = AssetRegistry.ASSETS.get(resource);
		if (result instanceof BinaryAsset) {
			return (BinaryAsset) result;
		}
		if (AssetRegistry.ASSETS.contains(resource)) {
			return null;
		}
		result = new BinaryAsset(resource, AssetRegistry.getResourcePath(resource, AssetTypes.BINARY, extension));
		AssetRegistry.registerInternal(resource, AssetTypes.BINARY, result);
		return (BinaryAsset) result;
	}

	private static void registerInternal(@NotNull final ResourceLocation resource, @NotNull final AssetTypes type, @Nullable final IAsset asset) {
		AssetRegistry.TYPES.register(resource, type);
		AssetRegistry.ASSETS.register(resource, asset);
	}

	@NotNull
	static String getResourcePath(@NotNull final ResourceLocation resource, @NotNull final AssetTypes type, @NotNull final String extension) {
		return String.format("/assets/%s/%s%s.%s", resource.getNamespace(), type.getRoot(), resource.getResource(), extension);
	}

	static URL getPathUrl(@NotNull final String path) {
		return AssetRegistry.class.getResource(path);
	}
}