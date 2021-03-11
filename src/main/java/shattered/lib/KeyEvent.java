package shattered.lib;

import shattered.core.event.Event;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT_ALT;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT_CONTROL;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT_SHIFT;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_RIGHT_ALT;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_RIGHT_CONTROL;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_RIGHT_SHIFT;
import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_REPEAT;

public final class KeyEvent extends Event<Void> {

	private final int keyCode, scanCode, action, mods;

	KeyEvent(final int keyCode, final int scanCode, final int action, final int mods) {
		this.keyCode = keyCode;
		this.scanCode = scanCode;
		this.action = action;
		this.mods = mods;
	}

	public int getKeyCode() {
		return this.keyCode;
	}

	public int getScanCode() {
		return this.scanCode;
	}

	public int getAction() {
		return this.action;
	}

	public int getMods() {
		return this.mods;
	}

	public boolean isPressed() {
		return this.action == GLFW_PRESS;
	}

	public boolean isRepeat() {
		return this.action == GLFW_REPEAT;
	}

	public boolean isShift() {
		return this.keyCode == GLFW_KEY_LEFT_SHIFT || this.keyCode == GLFW_KEY_RIGHT_SHIFT;
	}

	public boolean isCtrl() {
		return this.keyCode == GLFW_KEY_LEFT_CONTROL || this.keyCode == GLFW_KEY_RIGHT_CONTROL;
	}

	public boolean isAlt() {
		return this.keyCode == GLFW_KEY_LEFT_ALT || this.keyCode == GLFW_KEY_RIGHT_ALT;
	}
}