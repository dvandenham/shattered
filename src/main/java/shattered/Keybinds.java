package shattered;

import org.jetbrains.annotations.NotNull;
import shattered.lib.IKeyListener;
import shattered.lib.KeyBind;
import shattered.lib.KeyManager;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_F2;

public final class Keybinds implements IKeyListener {

	//Generic Keys
	public static KeyBind genericEscape;
	//Internal keys
	public static KeyBind internalOpenWorkspace;

	private Keybinds() {
	}

	@Override
	public void onKeybindChanged(@NotNull final KeyBind keybind) {
		if (keybind == Keybinds.internalOpenWorkspace) {
			Shattered.WORKSPACE.openExternal();
		}
	}

	private static void initGeneric(@NotNull final KeyManager manager) {
		Keybinds.genericEscape = manager.addToggle("generic.escape", "key.generic.escape", GLFW_KEY_ESCAPE);
	}

	private static void initInternal(@NotNull final KeyManager manager) {
		Keybinds.internalOpenWorkspace = manager.addToggle("internal.open_workspace", "key.internal.open_workspace", GLFW_KEY_F2, true, true, false);
	}

	static void init(@NotNull final KeyManager manager) {
		manager.registerListener(new Keybinds());
		Keybinds.initGeneric(manager);
		Keybinds.initInternal(manager);
	}
}