package shattered.lib.asset;

import java.awt.Color;
import java.awt.FontFormatException;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import shattered.lib.ResourceLocation;

final class FontLoader {

	private final AtlasStitcher atlas = new AtlasStitcher(true);
	private final java.awt.Font font;
	private final int size;

	private FontLoader(@NotNull final java.awt.Font font, final int size) {
		this.font = font;
		this.size = size;
	}

	@NotNull
	public Font create() {
		final Font result = new Font(this.atlas, this.size);
		for (int i = 0; i < 256; ++i) {
			final char character = (char) i;
			final BufferedImage charImage = this.createCharImage(character);
			final ResourceLocation charResource = new ResourceLocation("font", String.valueOf(System.nanoTime()), "character");
			final TextureAtlasDefault texture = this.atlas.addImage(charResource, charImage);
			result.charMap.put(character, texture.atlasId);
			result.charTextures.put(texture.atlasId, texture);
		}
		return result;
	}

	@NotNull
	private BufferedImage createCharImage(final char character) {
		Graphics2D graphics = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB).createGraphics();
		graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		graphics.setFont(this.font);
		final FontMetrics metrics = graphics.getFontMetrics();
		final int charWidth = metrics.charWidth(character) <= 0 ? 1 : metrics.charWidth(character);
		final int charHeight = metrics.getHeight() <= 0 ? this.font.getSize() : metrics.getHeight();
		final BufferedImage charImage = new BufferedImage(charWidth, charHeight, BufferedImage.TYPE_INT_ARGB);
		graphics = charImage.createGraphics();
		graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		graphics.setFont(this.font);
		graphics.setColor(Color.white);
		graphics.drawString(String.valueOf(character), 0, metrics.getAscent());
		graphics.dispose();
		return charImage;
	}

	@NotNull
	public static Font create(@NotNull final java.awt.Font font, final int size) {
		return new FontLoader(font.deriveFont((float) size), size).create();
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
}