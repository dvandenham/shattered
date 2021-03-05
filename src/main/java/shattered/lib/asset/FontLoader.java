package shattered.lib.asset;

import java.awt.Color;
import java.awt.FontFormatException;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.Locale;
import java.util.UUID;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import shattered.Shattered;
import shattered.lib.ResourceLocation;

final class FontLoader {

	private FontLoader() {
	}

	@Nullable
	public static java.awt.Font loadFontAwt(@NotNull final ResourceLocation resource) {
		final String path = AssetRegistry.getResourcePath(resource, AssetTypes.FONT, "ttf");
		final URL location = AssetRegistry.getPathUrl(path);
		if (location == null) {
			AssetRegistry.LOGGER.error("Registered font \"{}\" does not exist!", resource);
			AssetRegistry.LOGGER.error("\tExpected filepath: {}", path);
			return null;
		}
		try {
			return java.awt.Font.createFont(java.awt.Font.TRUETYPE_FONT, location.openStream()).deriveFont((float) 64);
		} catch (final FontFormatException | IOException e) {
			return null;
		}
	}

	@NotNull
	public static Font createFont(@NotNull final ResourceLocation resource, @NotNull final java.awt.Font awtFont) {
		final AtlasStitcher atlas = new AtlasStitcher(true);
		final Font result = new Font(resource, atlas);
		for (int i = 0; i < Font.DEFAULT_SIZES.length; ++i) {
			final int fontSize = Font.DEFAULT_SIZES[i];
			final java.awt.Font scaledFont = awtFont.deriveFont((float) fontSize);
			for (char j = 0; j < 256; ++j) {
				final BufferedImage charImage = FontLoader.createCharImage(scaledFont, j);
				final ResourceLocation charResource = new ResourceLocation(
						Shattered.NAME.toLowerCase(Locale.ROOT),
						System.nanoTime() + UUID.randomUUID().toString(),
						"character"
				);
				final TextureAtlasDefault texture = atlas.addImage(charResource, charImage);
				result.charMaps[i].put(j, texture.atlasId);
				result.charTextures.put(texture.atlasId, texture);
			}
		}
		return result;
	}

	@NotNull
	private static BufferedImage createCharImage(@NotNull final java.awt.Font awtFont, final char character) {
		Graphics2D graphics = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB).createGraphics();
		graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		graphics.setFont(awtFont);
		final FontMetrics metrics = graphics.getFontMetrics();
		final int charWidth = metrics.charWidth(character) <= 0 ? 1 : metrics.charWidth(character);
		final int charHeight = metrics.getHeight() <= 0 ? awtFont.getSize() : metrics.getHeight();
		final BufferedImage charImage = new BufferedImage(charWidth, charHeight, BufferedImage.TYPE_INT_ARGB);
		graphics = charImage.createGraphics();
		graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		graphics.setFont(awtFont);
		graphics.setColor(Color.white);
		graphics.drawString(String.valueOf(character), 0, metrics.getAscent());
		graphics.dispose();
		return charImage;
	}
}