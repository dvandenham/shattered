package shattered.lib;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2d;
import org.joml.Vector2f;
import org.lwjgl.glfw.GLFW;
import shattered.Shattered;
import shattered.lib.event.EventBusSubscriber;
import shattered.lib.event.MessageEvent;
import shattered.lib.event.MessageListener;
import shattered.lib.gfx.Display;
import shattered.lib.math.Rectangle;

@SuppressWarnings("unused")
@EventBusSubscriber("SYSTEM")
public final class InputImpl implements IInput {

	private static final InputImpl INSTANCE = new InputImpl();
	private static final long MOUSE_CLICK_TIMEOUT_MS = 150;

	private final Vector2d mousePos = new Vector2d();
	private final Vector2d mousePosPrev = new Vector2d();
	private final Vector2f mousePosDisplay = new Vector2f();
	//Save mouse states and positions for "clicks"
	private boolean mouseLeftDown = false;
	private boolean mouseRightDown = false;
	private boolean mouseLeftRelease = false;
	private boolean mouseRightRelease = false;
	private int mouseLeftDownX = -1;
	private int mouseLeftDownY = -1;
	private int mouseRightDownX = -1;
	private int mouseRightDownY = -1;
	private long mouseLeftClickPressTime = -1;
	private long mouseRightClickPressTime = -1;

	private Rectangle mouseAccepted = null;
	private boolean mouseBlocked = false;
	private boolean mouseLeft = false;
	private boolean mouseRight = false;
	private boolean mouseEntered = false;

	private void poll() {
		this.mousePosDisplay.set(0, 0);
		if (this.mousePosPrev.x > 0 && this.mousePosPrev.y > 0 && this.mouseEntered) {
			final double deltaX = this.mousePos.x - this.mousePosPrev.x;
			final double deltaY = this.mousePos.y - this.mousePosPrev.y;
			if (deltaX != 0) {
				this.mousePosDisplay.y = (float) deltaX;
			}
			if (deltaY != 0) {
				this.mousePosDisplay.x = (float) deltaY;
			}
		}
		this.mousePosPrev.set(this.mousePos);
		this.mouseLeftRelease = (Shattered.getSystemTime() - this.mouseLeftClickPressTime <= InputImpl.MOUSE_CLICK_TIMEOUT_MS) &&
				!this.mouseLeft && this.mouseLeftDown && this.checkClickValid(true);
		this.mouseRightRelease = (Shattered.getSystemTime() - this.mouseRightClickPressTime <= InputImpl.MOUSE_CLICK_TIMEOUT_MS) &&
				!this.mouseRight && this.mouseRightDown && this.checkClickValid(false);
		if (this.mouseLeft && !this.mouseLeftRelease) {
			this.mouseLeftDown = true;
			if (this.mouseLeftClickPressTime == -1) {
				this.mouseLeftClickPressTime = Shattered.getSystemTime();
			}
			if (this.isNotSetup(true)) {
				this.mouseLeftDownX = this.getMouseX();
				this.mouseLeftDownY = this.getMouseY();
			}
		} else {
			this.mouseLeftDown = false;
			this.mouseLeftClickPressTime = -1;
			this.mouseLeftDownX = -1;
			this.mouseLeftDownY = -1;
		}
		if (this.mouseRight && !this.mouseRightRelease) {
			this.mouseRightDown = true;
			if (this.mouseRightClickPressTime == -1) {
				this.mouseRightClickPressTime = Shattered.getSystemTime();
			}
			if (this.isNotSetup(false)) {
				this.mouseRightDownX = this.getMouseX();
				this.mouseRightDownY = this.getMouseY();
			}
		} else {
			this.mouseRightDown = false;
			this.mouseRightClickPressTime = -1;
			this.mouseRightDownX = -1;
			this.mouseRightDownY = -1;
		}
	}

	private boolean checkClickValid(final boolean leftButton) {
		if (this.isNotSetup(leftButton)) {
			return false;
		}
		final int x = (leftButton ? this.mouseLeftDownX : this.mouseRightDownX) - 5;
		final int y = (leftButton ? this.mouseLeftDownY : this.mouseRightDownY) - 5;
		return Rectangle.create(x, y, 10, 10).contains(this.getMouseX(), this.getMouseY());
	}

	private boolean isNotSetup(final boolean leftButton) {
		return (leftButton ? this.mouseLeftDownX : this.mouseRightDownX) == -1 && (leftButton ? this.mouseLeftDownY : this.mouseRightDownY) == -1;
	}

	@Override
	public void restrictMouseInput(@Nullable final Rectangle bounds) {
		this.mouseAccepted = bounds;
	}

	@Override
	public void releaseMouseRestriction() {
		this.mouseAccepted = null;
	}

	public boolean isMouseAllowed() {
		return Input.isMouseAllowed(Input.getMouseX(), Input.getMouseY());
	}

	@Override
	public boolean isMouseAllowed(final int x, final int y) {
		return !this.mouseBlocked && (this.mouseAccepted == null || this.mouseAccepted.contains(x, y));
	}

	@Override
	public int getMouseX() {
		return (int) this.mousePos.x;
	}

	@Override
	public int getMouseY() {
		return (int) this.mousePos.y;
	}

	@Override
	public boolean isMouseLeftPressed() {
		return this.isMouseAllowed() && this.mouseLeft;
	}

	@Override
	public boolean isMouseLeftClicked() {
		return this.isMouseAllowed() && this.mouseLeftRelease;
	}

	@Override
	public boolean isMouseRightPressed() {
		return this.isMouseAllowed() && this.mouseRight;
	}

	@Override
	public boolean isMouseRightClicked() {
		return this.isMouseAllowed() && this.mouseRightRelease;
	}

	@Override
	public boolean isMouseDragging() {
		return this.isMouseAllowed() && this.mouseLeftDown && !this.mouseLeftRelease;
	}

	@Override
	public int getDraggedDX() {
		return this.isMouseDragging() ? this.getMouseX() - this.mouseLeftDownX : 0;
	}

	@Override
	public int getDraggedDY() {
		return this.isMouseDragging() ? this.getMouseY() - this.mouseLeftDownY : 0;
	}


	@Override
	public boolean isKeyDown(final int keyCode) {
		return GLFW.glfwGetKey(Display.getWindowId(), keyCode) == GLFW.GLFW_TRUE;
	}

	@Override
	public boolean containsMouse(final int x, final int y, final int width, final int height) {
		return this.containsMouse(Rectangle.create(x, y, width, height));
	}

	@Override
	public boolean containsMouse(@Nullable final Rectangle bounds) {
		return bounds != null && (!this.mouseBlocked && bounds.contains(this.getMouseX(), this.getMouseY()));
	}

	@Override
	public void setMouseBlocked(final boolean blocked) {
		this.mouseBlocked = blocked;
	}

	@MessageListener("input_setup")
	private static void onInputSetup(final MessageEvent event) {
		event.setResponse(() -> new Object[]{
				InputImpl.INSTANCE,
				(Runnable) InputImpl.INSTANCE::poll
		});
	}

	@MessageListener("input_setup_callbacks")
	private static void onInputSetupCallbacks(final MessageEvent event) {
		event.setResponse(() -> new Object[]{
				(BiConsumer<Double, Double>) InputImpl.INSTANCE.mousePos::set,
				(Consumer<Boolean>) entered -> InputImpl.INSTANCE.mouseEntered = entered,
				(Consumer<Boolean>) down -> InputImpl.INSTANCE.mouseLeft = down,
				(Consumer<Boolean>) down -> InputImpl.INSTANCE.mouseRight = down,
		});
	}
}