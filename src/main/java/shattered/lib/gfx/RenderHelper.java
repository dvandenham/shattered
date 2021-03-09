package shattered.lib.gfx;

import shattered.lib.Color;
import shattered.lib.ResourceLocation;
import shattered.lib.asset.Texture;
import shattered.lib.math.Dimension;
import shattered.lib.math.Point;
import shattered.lib.math.Rectangle;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class RenderHelper {

	private RenderHelper() {
	}

	public static void writeGlitched(@NotNull final FontRenderer fontRenderer, @NotNull final Point position, @NotNull final StringData data) {
		final Point immutablePos = position.toImmutable();
		fontRenderer.start();
		fontRenderer.add(immutablePos.moveX(-4), data.withColor(Color.RED));
		fontRenderer.add(immutablePos.moveX(4), data.withColor(Color.XEROS));
		fontRenderer.add(immutablePos, data.withColor(Color.WHITE));
		fontRenderer.write();
	}

	public static void writeGlitched(@NotNull final FontRenderer fontRenderer, final int x, final int y, @NotNull final StringData data) {
		fontRenderer.start();
		fontRenderer.add(x - 4, y, data.withColor(Color.RED));
		fontRenderer.add(x + 4, y, data.withColor(Color.XEROS));
		fontRenderer.add(x, y, data.withColor(Color.WHITE));
		fontRenderer.write();
	}

	public static void writeGlitched(@NotNull final FontRenderer fontRenderer, @NotNull final Point position, @NotNull final String text) {
		final Point immutablePos = position.toImmutable();
		fontRenderer.start();
		fontRenderer.add(immutablePos.moveX(-4), text, Color.RED);
		fontRenderer.add(immutablePos.moveX(4), text, Color.XEROS);
		fontRenderer.add(immutablePos, text, Color.WHITE);
		fontRenderer.write();
	}

	public static void writeGlitched(@NotNull final FontRenderer fontRenderer, final int x, final int y, @NotNull final String text) {
		fontRenderer.start();
		fontRenderer.add(x - 4, y, text, Color.RED);
		fontRenderer.add(x + 4, y, text, Color.XEROS);
		fontRenderer.add(x, y, text, Color.WHITE);
		fontRenderer.write();
	}

	public static void writeGlitchedCentered(@NotNull final FontRenderer fontRenderer, @NotNull final Rectangle bounds, @NotNull final StringData data) {
		final Rectangle immutableBounds = bounds.toImmutable();
		fontRenderer.start();
		fontRenderer.addCentered(immutableBounds.moveX(-4), data.withColor(Color.RED));
		fontRenderer.addCentered(immutableBounds.moveX(4), data.withColor(Color.XEROS));
		fontRenderer.addCentered(immutableBounds, data.withColor(Color.WHITE));
		fontRenderer.write();
	}

	public static void writeGlitchedCentered(@NotNull final FontRenderer fontRenderer, @NotNull final Point position, @NotNull final Dimension centerSize, @NotNull final StringData data) {
		final Point immutablePos = position.toImmutable();
		fontRenderer.start();
		fontRenderer.addCentered(immutablePos.moveX(-4), centerSize, data.withColor(Color.RED));
		fontRenderer.addCentered(immutablePos.moveX(4), centerSize, data.withColor(Color.XEROS));
		fontRenderer.addCentered(immutablePos, centerSize, data.withColor(Color.WHITE));
		fontRenderer.write();
	}

	public static void writeGlitchedCentered(@NotNull final FontRenderer fontRenderer, final int x, final int y, @NotNull final Dimension centerSize, @NotNull final StringData data) {
		fontRenderer.start();
		fontRenderer.addCentered(x - 4, y, centerSize, data.withColor(Color.RED));
		fontRenderer.addCentered(x + 4, y, centerSize, data.withColor(Color.XEROS));
		fontRenderer.addCentered(x, y, centerSize, data.withColor(Color.WHITE));
		fontRenderer.write();
	}

	public static void writeGlitchedCentered(@NotNull final FontRenderer fontRenderer, @NotNull final Point position, final int centerWidth, final int centerHeight, @NotNull final StringData data) {
		final Point immutablePos = position.toImmutable();
		fontRenderer.start();
		fontRenderer.addCentered(immutablePos.moveX(-4), centerWidth, centerHeight, data.withColor(Color.RED));
		fontRenderer.addCentered(immutablePos.moveX(4), centerWidth, centerHeight, data.withColor(Color.XEROS));
		fontRenderer.addCentered(immutablePos, centerWidth, centerHeight, data.withColor(Color.WHITE));
		fontRenderer.write();
	}

	public static void writeGlitchedCentered(@NotNull final FontRenderer fontRenderer, final int x, final int y, final int centerWidth, final int centerHeight, @NotNull final StringData data) {
		fontRenderer.start();
		fontRenderer.addCentered(x - 4, y, centerWidth, centerHeight, data.withColor(Color.RED));
		fontRenderer.addCentered(x + 4, y, centerWidth, centerHeight, data.withColor(Color.XEROS));
		fontRenderer.addCentered(x, y, centerWidth, centerHeight, data.withColor(Color.WHITE));
		fontRenderer.write();
	}

	public static void writeGlitchedCentered(@NotNull final FontRenderer fontRenderer, @NotNull final Rectangle bounds, @NotNull final String text) {
		final Rectangle immutableBounds = bounds.toImmutable();
		fontRenderer.start();
		fontRenderer.addCentered(immutableBounds.moveX(-4), text, Color.RED);
		fontRenderer.addCentered(immutableBounds.moveX(4), text, Color.XEROS);
		fontRenderer.addCentered(immutableBounds, text, Color.WHITE);
		fontRenderer.write();
	}

	public static void writeGlitchedCentered(@NotNull final FontRenderer fontRenderer, @NotNull final Point position, @NotNull final Dimension centerSize, @NotNull final String text) {
		final Point immutablePos = position.toImmutable();
		fontRenderer.start();
		fontRenderer.addCentered(immutablePos.moveX(-4), centerSize, text, Color.RED);
		fontRenderer.addCentered(immutablePos.moveX(4), centerSize, text, Color.XEROS);
		fontRenderer.addCentered(immutablePos, centerSize, text, Color.WHITE);
		fontRenderer.write();
	}

	public static void writeGlitchedCentered(@NotNull final FontRenderer fontRenderer, final int x, final int y, final Dimension centerSize, @NotNull final String text) {
		fontRenderer.start();
		fontRenderer.addCentered(x - 4, y, centerSize, text, Color.RED);
		fontRenderer.addCentered(x + 4, y, centerSize, text, Color.XEROS);
		fontRenderer.addCentered(x, y, centerSize, text, Color.WHITE);
		fontRenderer.write();
	}

	public static void writeGlitchedCentered(@NotNull final FontRenderer fontRenderer, @NotNull final Point position, final int centerWidth, final int centerHeight, @NotNull final String text) {
		final Point immutablePos = position.toImmutable();
		fontRenderer.start();
		fontRenderer.addCentered(immutablePos.moveX(-4), centerWidth, centerHeight, text, Color.RED);
		fontRenderer.addCentered(immutablePos.moveX(4), centerWidth, centerHeight, text, Color.XEROS);
		fontRenderer.addCentered(immutablePos, centerWidth, centerHeight, text, Color.WHITE);
		fontRenderer.write();
	}

	public static void writeGlitchedCentered(@NotNull final FontRenderer fontRenderer, final int x, final int y, final int centerWidth, final int centerHeight, @NotNull final String text) {
		fontRenderer.start();
		fontRenderer.addCentered(x - 4, y, centerWidth, centerHeight, text, Color.RED);
		fontRenderer.addCentered(x + 4, y, centerWidth, centerHeight, text, Color.XEROS);
		fontRenderer.addCentered(x, y, centerWidth, centerHeight, text, Color.WHITE);
		fontRenderer.write();
	}

	public static void drawTriangle(@NotNull final Tessellator tessellator, final int x1, final int y1, final int x2, final int y2, final int x3, final int y3, @NotNull final Color color) {
		final PolygonBuilder polygon = tessellator.createPolygon();
		polygon.start(color);
		polygon.add(x1, y1);
		polygon.add(x2, y2);
		polygon.add(x3, y3);
		polygon.draw();
	}

	public static void renderFrame(@NotNull final Tessellator tessellator,
	                               @NotNull final Rectangle bounds,
	                               final int borderSize,
	                               @NotNull final Color color) {
		RenderHelper.renderFrame(tessellator, bounds.getX(), bounds.getY(), bounds.getWidth(), bounds.getHeight(), borderSize, color);
	}

	public static void renderFrame(@NotNull final Tessellator tessellator,
	                               final int x, final int y, final int width, final int height,
	                               final int borderSize,
	                               @NotNull final Color color) {
		tessellator.start();
		tessellator.set(x, y, width, borderSize, color);
		tessellator.next();
		tessellator.set(x, y + borderSize, borderSize, height - borderSize, color);
		tessellator.next();
		tessellator.set(x + width - borderSize, y + borderSize, borderSize, height - borderSize, color);
		tessellator.next();
		tessellator.set(x + borderSize, y + height - borderSize, width - borderSize * 2, borderSize, color);
		tessellator.draw();
	}

	public static void renderBorderTexture(@NotNull final Tessellator tessellator,
	                                       @NotNull final Rectangle bounds,
	                                       @NotNull final ResourceLocation resource,
	                                       final int borderSize) {
		RenderHelper.renderBorderTexture(tessellator, bounds, resource, borderSize, null);
	}

	public static void renderBorderTexture(@NotNull final Tessellator tessellator,
	                                       @NotNull final Rectangle bounds,
	                                       @NotNull final ResourceLocation resource,
	                                       final int size,
	                                       @Nullable final Color overlayColor
	) {
		final Texture texture = ((TessellatorImpl) tessellator).getTexture(resource);
		if (bounds.getWidth() <= texture.getTextureSize().getWidth() && bounds.getHeight() <= texture.getTextureSize().getHeight()) {
			tessellator.drawQuick(bounds, resource);
		} else if (bounds.getWidth() <= texture.getTextureSize().getWidth()) { // Width <= TexWidth && Height > TexHeight
			tessellator.start();
			tessellator.set(bounds.getPosition(), bounds.getWidth(), size, resource);
			if (overlayColor != null) {
				tessellator.color(overlayColor);
			}
			tessellator.vMax(size);
			tessellator.next();
			tessellator.set(bounds.getX(), bounds.getMaxY() - size, bounds.getWidth(), size, resource);
			if (overlayColor != null) {
				tessellator.color(overlayColor);
			}
			tessellator.vMin(texture.getTextureSize().getHeight() - size);
			tessellator.next();
			tessellator.set(bounds.getX(), bounds.getY() + size, bounds.getWidth(), bounds.getHeight() - size * 2, resource);
			if (overlayColor != null) {
				tessellator.color(overlayColor);
			}
			tessellator.vMin(size);
			tessellator.vMax(texture.getTextureSize().getHeight() - size);
			tessellator.draw();
		} else if (bounds.getHeight() <= texture.getTextureSize().getHeight()) { // Width > TexWidth && Height <= TexHeight
			tessellator.start();
			tessellator.set(bounds.getPosition(), size, bounds.getHeight(), resource);
			if (overlayColor != null) {
				tessellator.color(overlayColor);
			}
			tessellator.uMax(size);
			tessellator.next();
			tessellator.set(bounds.getMaxX() - size, bounds.getY(), size, bounds.getHeight(), resource);
			if (overlayColor != null) {
				tessellator.color(overlayColor);
			}
			tessellator.uMin(texture.getTextureSize().getWidth() - size);
			tessellator.next();
			tessellator.set(bounds.getX() + size, bounds.getY(), bounds.getWidth() - size * 2, bounds.getHeight(), resource);
			if (overlayColor != null) {
				tessellator.color(overlayColor);
			}
			tessellator.uMin(size);
			tessellator.uMax(texture.getTextureSize().getWidth() - size);
			tessellator.draw();
		} else { // Width > TexWidth && Height > TexHeight
			tessellator.start();
			//Top Left
			tessellator.set(bounds.getPosition(), size, size, resource);
			if (overlayColor != null) {
				tessellator.color(overlayColor);
			}
			tessellator.uMax(size);
			tessellator.vMax(size);
			//Top
			tessellator.next();
			tessellator.set(bounds.getX() + size, bounds.getY(), bounds.getWidth() - size * 2, size, resource);
			if (overlayColor != null) {
				tessellator.color(overlayColor);
			}
			tessellator.uMin(size);
			tessellator.uMax(texture.getTextureSize().getWidth() - size);
			tessellator.vMax(size);
			//Top Right
			tessellator.next();
			tessellator.set(bounds.getMaxX() - size, bounds.getY(), size, size, resource);
			if (overlayColor != null) {
				tessellator.color(overlayColor);
			}
			tessellator.uMin(texture.getTextureSize().getWidth() - size);
			tessellator.vMax(size);
			//Left
			tessellator.next();
			tessellator.set(bounds.getX(), bounds.getY() + size, size, bounds.getHeight() - size * 2, resource);
			if (overlayColor != null) {
				tessellator.color(overlayColor);
			}
			tessellator.uMax(size);
			tessellator.vMin(size);
			tessellator.vMax(texture.getTextureSize().getHeight() - size);
			//Center
			tessellator.next();
			tessellator.set(bounds.getX() + size, bounds.getY() + size, bounds.getWidth() - size * 2, bounds.getHeight() - size * 2, resource);
			if (overlayColor != null) {
				tessellator.color(overlayColor);
			}
			tessellator.uMin(size);
			tessellator.vMin(size);
			tessellator.uMax(texture.getTextureSize().getWidth() - size);
			tessellator.vMax(texture.getTextureSize().getHeight() - size);
			//Right
			tessellator.next();
			tessellator.set(bounds.getMaxX() - size, bounds.getY() + size, size, bounds.getHeight() - size * 2, resource);
			if (overlayColor != null) {
				tessellator.color(overlayColor);
			}
			tessellator.uMin(texture.getTextureSize().getWidth() - size);
			tessellator.vMin(size);
			tessellator.vMax(texture.getTextureSize().getHeight() - size);
			//Bottom left
			tessellator.next();
			tessellator.set(bounds.getX(), bounds.getMaxY() - size, size, size, resource);
			if (overlayColor != null) {
				tessellator.color(overlayColor);
			}
			tessellator.uMax(size);
			tessellator.vMin(texture.getTextureSize().getHeight() - size);
			//Bottom
			tessellator.next();
			tessellator.set(bounds.getX() + size, bounds.getMaxY() - size, bounds.getWidth() - size * 2, size, resource);
			if (overlayColor != null) {
				tessellator.color(overlayColor);
			}
			tessellator.uMin(size);
			tessellator.vMin(texture.getTextureSize().getHeight() - size);
			tessellator.uMax(texture.getTextureSize().getWidth() - size);
			//Bottom Right
			tessellator.next();
			tessellator.set(bounds.getMaxX() - size, bounds.getMaxY() - size, size, size, resource);
			if (overlayColor != null) {
				tessellator.color(overlayColor);
			}
			tessellator.uMin(texture.getTextureSize().getWidth() - size);
			tessellator.vMin(texture.getTextureSize().getHeight() - size);
			tessellator.draw();
		}
	}
}