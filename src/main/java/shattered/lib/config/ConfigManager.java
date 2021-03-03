package shattered.lib.config;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;
import org.jetbrains.annotations.NotNull;
import shattered.core.event.EventBusSubscriber;
import shattered.core.event.MessageEvent;
import shattered.core.event.MessageListener;
import shattered.Shattered;
import shattered.lib.Color;
import shattered.lib.FastNamedObjectMap;
import shattered.lib.json.JDB;
import shattered.lib.json.JDBKeyTypes;
import shattered.lib.json.JDBTable;
import shattered.lib.math.Dimension;
import shattered.lib.math.Point;
import shattered.lib.math.Rectangle;

public class ConfigManager {

	private static final FastNamedObjectMap<IOption<?>> PREFERENCES = new FastNamedObjectMap<>();
	private static final File FILE = Shattered.WORKSPACE.getDataFile("config.db");

	public static ConfigBoolean register(@NotNull final String key, final boolean defaultValue) {
		final ConfigBoolean result = new ConfigBoolean(key, defaultValue);
		ConfigManager.PREFERENCES.put(key, result);
		return result;
	}

	public static ConfigInteger register(@NotNull final String key, final int defaultValue) {
		final ConfigInteger result = new ConfigInteger(key, defaultValue);
		ConfigManager.PREFERENCES.put(key, result);
		return result;
	}

	public static ConfigLong register(@NotNull final String key, final long defaultValue) {
		final ConfigLong result = new ConfigLong(key, defaultValue);
		ConfigManager.PREFERENCES.put(key, result);
		return result;
	}

	public static ConfigDouble register(@NotNull final String key, final double defaultValue) {
		final ConfigDouble result = new ConfigDouble(key, defaultValue);
		ConfigManager.PREFERENCES.put(key, result);
		return result;
	}

	public static ConfigCharacter register(@NotNull final String key, final char defaultValue) {
		final ConfigCharacter result = new ConfigCharacter(key, defaultValue);
		ConfigManager.PREFERENCES.put(key, result);
		return result;
	}

	public static ConfigString register(@NotNull final String key, @NotNull final String defaultValue) {
		final ConfigString result = new ConfigString(key, defaultValue);
		ConfigManager.PREFERENCES.put(key, result);
		return result;
	}

	public static ConfigPoint register(@NotNull final String key, @NotNull final Point defaultValue) {
		final ConfigPoint result = new ConfigPoint(key, defaultValue);
		ConfigManager.PREFERENCES.put(key, result);
		return result;
	}

	public static ConfigDimension register(@NotNull final String key, @NotNull final Dimension defaultValue) {
		final ConfigDimension result = new ConfigDimension(key, defaultValue);
		ConfigManager.PREFERENCES.put(key, result);
		return result;
	}

	public static ConfigRectangle register(@NotNull final String key, @NotNull final Rectangle defaultValue) {
		final ConfigRectangle result = new ConfigRectangle(key, defaultValue);
		ConfigManager.PREFERENCES.put(key, result);
		return result;
	}

	public static ConfigColor register(@NotNull final String key, @NotNull final Color defaultValue) {
		final ConfigColor result = new ConfigColor(key, defaultValue);
		ConfigManager.PREFERENCES.put(key, result);
		return result;
	}

	@SuppressWarnings({"RedundantCast", "unchecked"})
	private static void load() {
		if (!ConfigManager.FILE.exists()) {
			ConfigManager.save();
		} else {
			try {
				final JDB store = JDB.read(ConfigManager.FILE);
				boolean dirty = !Arrays.asList(store.getKeyNames()).containsAll(ConfigManager.PREFERENCES.keySet());
				for (final String key : store.getKeyNames()) {
					final IOption<?> option = ConfigManager.PREFERENCES.get(key);
					if (option == null) {
						dirty = true;
					} else {
						((IOption<Object>) option).value = option.deserialize(store);
					}
				}
				if (dirty) {
					ConfigManager.save();
				}
			} catch (final IOException e) {
				Shattered.LOGGER.fatal("Could not read config, loading defaults!", e);
			}
		}
	}

	static void save() {
		try {
			final JDB store = new JDB();
			ConfigManager.PREFERENCES.values().forEach(option -> option.serialize(store));
			store.save(ConfigManager.FILE);
		} catch (final IOException e) {
			Shattered.LOGGER.warn("Could not write config!", e);
		}
	}

	public static final class ConfigBoolean extends IOption<Boolean> {

		ConfigBoolean(@NotNull final String preference, final boolean defaultValue) {
			super(preference, defaultValue);
		}

		@Override
		void serialize(@NotNull final JDB store) {
			store.set(this.preference, this.get());
		}

		@Override
		@NotNull
		Boolean deserialize(@NotNull final JDB store) {
			return store.getBoolean(this.preference);
		}
	}

	public static final class ConfigInteger extends IOption<Integer> {

		ConfigInteger(@NotNull final String preference, final int defaultValue) {
			super(preference, defaultValue);
		}

		@Override
		void serialize(@NotNull final JDB store) {
			store.set(this.preference, this.get());
		}

		@Override
		@NotNull
		Integer deserialize(@NotNull final JDB store) {
			return store.getInteger(this.preference);
		}
	}

	public static final class ConfigLong extends IOption<Long> {

		ConfigLong(@NotNull final String preference, final long defaultValue) {
			super(preference, defaultValue);
		}

		@Override
		void serialize(@NotNull final JDB store) {
			store.set(this.preference, this.get());
		}

		@Override
		@NotNull
		Long deserialize(@NotNull final JDB store) {
			return store.getLong(this.preference);
		}
	}

	public static final class ConfigDouble extends IOption<Double> {

		ConfigDouble(@NotNull final String preference, final double defaultValue) {
			super(preference, defaultValue);
		}

		@Override
		void serialize(@NotNull final JDB store) {
			store.set(this.preference, this.get());
		}

		@Override
		@NotNull
		Double deserialize(@NotNull final JDB store) {
			return store.getDouble(this.preference);
		}
	}

	public static final class ConfigCharacter extends IOption<Character> {

		ConfigCharacter(@NotNull final String preference, final char defaultValue) {
			super(preference, defaultValue);
		}

		@Override
		void serialize(@NotNull final JDB store) {
			store.set(this.preference, this.get());
		}

		@Override
		@NotNull
		Character deserialize(@NotNull final JDB store) {
			return store.getCharacter(this.preference);
		}
	}

	public static final class ConfigString extends IOption<String> {

		ConfigString(@NotNull final String preference, @NotNull final String defaultValue) {
			super(preference, defaultValue);
		}

		@Override
		void serialize(@NotNull final JDB store) {
			store.set(this.preference, this.get());
		}

		@Override
		@NotNull
		String deserialize(@NotNull final JDB store) {
			return Objects.requireNonNull(store.getString(this.preference));
		}
	}

	public static final class ConfigPoint extends IOption<Point> {

		ConfigPoint(@NotNull final String preference, @NotNull final Point defaultValue) {
			super(preference, defaultValue);
		}

		@Override
		void serialize(@NotNull final JDB store) {
			final JDBTable table = store.newTable(this.preference);
			table.set("x", this.get().getX());
			table.set("y", this.get().getY());
		}

		@Override
		@NotNull
		Point deserialize(@NotNull final JDB store) {
			final JDBTable table = store.getTable(this.preference);
			if (table == null) {
				throw new NullPointerException();
			}
			if (!table.hasKey("x", JDBKeyTypes.INTEGER) || !table.hasKey("y", JDBKeyTypes.INTEGER)) {
				throw new NullPointerException();
			}
			return Point.create(table.getInteger("x"), table.getInteger("y"));
		}
	}

	public static final class ConfigDimension extends IOption<Dimension> {

		ConfigDimension(@NotNull final String preference, @NotNull final Dimension defaultValue) {
			super(preference, defaultValue);
		}

		@Override
		void serialize(@NotNull final JDB store) {
			final JDBTable table = store.newTable(this.preference);
			table.set("w", this.get().getWidth());
			table.set("h", this.get().getHeight());
		}

		@Override
		@NotNull
		Dimension deserialize(@NotNull final JDB store) {
			final JDBTable table = store.getTable(this.preference);
			if (table == null) {
				throw new NullPointerException();
			}
			if (!table.hasKey("w", JDBKeyTypes.INTEGER) || !table.hasKey("h", JDBKeyTypes.INTEGER)) {
				throw new NullPointerException();
			}
			return Dimension.create(table.getInteger("w"), table.getInteger("h"));
		}
	}

	public static final class ConfigRectangle extends IOption<Rectangle> {

		ConfigRectangle(@NotNull final String preference, @NotNull final Rectangle defaultValue) {
			super(preference, defaultValue);
		}

		@Override
		void serialize(@NotNull final JDB store) {
			final JDBTable table = store.newTable(this.preference);
			table.set("x", this.get().getX());
			table.set("y", this.get().getY());
			table.set("w", this.get().getWidth());
			table.set("h", this.get().getHeight());
		}

		@Override
		@NotNull
		Rectangle deserialize(@NotNull final JDB store) {
			final JDBTable table = store.getTable(this.preference);
			if (table == null) {
				throw new NullPointerException();
			}
			if (!table.hasKey("x", JDBKeyTypes.INTEGER) || !table.hasKey("y", JDBKeyTypes.INTEGER)) {
				throw new NullPointerException();
			}
			if (!table.hasKey("w", JDBKeyTypes.INTEGER) || !table.hasKey("h", JDBKeyTypes.INTEGER)) {
				throw new NullPointerException();
			}
			return Rectangle.create(table.getInteger("x"), table.getInteger("y"), table.getInteger("w"), table.getInteger("h"));
		}
	}

	public static final class ConfigColor extends IOption<Color> {

		ConfigColor(@NotNull final String preference, @NotNull final Color defaultValue) {
			super(preference, defaultValue);
		}

		@Override
		void serialize(@NotNull final JDB store) {
			final JDBTable table = store.newTable(this.preference);
			table.set("r", this.get().getRedByte());
			table.set("g", this.get().getGreenByte());
			table.set("b", this.get().getBlueByte());
			table.set("a", this.get().getAlphaByte());
		}

		@Override
		@NotNull
		Color deserialize(@NotNull final JDB store) {
			final JDBTable table = store.getTable(this.preference);
			if (table == null) {
				throw new NullPointerException();
			}
			if (!table.hasKey("r", JDBKeyTypes.INTEGER)
					|| !table.hasKey("g", JDBKeyTypes.INTEGER)
					|| !table.hasKey("b", JDBKeyTypes.INTEGER)
					|| !table.hasKey("a", JDBKeyTypes.INTEGER)) {
				throw new NullPointerException();
			}
			return Color.get(table.getInteger("r"), table.getInteger("g"), table.getInteger("b"), table.getInteger("a"));
		}
	}

	@EventBusSubscriber(Shattered.SYSTEM_BUS_NAME)
	private static class EventHandler {

		@MessageListener("load_config")
		public static void onLoadConfig(final MessageEvent ignored) {
			ConfigManager.load();
		}
	}
}