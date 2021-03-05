package shattered.lib;

import org.lwjgl.glfw.GLFW;

public final class KeyEvent {

	private final int keyCode, scanCode, action, mods;

	public KeyEvent(final int keyCode, final int scanCode, final int action, final int mods) {
		this.keyCode = keyCode;
		this.scanCode = scanCode;
		this.action = action;
		this.mods = mods;
	}

	public boolean isPressed() {
		return this.action == GLFW.GLFW_PRESS;
	}

	public boolean isShift() {
		return this.keyCode == GLFW.GLFW_KEY_LEFT_SHIFT || this.keyCode == GLFW.GLFW_KEY_RIGHT_SHIFT;
	}

	public boolean isCtrl() {
		return this.keyCode == GLFW.GLFW_KEY_LEFT_CONTROL || this.keyCode == GLFW.GLFW_KEY_RIGHT_CONTROL;
	}

	public boolean isAlt() {
		return this.keyCode == GLFW.GLFW_KEY_LEFT_ALT || this.keyCode == GLFW.GLFW_KEY_RIGHT_ALT;
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
}