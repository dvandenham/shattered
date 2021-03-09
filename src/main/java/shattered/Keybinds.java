package shattered;

import shattered.lib.IKeyListener;
import shattered.lib.KeyBind;
import shattered.lib.KeyManager;
import org.jetbrains.annotations.NotNull;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_F2;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_SPACE;

public final class Keybinds implements IKeyListener {

	//Generic Keys
	public static KeyBind genericEscape;
	//Internal keys
	public static KeyBind internalOpenWorkspace;
	//Game Keys
	public static KeyBind gamePause;
	public static KeyBind gameJump;

	private Keybinds() {
	}

	@Override
	public void onKeybindChanged(@NotNull final KeyBind keybind) {
		if (keybind == Keybinds.internalOpenWorkspace) {
			Shattered.WORKSPACE.openExternal();
		} else if (keybind == Keybinds.genericEscape) {
			if (!Shattered.getInstance().getGameManager().isRunning()) {
				Shattered.getInstance().getGuiManager().closeLastScreen();
			}
		}
	}

	private static void initGeneric(@NotNull final KeyManager manager) {
		Keybinds.genericEscape = manager.addToggle("generic.escape", "key.generic.escape", GLFW_KEY_ESCAPE);
	}

	private static void initInternal(@NotNull final KeyManager manager) {
		Keybinds.internalOpenWorkspace = manager.addToggle("internal.open_workspace", "key.internal.open_workspace", GLFW_KEY_F2, true, true, false);
	}

	private static void initGame(@NotNull final KeyManager manager) {
		Keybinds.gamePause = manager.addToggle("game.pause", "key.game.pause", GLFW_KEY_ESCAPE);
		Keybinds.gameJump = manager.addRepeating("game.jump", "key.game.jump", GLFW_KEY_SPACE, 10);
	}

	static void init(@NotNull final KeyManager manager) {
		manager.registerListener(new Keybinds());
		Keybinds.initGeneric(manager);
		Keybinds.initInternal(manager);
		Keybinds.initGame(manager);
	}
}