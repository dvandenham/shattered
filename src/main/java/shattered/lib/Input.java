package shattered.lib;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import shattered.Shattered;
import shattered.core.event.EventBusSubscriber;
import shattered.core.event.MessageEvent;
import shattered.core.event.MessageListener;
import shattered.lib.gfx.Display;
import shattered.lib.math.Point;
import shattered.lib.math.Rectangle;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2d;
import org.joml.Vector2f;
import org.lwjgl.glfw.GLFW;

@SuppressWarnings("unused")
@EventBusSubscriber("SYSTEM")
public final class Input {

	private static final int MAX_CLICK_POS_DELTA = 4;
	private static final long MOUSE_CLICK_TIMEOUT_MS = 250;

	private static final ConcurrentLinkedQueue<KeyEvent> KEY_QUEUE = new ConcurrentLinkedQueue<>();
	static final Int2ObjectArrayMap<String> KEY_NAMES = new Int2ObjectArrayMap<>();
	static final Int2ObjectArrayMap<Character[]> KEY_CHARS = new Int2ObjectArrayMap<>();
	private static KeyManager keyManager;
	private static boolean keyManagerBlocked = false;

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
	private static long mouseLeftClickPressTime = -1;
	private static long mouseRightClickPressTime = -1;

	private static Rectangle mouseAccepted = null;
	private static boolean mouseBlocked = false;
	private static boolean mouseLeft = false;
	private static boolean mouseRight = false;
	private static boolean mouseEntered = false;

	static {
		KeyboardHelper.addKeyNames();
		KeyboardHelper.addKeyChars();
	}

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
		Input.mouseLeftRelease = (Shattered.getSystemTime() - Input.mouseLeftClickPressTime <= Input.MOUSE_CLICK_TIMEOUT_MS) &&
				!Input.mouseLeft && Input.mouseLeftDown && Input.checkClickValid(true);
		Input.mouseRightRelease = (Shattered.getSystemTime() - Input.mouseRightClickPressTime <= Input.MOUSE_CLICK_TIMEOUT_MS) &&
				!Input.mouseRight && Input.mouseRightDown && Input.checkClickValid(false);
		if (Input.mouseLeft && !Input.mouseLeftRelease) {
			Input.mouseLeftDown = true;
			if (Input.mouseLeftClickPressTime == -1) {
				Input.mouseLeftClickPressTime = Shattered.getSystemTime();
			}
			if (Input.isNotSetup(true)) {
				Input.mouseLeftDownX = Input.getMouseX();
				Input.mouseLeftDownY = Input.getMouseY();
			}
		} else {
			Input.mouseLeftDown = false;
			Input.mouseLeftClickPressTime = -1;
			Input.mouseLeftDownX = -1;
			Input.mouseLeftDownY = -1;
		}
		if (Input.mouseRight && !Input.mouseRightRelease) {
			Input.mouseRightDown = true;
			if (Input.mouseRightClickPressTime == -1) {
				Input.mouseRightClickPressTime = Shattered.getSystemTime();
			}
			if (Input.isNotSetup(false)) {
				Input.mouseRightDownX = Input.getMouseX();
				Input.mouseRightDownY = Input.getMouseY();
			}
		} else {
			Input.mouseRightDown = false;
			Input.mouseRightClickPressTime = -1;
			Input.mouseRightDownX = -1;
			Input.mouseRightDownY = -1;
		}
		if (!Input.keyManagerBlocked) {
			Input.keyManager.poll();
			Input.KEY_QUEUE.clear();
		}
	}

	private static boolean checkClickValid(final boolean leftButton) {
		if (Input.isNotSetup(leftButton)) {
			return false;
		}
		final int x = (leftButton ? Input.mouseLeftDownX : Input.mouseRightDownX) - Input.MAX_CLICK_POS_DELTA;
		final int y = (leftButton ? Input.mouseLeftDownY : Input.mouseRightDownY) - Input.MAX_CLICK_POS_DELTA;
		return Rectangle.create(x, y, Input.MAX_CLICK_POS_DELTA * 2, Input.MAX_CLICK_POS_DELTA * 2)
				.contains(Input.getMouseX(), Input.getMouseY());
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

	public static int getDraggedStartX() {
		return Input.isMouseDragging() ? Input.mouseLeftDownX : -1;
	}

	public static int getDraggedStartY() {
		return Input.isMouseDragging() ? Input.mouseLeftDownY : -1;
	}

	public static int getDraggedDX() {
		final int startX = Input.getDraggedStartX();
		return startX != -1 ? Input.getMouseX() - startX : 0;
	}

	public static int getDraggedDY() {
		final int startY = Input.getDraggedStartY();
		return startY != -1 ? Input.getMouseY() - startY : 0;
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

	public static boolean isKeyManagerBlocked() {
		return Input.keyManagerBlocked;
	}

	public static void setKeyManagerBlocked(final boolean blocked) {
		Input.keyManagerBlocked = blocked;
	}

	public static boolean hasKeyEventQueued() {
		return !Input.KEY_QUEUE.isEmpty();
	}

	@Nullable
	public static KeyEvent nextQueuedKey() {
		return Input.KEY_QUEUE.poll();
	}

	@NotNull
	public static String getKeyName(final int keyCode) {
		return Input.KEY_NAMES.get(keyCode);
	}

	public static char getKeyChar(final int keyCode, final boolean shiftPressed) {
		final Character[] result = Input.KEY_CHARS.get(keyCode);
		return result == null ? 0 : result[result.length == 2 && shiftPressed ? 1 : 0];
	}

	@MessageListener("input_setup")
	private static void onInputSetup(final MessageEvent event) {
		Input.keyManager = new KeyManager(Shattered.WORKSPACE);
		event.setResponse(() -> new Object[]{Input.keyManager, (Runnable) Input::poll});
	}

	@MessageListener("input_setup_callbacks")
	private static void onInputSetupCallbacks(final MessageEvent event) {
		event.setResponse(() -> new Object[]{
				(BiConsumer<Double, Double>) Input.MOUSE_POS::set,
				(Consumer<Boolean>) entered -> Input.mouseEntered = entered,
				(Consumer<Boolean>) down -> Input.mouseLeft = down,
				(Consumer<Boolean>) down -> Input.mouseRight = down,
				Input.KEY_QUEUE
		});
	}
}