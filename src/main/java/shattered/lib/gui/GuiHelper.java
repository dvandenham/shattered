package shattered.lib.gui;

import shattered.Assets;
import shattered.lib.Color;
import shattered.lib.gfx.Display;
import shattered.lib.gfx.RenderHelper;
import shattered.lib.gfx.Tessellator;
import shattered.lib.math.Dimension;
import shattered.lib.math.MathHelper;
import shattered.lib.math.Rectangle;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class GuiHelper {

	private static final int DEFAULT_BOUNDS_MODIFIER = -1;
	//Screen
	static final Dimension BOUNDS_IGNORE = Dimension.create(GuiHelper.DEFAULT_BOUNDS_MODIFIER, GuiHelper.DEFAULT_BOUNDS_MODIFIER);
	static final Rectangle BOUNDS_FULLSCREEN = Rectangle.create(GuiHelper.DEFAULT_BOUNDS_MODIFIER, GuiHelper.DEFAULT_BOUNDS_MODIFIER, GuiHelper.BOUNDS_IGNORE);
	//Props
	public static final int TITLEBAR_SIZE = 48;
	public static final int CLOSE_BUTTON_WIDTH = GuiHelper.TITLEBAR_SIZE;
	public static final int CLOSE_BUTTON_HEIGHT = GuiHelper.TITLEBAR_SIZE;
	public static final int BORDER_SIZE = 4;

	public static final int COMPONENT_HEIGHT = 48;
	public static final int COMPONENT_SPACING = 8;

	private GuiHelper() {
	}

	static Rectangle getCorrectBounds(@NotNull final Rectangle bounds, @NotNull final Dimension sizeMin, @NotNull final Dimension sizeMax) {
		int width = bounds.getWidth();
		if (width == GuiHelper.DEFAULT_BOUNDS_MODIFIER) {
			width = Display.getWidth();
		} else if (width < 0) {
			width = Display.getWidth() / -width;
		} else if (width == 0 || width > Display.getWidth()) {
			width = Display.getWidth();
		}
		if (sizeMin.getWidth() != GuiHelper.DEFAULT_BOUNDS_MODIFIER || sizeMax.getWidth() != GuiHelper.DEFAULT_BOUNDS_MODIFIER) {
			final int minWidth = sizeMin.getWidth() == GuiHelper.DEFAULT_BOUNDS_MODIFIER ? 0 : sizeMin.getWidth();
			final int maxWidth = sizeMax.getWidth() == GuiHelper.DEFAULT_BOUNDS_MODIFIER ? Display.getWidth() : sizeMax.getWidth();
			width = MathHelper.clamp(width, minWidth, maxWidth);
		}

		int height = bounds.getHeight();
		if (height == GuiHelper.DEFAULT_BOUNDS_MODIFIER) {
			height = Display.getHeight();
		} else if (height < 0) {
			height = Display.getHeight() / -height;
		} else if (height == 0 || height > Display.getHeight()) {
			height = Display.getHeight();
		}
		if (sizeMin.getHeight() != GuiHelper.DEFAULT_BOUNDS_MODIFIER || sizeMax.getHeight() != GuiHelper.DEFAULT_BOUNDS_MODIFIER) {
			final int minHeight = sizeMin.getHeight() == GuiHelper.DEFAULT_BOUNDS_MODIFIER ? 0 : sizeMin.getHeight();
			final int maxHeight = sizeMax.getHeight() == GuiHelper.DEFAULT_BOUNDS_MODIFIER ? Display.getHeight() : sizeMax.getHeight();
			height = MathHelper.clamp(height, minHeight, maxHeight);
		}

		final int availableX = Display.getWidth() - width;
		final int availableY = Display.getHeight() - height;

		int x = width == Display.getWidth() ? 0 : bounds.getX();
		if (x < 0) {
			x = availableX / 2;
		} else if (x > availableX) {
			x = availableX;
		}

		int y = height == Display.getHeight() ? 0 : bounds.getY();
		if (y < 0) {
			y = availableY / 2;
		} else if (y > availableY) {
			y = availableY;
		}

		return Rectangle.create(x, y, width, height);
	}

	static Layout createDefaultLayout(@NotNull final IGuiScreen screen) {
		return new DefaultLayout(GuiHelper.COMPONENT_HEIGHT, GuiHelper.COMPONENT_SPACING, screen.getInternalBounds());
	}

	public static void renderGuiPanel(@NotNull final Tessellator tessellator, @NotNull final Rectangle bounds, @Nullable final Color overlayColor) {
		RenderHelper.renderBorderTexture(tessellator, bounds, Assets.TEXTURE_GUI_PANEL, 5, overlayColor);
	}

	public static void renderGuiPanel(@NotNull final Tessellator tessellator, @NotNull final Rectangle bounds) {
		RenderHelper.renderBorderTexture(tessellator, bounds, Assets.TEXTURE_GUI_PANEL, 5);
	}
}