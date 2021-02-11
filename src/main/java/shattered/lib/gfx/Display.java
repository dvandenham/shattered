package shattered.lib.gfx;

import org.jetbrains.annotations.NotNull;
import shattered.Shattered;
import shattered.lib.math.Dimension;
import shattered.lib.math.Rectangle;

public final class Display {

	private Display() {
	}

	//TODO implement fullscreen

	//	public static void toggleFullscreen() {
	//		GLFWSetup.ToggleFullscreen(!Display.isFullscreen());
	//	}

	public static void setLogicalResolution(final int width, final int height) {
		GLFWSetup.setScaledResolution(width, height, Display.getActiveShader());
	}

	public static void resetLogicalResolution() {
		GLFWSetup.setScaledResolution(1, Display.getActiveShader());
	}

	private static Shader getActiveShader() {
		return ((TessellatorImpl) Shattered.getInstance().tessellator).getShader();
	}

	//TODO implement fullscreen

	//	public static boolean isFullscreen() {
	//		return GLFWSetup.IsFullscreen;
	//	}

	public static long getWindowId() {
		return GLFWSetup.windowId;
	}

	public static int getWidth() {
		final double scale = GLFWSetup.scale;
		if (scale == 1) {
			return GLFWSetup.DISPLAY_SIZE.getWidth();
		}
		return (int) (GLFWSetup.DISPLAY_SIZE.getWidth() / scale);
	}

	public static int getHeight() {
		final double scale = GLFWSetup.scale;
		if (scale == 1) {
			return GLFWSetup.DISPLAY_SIZE.getHeight();
		}
		return (int) (GLFWSetup.DISPLAY_SIZE.getHeight() / scale);
	}

	@NotNull
	public static Dimension getSize() {
		return Dimension.create(Display.getWidth(), Display.getHeight());
	}

	@NotNull
	public static Dimension getPhysicalSize() {
		return GLFWSetup.DISPLAY_SIZE.toImmutable();
	}

	@NotNull
	public static Rectangle getBounds() {
		return Rectangle.create(0, 0, Display.getSize());
	}
}