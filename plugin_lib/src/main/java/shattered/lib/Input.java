package shattered.lib;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;
import shattered.lib.math.Point;
import shattered.lib.math.Rectangle;

@SuppressWarnings({"unused", "ConstantConditions"})
public final class Input {

	private static final IInput INSTANCE = null;

	private Input() {
	}

	public static void restrictMouseInput(@Nullable final Rectangle bounds) {
		Input.INSTANCE.restrictMouseInput(bounds);
	}

	public static void releaseMouseRestriction() {
		Input.INSTANCE.releaseMouseRestriction();
	}

	public static boolean isMouseAllowed() {
		return Input.isMouseAllowed(Input.getMouseX(), Input.getMouseY());
	}

	public static boolean isMouseAllowed(final int x, final int y) {
		return Input.INSTANCE.isMouseAllowed(x, y);
	}

	public static boolean isMouseAllowed(@NotNull final Point position) {
		return Input.isMouseAllowed(position.getX(), position.getY());
	}

	public static int getMouseX() {
		return Input.INSTANCE.getMouseX();
	}

	public static int getMouseY() {
		return Input.INSTANCE.getMouseY();
	}

	public static boolean isMouseLeftPressed() {
		return Input.INSTANCE.isMouseLeftPressed();
	}

	public static boolean isMouseLeftClicked() {
		return Input.INSTANCE.isMouseLeftClicked();
	}

	public static boolean isMouseRightPressed() {
		return Input.INSTANCE.isMouseRightPressed();
	}

	public static boolean isMouseRightClicked() {
		return Input.INSTANCE.isMouseRightClicked();
	}

	public static boolean isMouseDragging() {
		return Input.INSTANCE.isMouseDragging();
	}

	public static int getDraggedDX() {
		return Input.INSTANCE.getDraggedDX();
	}

	public static int getDraggedDY() {
		return Input.INSTANCE.getDraggedDY();
	}

	public static boolean isKeyboardShiftDown() {
		return Input.isKeyDown(GLFW.GLFW_KEY_LEFT_SHIFT) || Input.isKeyDown(GLFW.GLFW_KEY_RIGHT_SHIFT);
	}

	public static boolean isKeyboardControlDown() {
		return Input.isKeyDown(GLFW.GLFW_KEY_LEFT_CONTROL) || Input.isKeyDown(GLFW.GLFW_KEY_RIGHT_CONTROL);
	}

	public static boolean isKeyboardMetaDown() {
		return Input.isKeyDown(GLFW.GLFW_KEY_LEFT_SUPER) || Input.isKeyDown(GLFW.GLFW_KEY_RIGHT_SUPER);
	}

	public static boolean isKeyboardAltDown() {
		return Input.isKeyDown(GLFW.GLFW_KEY_LEFT_ALT) || Input.isKeyDown(GLFW.GLFW_KEY_RIGHT_ALT);
	}

	public static boolean isKeyboardEscDown() {
		return Input.isKeyDown(GLFW.GLFW_KEY_ESCAPE);
	}

	public static boolean isKeyboardSpaceDown() {
		return Input.isKeyDown(GLFW.GLFW_KEY_SPACE);
	}

	public static boolean isKeyDown(final int keyCode) {
		return Input.INSTANCE.isKeyDown(keyCode);
	}

	public static boolean containsMouse(final int x, final int y, final int width, final int height) {
		return Input.containsMouse(Rectangle.create(x, y, width, height));
	}

	public static boolean containsMouse(@Nullable final Rectangle bounds) {
		return Input.INSTANCE.containsMouse(bounds);
	}

	public static void setMouseBlocked(final boolean blocked) {
		Input.INSTANCE.setMouseBlocked(blocked);
	}
}