package shattered;

import shattered.lib.config.ConfigManager;
import shattered.lib.math.Dimension;

public final class Config {

	public static final ConfigManager.ConfigDimension DISPLAY_SIZE
			= ConfigManager.register("display_size", Dimension.create(800, 600));
	public static final ConfigManager.ConfigBoolean DISPLAY_VSYNC
			= ConfigManager.register("display_vsync", true);

	public static final ConfigManager.ConfigBoolean GLOBAL_BOOT_ANIMATION
			= ConfigManager.register("global_boot_animation", true);
}