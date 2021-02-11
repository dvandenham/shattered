package shattered.core.lua;

import java.util.ArrayList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.luaj.vm2.LuaBoolean;
import org.luaj.vm2.LuaInteger;
import org.luaj.vm2.LuaNumber;
import org.luaj.vm2.LuaString;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import shattered.Shattered;
import shattered.lib.json.JDBArray;
import shattered.lib.json.JDBCollection;
import shattered.lib.json.JDBTable;

public final class LuaSerializer {

	private LuaSerializer() {
	}

	public static boolean isArray(@NotNull final LuaTable table) {
		int index = 0;
		final LuaTableIterator iterator = new LuaTableIterator(table);
		while (iterator.hasNext()) {
			iterator.next();
			if (table.get(++index).isnil()) {
				return false;
			}
		}
		return true;
	}

	@Nullable
	public static String serializeValue(@NotNull final LuaValue value) {
		switch (value.type()) {
			case LuaValue.TINT:
				return "I:" + value.checkint();
			case LuaValue.TNONE:
				return "NONE:";
			case LuaValue.TNIL:
				return "NIL:";
			case LuaValue.TBOOLEAN:
				return "B:" + value.checkboolean();
			case LuaValue.TNUMBER:
				return "N:" + value.checkdouble();
			case LuaValue.TSTRING:
				return "S:" + value.checkjstring();
		}
		return null;
	}

	@NotNull
	public static LuaValue deserializeValue(@Nullable final String value) {
		if (value == null || !value.contains(":")) {
			Shattered.LOGGER.warn("Cannot deserialize value to Lua: {}", value);
			return LuaValue.NIL;
		}
		final String[] parts = value.split(":", 2);
		switch (parts[0]) {
			case "I":
				return LuaInteger.valueOf(Integer.parseInt(parts[1]));
			case "NONE":
				return LuaValue.NONE;
			case "NIL":
				return LuaValue.NIL;
			case "B":
				return LuaBoolean.valueOf(Boolean.parseBoolean(parts[1]));
			case "N":
				return LuaNumber.valueOf(Double.parseDouble(parts[1]));
			case "S":
				return LuaString.valueOf(parts[1]);
		}
		Shattered.LOGGER.warn("Cannot deserialize value to Lua: {}", value);
		return LuaValue.NIL;
	}

	@Nullable
	public static JDBCollection serializeTable(@NotNull final LuaValue value) {
		return LuaSerializer.serializeTable(value, new ArrayList<>(), null);
	}

	@Nullable
	private static JDBCollection serializeTable(@NotNull final LuaValue value, @NotNull final ArrayList<LuaValue> tracking, @Nullable final Object trackingKey) {
		if (!value.istable()) {
			return null;
		}
		if (tracking.contains(value)) {
			Shattered.LOGGER.warn("Cannot serialize recursive LuaTable at key: {}", trackingKey);
			return null;
		}
		final LuaTable table = (LuaTable) value;
		if (LuaSerializer.isArray(table)) {
			return LuaSerializer.serializeArray(table, tracking, trackingKey);
		}
		tracking.add(value);
		final JDBTable result = new JDBTable();
		final LuaTableIterator iterator = new LuaTableIterator(table);
		while (iterator.hasNext()) {
			final Varargs entry = iterator.next();
			final String key = entry.arg1().checkjstring();
			final LuaValue rowValue = entry.arg(2);
			if (rowValue instanceof LuaTable) {
				if (LuaSerializer.isArray((LuaTable) rowValue)) {
					final JDBArray serializedArray = LuaSerializer.serializeArray((LuaTable) value, tracking, key);
					if (serializedArray != null) {
						result.set(key, serializedArray);
					}
				} else {
					final JDBCollection serializedTable = LuaSerializer.serializeTable(rowValue, tracking, key);
					if (serializedTable != null) {
						result.set(key, serializedTable);
					}
				}
				continue;
			}
			final String serialized = LuaSerializer.serializeValue(rowValue);
			if (serialized == null) {
				Shattered.LOGGER.warn("Cannot serialize LuaValue: {}", rowValue.toString());
				continue;
			}
			result.set(key, serialized);
		}
		return result;
	}

	@Nullable
	private static JDBArray serializeArray(@NotNull final LuaTable table, @NotNull final ArrayList<LuaValue> tracking, @Nullable final Object trackingKey) {
		if (tracking.contains(table)) {
			Shattered.LOGGER.warn("Cannot serialize recursive LuaTable at key: {}", trackingKey);
			return null;
		}
		tracking.add(table);
		final JDBArray result = new JDBArray();
		final LuaArrayIterator iterator = new LuaArrayIterator(table);
		while (iterator.hasNext()) {
			final Varargs entry = iterator.next();
			final LuaValue index = entry.arg1();
			final LuaValue value = entry.arg(2);
			if (value instanceof LuaTable) {
				if (LuaSerializer.isArray((LuaTable) value)) {
					final JDBArray serializedArray = LuaSerializer.serializeArray((LuaTable) value, tracking, index);
					if (serializedArray != null) {
						result.add(serializedArray);
					}
				} else {
					final JDBCollection serializeTable = LuaSerializer.serializeTable(value, tracking, index);
					if (serializeTable != null) {
						result.add(serializeTable);
					}
				}
				continue;
			}
			String serialized = LuaSerializer.serializeValue(value);
			if (serialized == null) {
				Shattered.LOGGER.warn("Cannot serialize LuaValue: {}", value.toString());
				serialized = "NIL:";
			}
			result.add(serialized);
		}
		return result;
	}

	@Nullable
	public static LuaTable deserializeTable(@Nullable final JDBCollection collection) {
		if (collection instanceof JDBArray) {
			return LuaSerializer.deserializeArray((JDBArray) collection);
		}
		if (!(collection instanceof JDBTable)) {
			return null;
		}
		final JDBTable table = (JDBTable) collection;
		final LuaTable result = new LuaTable();
		for (final String keyName : table.getKeyNames()) {
			final String rowValue = table.getString(keyName);
			final LuaValue value = LuaSerializer.deserializeValue(rowValue);
			if (value == LuaValue.NIL) {
				Shattered.LOGGER.warn("Cannot deserialize value to Lua: {}", rowValue);
				continue;
			}
			result.set(keyName, rowValue);
		}
		return result;
	}

	@NotNull
	private static LuaTable deserializeArray(@NotNull final JDBArray array) {
		final LuaTable result = new LuaTable();
		for (int i = 0; i < array.getSize(); ++i) {
			final String rowValue = array.getString(i);
			if (rowValue == null) {
				Shattered.LOGGER.warn("Cannot deserialize value to Lua at index: {}", i);
				continue;
			}
			final LuaValue value = LuaSerializer.deserializeValue(rowValue);
			result.set(i + 1, value);
		}
		return result;
	}
}