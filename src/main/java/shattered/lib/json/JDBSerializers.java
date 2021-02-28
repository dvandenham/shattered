package shattered.lib.json;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.regex.Matcher;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import org.jetbrains.annotations.NotNull;

public final class JDBSerializers {

	private static final Gson GSON = new GsonBuilder().registerTypeAdapterFactory(new JsonFactory()).create();

	private JDBSerializers() {
	}

	public static void serialize(@NotNull final JDB data, @NotNull final Writer writer) throws IOException {
		writer.write(JDB.HEADER);
		writer.write('\n');
		writer.write(JDBSerializers.GSON.toJson(data.data, JDBTable.class));
		writer.flush();
	}

	public static JDB deserialize(@NotNull final Reader reader) throws IOException {
		final BufferedReader buf = new BufferedReader(reader);
		JDBSerializers.validateVersion(buf.readLine());
		return new JDB(JDBSerializers.GSON.fromJson(buf, JDBTable.class));
	}

	private static void validateVersion(@NotNull final String header) throws IOException {
		final Matcher matcher = JDB.PATTERN.matcher(header);
		if (!matcher.matches()) {
			throw new IOException("Invalid data!");
		}
		try {
			final String found = matcher.group(1);
			final int number = Integer.parseInt(found);
			if (number > JDB.VERSION) {
				throw new IOException("Could not read data with a newer version of the program!");
			}
		} catch (final NumberFormatException ignored) {
			throw new IOException("Invalid data!");
		}
	}

	private static class JsonFactory implements TypeAdapterFactory {

		@SuppressWarnings("unchecked")
		@Override
		public <T> TypeAdapter<T> create(final Gson gson, final TypeToken<T> type) {
			if (type.getRawType() == JDBTable.class) {
				return (TypeAdapter<T>) new JsonTableAdapter(gson);
			} else if (type.getRawType() == JDBArray.class) {
				return (TypeAdapter<T>) new JsonArrayAdapter(gson);
			}
			return null;
		}
	}

	private static class JsonTableAdapter extends TypeAdapter<JDBTable> {

		private final Gson gson;

		public JsonTableAdapter(final Gson gson) {
			this.gson = gson;
		}

		@Override
		public void write(final JsonWriter writer, final JDBTable table) throws IOException {
			writer.beginObject();
			for (final String key : table.getKeyNames()) {
				writer.name(key);
				writer.jsonValue(this.gson.toJson(table.getKey(key)));
			}
			writer.endObject();
		}

		@Override
		public JDBTable read(final JsonReader reader) throws IOException {
			final JDBTable result = new JDBTable();
			reader.beginObject();
			while (reader.hasNext()) {
				final String key = reader.nextName();
				switch (reader.peek()) {
					case BEGIN_OBJECT: {
						result.set(key, (JDBTable) this.gson.fromJson(reader, JDBTable.class));
						break;
					}
					case BEGIN_ARRAY: {
						result.set(key, (JDBArray) this.gson.fromJson(reader, JDBArray.class));
						break;
					}
					case BOOLEAN: {
						result.set(key, reader.nextBoolean());
						break;
					}
					case NUMBER: {
						result.set(key, reader.nextDouble());
						break;
					}
					case STRING: {
						result.set(key, reader.nextString());
						break;
					}
					default:
						reader.skipValue();
						break;
				}
			}
			reader.endObject();
			return result;
		}
	}

	private static class JsonArrayAdapter extends TypeAdapter<JDBArray> {

		private final Gson gson;

		public JsonArrayAdapter(final Gson gson) {
			this.gson = gson;
		}

		@Override
		public void write(final JsonWriter writer, final JDBArray array) throws IOException {
			writer.beginArray();
			for (int i = 0; i < array.getSize(); ++i) {
				writer.jsonValue(this.gson.toJson(array.getKey(i)));
			}
			writer.endArray();
		}

		@Override
		public JDBArray read(final JsonReader reader) throws IOException {
			final JDBArray result = new JDBArray();
			reader.beginArray();
			while (reader.hasNext()) {
				switch (reader.peek()) {
					case BEGIN_OBJECT: {
						result.add((JDBTable) this.gson.fromJson(reader, JDBTable.class));
						break;
					}
					case BEGIN_ARRAY: {
						result.add((JDBArray) this.gson.fromJson(reader, JDBArray.class));
						break;
					}
					case BOOLEAN: {
						result.add(reader.nextBoolean());
						break;
					}
					case NUMBER: {
						result.add(reader.nextDouble());
						break;
					}
					case STRING: {
						result.add(reader.nextString());
						break;
					}
					default:
						reader.skipValue();
						break;
				}
			}
			reader.endObject();
			return result;
		}
	}
}