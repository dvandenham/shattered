package shattered.lib.asset;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Arrays;
import java.util.Map;
import javax.imageio.ImageIO;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import shattered.lib.FastNamedObjectMap;
import shattered.lib.ResourceLocation;
import shattered.lib.json.JsonUtils;
import shattered.lib.math.Dimension;
import shattered.lib.math.Rectangle;

final class TextureLoader {

	private TextureLoader() {
	}

	@Nullable
	public static BufferedImage loadImage(@NotNull final ResourceLocation resource) {
		final String path = AssetRegistry.getResourcePath(resource, AssetTypes.TEXTURE, "png");
		final URL location = AssetRegistry.getPathUrl(path);
		if (location == null) {
			AssetRegistry.LOGGER.error("Registered texture \"{}\" does not exist!", resource);
			AssetRegistry.LOGGER.error("\tExpected filepath: {}", path);
			return null;
		}
		try (final InputStream stream = location.openStream()) {
			return ImageIO.read(stream);
		} catch (final IOException e) {
			AssetRegistry.LOGGER.error("Could not read texture from location: " + path, e);
			return null;
		}
	}

	@Nullable
	public static JsonTextureData loadJsonData(@NotNull final ResourceLocation resource) {
		final String path = AssetRegistry.getResourcePath(resource, AssetTypes.TEXTURE, "json");
		final URL location = AssetRegistry.getPathUrl(path);
		if (location == null) {
			AssetRegistry.LOGGER.error("Registered texture \"{}\" has no matching metadata file!", resource);
			AssetRegistry.LOGGER.error("\tExpected filepath: {}", path);
			AssetRegistry.LOGGER.error("\tAssuming it's a default texture without variants");
			return null;
		}
		try (final InputStreamReader reader = new InputStreamReader(location.openStream())) {
			return JsonUtils.deserialize(reader, JsonTextureData.class);
		} catch (final IOException | JsonIOException | JsonSyntaxException e) {
			AssetRegistry.LOGGER.error("Could not read texture metadata from texture \"{}\"", resource);
			AssetRegistry.LOGGER.error(e);
			AssetRegistry.LOGGER.error("\tIgnoring the metadata and loading as a default texture without variants");
			return null;
		}
	}

	@Nullable
	public static JsonTextureData loadVariantJsonData(@NotNull final ResourceLocation variant, @NotNull final ResourceLocation resource) {
		final String path = AssetRegistry.getResourcePath(resource, AssetTypes.TEXTURE, "png.json");
		final URL location = AssetRegistry.getPathUrl(path);
		if (location == null) {
			return null;
		}
		try (final InputStreamReader reader = new InputStreamReader(location.openStream())) {
			return JsonUtils.deserialize(reader, JsonTextureData.class);
		} catch (final IOException | JsonIOException | JsonSyntaxException e) {
			AssetRegistry.LOGGER.error("Could not read texture variant metadata from texture \"{}\"", variant);
			AssetRegistry.LOGGER.error(e);
			AssetRegistry.LOGGER.error("\tIgnoring the variant metadata and using the parent metadata");
			return null;
		}
	}

	@NotNull
	public static TextureAtlasDefault createTextureDefault(
			@NotNull final ResourceLocation resource,
			@NotNull final BufferedImage image) {
		return AssetRegistry.ATLAS.addImage(resource, image);
	}

	@Nullable
	public static TextureAtlasSub[] createTextureStitched(
			@NotNull final ResourceLocation resource,
			@NotNull final BufferedImage image,
			@NotNull final JsonTextureData data
	) {
		final int usableWidth = data.stitchedUsableWidth != null ? data.stitchedUsableWidth : image.getWidth();
		final int spriteWidth = data.stitchedSpriteWidth != null ? data.stitchedSpriteWidth : data.stitchedSpriteSize;
		final int spriteHeight = data.stitchedSpriteHeight != null ? data.stitchedSpriteHeight : data.stitchedSpriteSize;
		final TextureAtlasStitched original = AssetRegistry.ATLAS.addImageStitched(resource, image, usableWidth, Dimension.create(spriteWidth, spriteHeight));
		final TextureAtlasSub[] result = new TextureAtlasSub[data.stitchedSpriteCount];
		//noinspection ConstantConditions
		for (int i = 0; i < result.length; ++i) {
			result[i] = original.getSubTexture(i);
		}
		return result;
	}

	@Nullable
	public static TextureAtlasSub[] createTextureMapped(
			@NotNull final ResourceLocation resource,
			@NotNull final BufferedImage image,
			@NotNull final JsonTextureData data
	) {
		for (final Map.Entry<String, Rectangle> entry : data.mappedMapping.entrySet()) {
			if (entry.getKey() == null) {
				AssetRegistry.LOGGER.error("Registered texture \"{}\" has invalid metadata!", resource);
				AssetRegistry.LOGGER.error("\tSub-texture mapping contains a \"null\" key");
				return null;
			} else if (entry.getValue() == null) {
				AssetRegistry.LOGGER.error("Registered texture \"{}\" has invalid metadata!", resource);
				AssetRegistry.LOGGER.error("\tSub-texture mapping \"{}\" contains a \"null\" value", entry.getKey());
				return null;
			}
		}
		final FastNamedObjectMap<Rectangle> mapping = new FastNamedObjectMap<>();
		mapping.putAll(data.mappedMapping);
		final TextureAtlasMapped original = AssetRegistry.ATLAS.addImageMapped(resource, image, mapping);
		return data.mappedMapping.keySet().stream().map(original::getSubTexture).toArray(TextureAtlasSub[]::new);
	}

	@Nullable
	public static Texture createTextureAnimated(
			@NotNull final ResourceLocation resource,
			@NotNull final BufferedImage image,
			@NotNull final JsonTextureData data
	) {
		int type = -1;
		if (image.getWidth() > image.getHeight()) {
			if (image.getWidth() % image.getHeight() != 0) {
				AssetRegistry.LOGGER.warn("Loaded animated texture has wrong aspect ratio: {}", resource);
				return null;
			}
			type = 0;
		} else if (image.getHeight() > image.getWidth()) {
			if (image.getHeight() % image.getWidth() != 0) {
				AssetRegistry.LOGGER.warn("Loaded animated texture has wrong aspect ratio: {}", resource);
				return null;
			}
			type = 1;
		}
		if (type == -1) {
			AssetRegistry.LOGGER.warn("Loaded animated texture is not an animation sheet: {}", resource);
			return AssetRegistry.ATLAS.addImage(resource, image);
		}
		int frames = -1;
		int[] mapping = null;
		if (data.animationFrameMapping != null && data.animationFrameMapping.length > 0) {
			mapping = Arrays.stream(data.animationFrameMapping).mapToInt(Integer::intValue).toArray();
		} else if (type == 0) {
			frames = image.getWidth() / image.getHeight();
		} else {
			frames = image.getHeight() / image.getWidth();
		}
		return AssetRegistry.ATLAS.addImageAnimated(resource, image, data.animationFps, frames, mapping, type == 0);
	}
}