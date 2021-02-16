package shattered.lib.gfx;

import org.jetbrains.annotations.NotNull;
import org.joml.Vector4f;
import shattered.lib.math.Rectangle;
import static org.lwjgl.opengl.GL11.GL_LINEAR;
import static org.lwjgl.opengl.GL11.GL_NEAREST;
import static org.lwjgl.opengl.GL11.GL_NO_ERROR;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MAG_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MIN_FILTER;
import static org.lwjgl.opengl.GL11.glGetError;
import static org.lwjgl.opengl.GL11.glScissor;
import static org.lwjgl.opengl.GL11.glTexParameteri;

public final class GLHelper {

	private static final Vector4f SCISSOR_TRANSFORM = new Vector4f(0, 0, 1, 1);
	private static boolean isSmoothing = false;

	private GLHelper() {
	}

	public static void checkError(@NotNull final String msg) {
		final int result = glGetError();
		if (result != GL_NO_ERROR) {
			throw new RuntimeException(String.format("[%s]%s", result, msg));
		}
	}

	public static void enableSmoothing() {
		if (GLHelper.isSmoothing) {
			return;
		}
		GLHelper.isSmoothing = true;
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
	}

	public static void disableSmoothing() {
		if (!GLHelper.isSmoothing) {
			return;
		}
		GLHelper.isSmoothing = false;
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
	}

	public static void scaleScissor(final float scaleX, final float scaleY) {
		GLHelper.SCISSOR_TRANSFORM.z = scaleX;
		GLHelper.SCISSOR_TRANSFORM.w = scaleY;
	}

	public static void translateScissor(final int x, final int y) {
		GLHelper.SCISSOR_TRANSFORM.x = x;
		GLHelper.SCISSOR_TRANSFORM.y = y;
	}

	public static void resetScissor() {
		GLHelper.translateScissor(0, 0);
		GLHelper.scaleScissor(1, 1);
	}

	public static void scissor(@NotNull final Rectangle bounds) {
		GLHelper.scissor(bounds.getX(), bounds.getY(), bounds.getWidth(), bounds.getHeight());
	}

	public static void scissor(final int x, final int y, final int width, final int height) {
		final int newX = (int) GLHelper.SCISSOR_TRANSFORM.x + x;
		final int newY = (int) GLHelper.SCISSOR_TRANSFORM.y + y;
		final int newWidth = (int) Math.ceil(GLHelper.SCISSOR_TRANSFORM.z * width);
		final int newHeight = (int) Math.ceil(GLHelper.SCISSOR_TRANSFORM.w * height);
		glScissor(newX, Display.getHeight() - newHeight - newY, newWidth, newHeight);
	}

	public static void disableScissor() {
		glScissor(0, 0, Display.getWidth(), Display.getHeight());
	}
}