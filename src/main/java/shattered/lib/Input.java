package shattered.lib;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2d;
import org.joml.Vector2f;
import org.lwjgl.glfw.GLFW;
import shattered.core.event.EventBusSubscriber;
import shattered.core.event.MessageEvent;
import shattered.core.event.MessageListener;
import shattered.lib.gfx.Display;
import shattered.lib.math.Point;
import shattered.lib.math.Rectangle;

@SuppressWarnings("unused")
@EventBusSubscriber("SYSTEM")
public final class Input {

	private static final Vector2d MOUSE_POS_PREV = new Vector2d();
	private static final Vector2d MOUSE_POS = new Vector2d();
	private static final Vector2f MOUSE_POS_DISPLAY = new Vector2f();
	//Save mouse states and positions for "clicks"
	private static boolean mouseLeftDown = false;
	private static boolean mouseRightDown = false;
	private static boolean mouseLeftRelease = false;
	private static boolean mouseRightRelease = false;
	private static int mouseLeftDownX = -1;
	private static int mouseLeftDownY = -1;
	private static int mouseRightDownX = -1;
	private static int mouseRightDownY = -1;

	private static Rectangle mouseAccepted = null;
	private static boolean mouseBlocked = false;
	private static boolean mouseLeft = false;
	private static boolean mouseRight = false;
	private static boolean mouseEntered = false;

	private static void poll() {
		Input.MOUSE_POS_DISPLAY.set(0, 0);
		if (Input.MOUSE_POS_PREV.x > 0 && Input.MOUSE_POS_PREV.y > 0 && Input.mouseEntered) {
			final double deltaX = Input.MOUSE_POS.x - Input.MOUSE_POS_PREV.x;
			final double deltaY = Input.MOUSE_POS.y - Input.MOUSE_POS_PREV.y;
			if (deltaX != 0) {
				Input.MOUSE_POS_DISPLAY.y = (float) deltaX;
			}
			if (deltaY != 0) {
				Input.MOUSE_POS_DISPLAY.x = (float) deltaY;
			}
		}
		Input.MOUSE_POS_PREV.set(Input.MOUSE_POS);
		Input.mouseLeftRelease = !Input.mouseLeft && Input.mouseLeftDown && Input.checkClickValid(true);
		Input.mouseRightRelease = !Input.mouseRight && Input.mouseRightDown && Input.checkClickValid(false);
		if (Input.mouseLeft && !Input.mouseLeftRelease) {
			Input.mouseLeftDown = true;
			if (Input.isNotSetup(true)) {
				Input.mouseLeftDownX = Input.getMouseX();
				Input.mouseLeftDownY = Input.getMouseY();
			}
		} else {
			Input.mouseLeftDown = false;
			Input.mouseLeftDownX = -1;
			Input.mouseLeftDownY = -1;
		}
		if (Input.mouseRight && !Input.mouseRightRelease) {
			Input.mouseRightDown = true;
			if (Input.isNotSetup(false)) {
				Input.mouseRightDownX = Input.getMouseX();
				Input.mouseRightDownY = Input.getMouseY();
			}
		} else {
			Input.mouseRightDown = false;
			Input.mouseRightDownX = -1;
			Input.mouseRightDownY = -1;
		}
	}

	private static boolean checkClickValid(final boolean leftButton) {
		if (Input.isNotSetup(leftButton)) {
			return false;
		}
		final int x = (leftButton ? Input.mouseLeftDownX : Input.mouseRightDownX) - 5;
		final int y = (leftButton ? Input.mouseLeftDownY : Input.mouseRightDownY) - 5;
		return Rectangle.create(x, y, 10, 10).contains(Input.getMouseX(), Input.getMouseY());
	}

	private static boolean isNotSetup(final boolean leftButton) {
		return (leftButton ? Input.mouseLeftDownX : Input.mouseRightDownX) == -1 && (leftButton ? Input.mouseLeftDownY : Input.mouseRightDownY) == -1;
	}

	public static void restrictMouseInput(@Nullable final Rectangle bounds) {
		Input.mouseAccepted = bounds;
	}

	public static void releaseMouseRestriction() {
		Input.mouseAccepted = null;
	}

	public static boolean isMouseAllowed() {
		return Input.isMouseAllowed(Input.getMouseX(), Input.getMouseY());
	}

	public static boolean isMouseAllowed(final int x, final int y) {
		return !Input.mouseBlocked && (Input.mouseAccepted == null || Input.mouseAccepted.contains(x, y));
	}

	public static boolean isMouseAllowed(@NotNull final Point position) {
		return Input.isMouseAllowed(position.getX(), position.getY());
	}

	public static int getMouseX() {
		return (int) Input.MOUSE_POS.x;
	}

	public static int getMouseY() {
		return (int) Input.MOUSE_POS.y;
	}

	public static boolean isMouseLeftPressed() {
		return Input.isMouseAllowed() && Input.mouseLeft;
	}

	public static boolean isMouseLeftClicked() {
		return Input.isMouseAllowed() && Input.mouseLeftRelease;
	}

	public static boolean isMouseRightPressed() {
		return Input.isMouseAllowed() && Input.mouseRight;
	}

	public static boolean isMouseRightClicked() {
		return Input.isMouseAllowed() && Input.mouseRightRelease;
	}

	public static boolean isMouseDragging() {
		return Input.isMouseAllowed() && Input.mouseLeftDown && !Input.mouseLeftRelease;
	}

	public static int getDraggedDX() {
		return Input.isMouseDragging() ? Input.getMouseX() - Input.mouseLeftDownX : 0;
	}

	public static int getDraggedDY() {
		return Input.isMouseDragging() ? Input.getMouseY() - Input.mouseLeftDownY : 0;
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
		return GLFW.glfwGetKey(Display.getWindowId(), keyCode) == GLFW.GLFW_TRUE;
	}

	public static boolean containsMouse(final int x, final int y, final int width, final int height) {
		return Input.containsMouse(Rectangle.create(x, y, width, height));
	}

	public static boolean containsMouse(@Nullable final Rectangle bounds) {
		return bounds != null && (!Input.mouseBlocked && bounds.contains(Input.getMouseX(), Input.getMouseY()));
	}

	public static void setMouseBlocked(final boolean blocked) {
		Input.mouseBlocked = blocked;
	}

	@MessageListener("input_setup")
	private static void onInputSetup(final MessageEvent event) {
		event.setResponse(() -> (Runnable) Input::poll);
	}

	@MessageListener("input_setup_callbacks")
	private static void onInputSetupCallbacks(final MessageEvent event) {
		event.setResponse(() -> new Object[]{
				(BiConsumer<Double, Double>) Input.MOUSE_POS::set,
				(Consumer<Boolean>) entered -> Input.mouseEntered = entered,
				(Consumer<Boolean>) down -> Input.mouseLeft = down,
				(Consumer<Boolean>) down -> Input.mouseRight = down,
		});
	}
}