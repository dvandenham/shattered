package shattered;

import shattered.lib.config.ConfigManager;
import shattered.lib.math.Dimension;
import org.lwjgl.glfw.GLFW;

public final class Config {

	public static final ConfigManager.ConfigDimension DISPLAY_SIZE
			= ConfigManager.register("display_size", Dimension.create(800, 600));
	public static final ConfigManager.ConfigBoolean DISPLAY_VSYNC
			= ConfigManager.register("display_vsync", true);

	public static final ConfigManager.ConfigBoolean GLOBAL_BOOT_ANIMATION
			= ConfigManager.register("global_boot_animation", true);

	public static final ConfigManager.ConfigInteger KEY_GAME_LEFT
			= ConfigManager.register("key_game_left", GLFW.GLFW_KEY_A);
	public static final ConfigManager.ConfigInteger KEY_GAME_RIGHT
			= ConfigManager.register("key_game_right", GLFW.GLFW_KEY_D);
}