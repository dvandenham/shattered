package shattered;

import org.jetbrains.annotations.NotNull;
import shattered.lib.IKeyListener;
import shattered.lib.KeyBind;
import shattered.lib.KeyManager;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;

public final class Keybinds implements IKeyListener {

	//Generic Keys
	public static KeyBind genericEscape;

	private Keybinds() {
	}

	@Override
	public void onKeybindChanged(@NotNull final KeyBind keybind) {
	}

	private static void initGeneric(@NotNull final KeyManager manager) {
		Keybinds.genericEscape = manager.addToggle("generic.escape", "key.generic.escape", GLFW_KEY_ESCAPE);
	}

	static void init(@NotNull final KeyManager manager) {
		manager.registerListener(new Keybinds());
		Keybinds.initGeneric(manager);
	}
}