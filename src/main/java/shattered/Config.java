package shattered;

import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import shattered.lib.json.Json;
import shattered.lib.json.JsonUtils;
import shattered.lib.math.Dimension;

@Json
public final class Config {

	public Display display = new Display();

	public static class Display {

		public Dimension size = Dimension.createMutable(800, 600);

		public boolean vsync = true;
	}

	private static Config INSTANCE = new Config();

	public static Config getInstance() {
		return Config.INSTANCE;
	}

	static void load() {
		try {
			Config.INSTANCE = JsonUtils.deserialize(Shattered.WORKSPACE.getDataFile("config.db"), Config.class);
		} catch (final FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public static void save() {
		try (final FileWriter writer = new FileWriter(Shattered.WORKSPACE.getDataFile("config.db"))) {
			JsonUtils.GSON.toJson(Config.getInstance(), writer);
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}
}