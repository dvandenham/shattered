package shattered.lib.json;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.math.BigDecimal;
import java.math.BigInteger;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class JsonUtils {

	private JsonUtils() {
	}

	public static final Gson GSON;

	public static boolean hasField(@Nullable final JsonObject json, @NotNull final String name) {
		return json != null && json.has(name);
	}

	@SuppressWarnings("BooleanMethodIsAlwaysInverted")
	public static boolean hasPrimitive(@Nullable final JsonObject json, @NotNull final String name) {
		return JsonUtils.hasField(json, name) && json.get(name).isJsonPrimitive();
	}

	public static boolean hasBoolean(@Nullable final JsonObject json, @NotNull final String name) {
		if (!JsonUtils.hasPrimitive(json, name)) {
			return false;
		}
		assert json != null;
		return json.getAsJsonPrimitive(name).isBoolean();
	}

	public static boolean hasNumber(@Nullable final JsonObject json, @NotNull final String name) {
		if (!JsonUtils.hasPrimitive(json, name)) {
			return false;
		}
		assert json != null;
		return json.getAsJsonPrimitive(name).isNumber();
	}

	public static boolean hasString(@Nullable final JsonObject json, @NotNull final String name) {
		if (!JsonUtils.hasPrimitive(json, name)) {
			return false;
		}
		assert json != null;
		return json.getAsJsonPrimitive(name).isString();
	}

	public static boolean hasObject(@Nullable final JsonObject json, @NotNull final String name) {
		return JsonUtils.hasField(json, name) && json.get(name).isJsonObject();
	}

	public static boolean hasArray(@Nullable final JsonObject json, @NotNull final String name) {
		return JsonUtils.hasField(json, name) && json.get(name).isJsonArray();
	}

	public static boolean isNull(@Nullable final JsonObject json, @NotNull final String name) {
		return !JsonUtils.hasField(json, name) || json.get(name).isJsonNull();
	}

	@NotNull
	public static JsonObject getAsObject(@Nullable final JsonElement json, @NotNull final String name) {
		if (json == null || !json.isJsonObject()) {
			throw JsonUtils.createException(json, name, "a primitive");
		}
		return json.getAsJsonObject();
	}

	@NotNull
	public static JsonArray getAsArray(@Nullable final JsonElement json, @NotNull final String name) {
		if (json == null || !json.isJsonArray()) {
			throw JsonUtils.createException(json, name, "a primitive");
		}
		return json.getAsJsonArray();
	}

	@NotNull
	public static JsonPrimitive getAsPrimitive(@Nullable final JsonElement json, @NotNull final String name) {
		if (json == null || !json.isJsonPrimitive()) {
			throw JsonUtils.createException(json, name, "a primitive");
		}
		return json.getAsJsonPrimitive();
	}

	public static boolean getAsBoolean(@Nullable final JsonElement json, @NotNull final String name) {
		if (json == null || !json.isJsonPrimitive()) {
			throw JsonUtils.createException(json, name, "a string");
		}
		final JsonPrimitive primitive = json.getAsJsonPrimitive();
		if (!primitive.isBoolean()) {
			throw JsonUtils.createException(json, name, "a boolean");
		}
		return primitive.getAsBoolean();
	}

	@NotNull
	public static JsonPrimitive getAsNumber(@Nullable final JsonElement json, @NotNull final String name) {
		if (json == null || !json.isJsonPrimitive()) {
			throw JsonUtils.createException(json, name, "a number");
		}
		final JsonPrimitive primitive = json.getAsJsonPrimitive();
		if (!primitive.isNumber()) {
			throw JsonUtils.createException(json, name, "a number");
		}
		return primitive;
	}

	public static int getAsByte(@Nullable final JsonElement json, @NotNull final String name) {
		return JsonUtils.getAsNumber(json, name).getAsByte();
	}

	public static int getAsShort(@Nullable final JsonElement json, @NotNull final String name) {
		return JsonUtils.getAsNumber(json, name).getAsShort();
	}

	public static int getAsInt(@Nullable final JsonElement json, @NotNull final String name) {
		return JsonUtils.getAsNumber(json, name).getAsInt();
	}

	public static float getAsFloat(@Nullable final JsonElement json, @NotNull final String name) {
		return JsonUtils.getAsNumber(json, name).getAsFloat();
	}

	public static double getAsDouble(@Nullable final JsonElement json, @NotNull final String name) {
		return JsonUtils.getAsNumber(json, name).getAsDouble();
	}

	public static long getAsLong(@Nullable final JsonElement json, @NotNull final String name) {
		return JsonUtils.getAsNumber(json, name).getAsLong();
	}

	@NotNull
	public static BigInteger getAsBigInteger(@Nullable final JsonElement json, @NotNull final String name) {
		return JsonUtils.getAsNumber(json, name).getAsBigInteger();
	}

	@NotNull
	public static BigDecimal getAsBigDecimal(@Nullable final JsonElement json, @NotNull final String name) {
		return JsonUtils.getAsNumber(json, name).getAsBigDecimal();
	}

	@NotNull
	public static String getAsString(@Nullable final JsonElement json, @NotNull final String name) {
		if (json == null || !json.isJsonPrimitive()) {
			throw JsonUtils.createException(json, name, "a string");
		}
		final JsonPrimitive primitive = json.getAsJsonPrimitive();
		if (!primitive.isString()) {
			throw JsonUtils.createException(json, name, "a string");
		}
		return primitive.getAsString();
	}

	@NotNull
	public static JsonObject getObject(@Nullable final JsonObject json, @NotNull final String name) {
		if (json == null || !json.has(name) || !json.get(name).isJsonObject()) {
			throw JsonUtils.createException(json, name, "an object");
		}
		return json.get(name).getAsJsonObject();
	}

	@NotNull
	public static JsonArray getArray(@Nullable final JsonObject json, @NotNull final String name) {
		if (json == null || !json.has(name) || !json.get(name).isJsonArray()) {
			throw JsonUtils.createException(json, name, "an array");
		}
		return json.get(name).getAsJsonArray();
	}

	@NotNull
	public static JsonPrimitive getPrimitive(@Nullable final JsonObject json, @NotNull final String name) {
		if (json == null || !json.has(name) || !json.get(name).isJsonPrimitive()) {
			throw JsonUtils.createException(json, name, "a primitive");
		}
		return json.get(name).getAsJsonPrimitive();
	}

	public static boolean getBoolean(@Nullable final JsonObject json, @NotNull final String name) {
		if (json == null || !json.has(name) || !json.get(name).isJsonPrimitive()) {
			throw JsonUtils.createException(json, name, "a boolean");
		}
		final JsonPrimitive primitive = json.get(name).getAsJsonPrimitive();
		if (!primitive.isBoolean()) {
			throw JsonUtils.createException(json, name, "a boolean");
		}
		return primitive.getAsBoolean();
	}

	public static JsonPrimitive getNumberInternal(@Nullable final JsonObject json, @NotNull final String name) {
		if (json == null || !json.has(name) || !json.get(name).isJsonPrimitive()) {
			throw JsonUtils.createException(json, name, "a number");
		}
		final JsonPrimitive primitive = json.get(name).getAsJsonPrimitive();
		if (!primitive.isNumber()) {
			throw JsonUtils.createException(json, name, "a number");
		}
		return primitive;
	}

	public static Number getNumber(@Nullable final JsonObject json, @NotNull final String name) {
		return JsonUtils.getNumberInternal(json, name).getAsNumber();
	}

	public static byte getByte(@Nullable final JsonObject json, @NotNull final String name) {
		return JsonUtils.getNumber(json, name).byteValue();
	}

	public static short getShort(@Nullable final JsonObject json, @NotNull final String name) {
		return JsonUtils.getNumber(json, name).shortValue();
	}

	public static int getInt(@Nullable final JsonObject json, @NotNull final String name) {
		return JsonUtils.getNumber(json, name).intValue();
	}

	public static float getFloat(@Nullable final JsonObject json, @NotNull final String name) {
		return JsonUtils.getNumber(json, name).floatValue();
	}

	public static double getDouble(@Nullable final JsonObject json, @NotNull final String name) {
		return JsonUtils.getNumber(json, name).doubleValue();
	}

	public static double getLong(@Nullable final JsonObject json, @NotNull final String name) {
		return JsonUtils.getNumber(json, name).longValue();
	}

	public static BigInteger getBigInteger(@Nullable final JsonObject json, @NotNull final String name) {
		return JsonUtils.getNumberInternal(json, name).getAsBigInteger();
	}

	public static BigDecimal getBigDecimal(@Nullable final JsonObject json, @NotNull final String name) {
		return JsonUtils.getNumberInternal(json, name).getAsBigDecimal();
	}

	public static String getString(@Nullable final JsonObject json, @NotNull final String name) {
		if (json == null || !json.has(name) || !json.get(name).isJsonPrimitive()) {
			throw JsonUtils.createException(json, name, "a string");
		}
		final JsonPrimitive primitive = json.get(name).getAsJsonPrimitive();
		if (!primitive.isString()) {
			throw JsonUtils.createException(json, name, "a string");
		}
		return primitive.getAsString();
	}

	@Nullable
	public static <T> T deserialize(@NotNull final Reader reader, @NotNull final Class<T> type) {
		try (final JsonReader json = new JsonReader(reader)) {
			if (json.peek() != JsonToken.BEGIN_OBJECT) {
				throw new JsonParseException("Expected object");
			}
			final JsonObject object = JsonUtils.GSON.fromJson(json, JsonObject.class);
			if (object == null) {
				return null;
			}
			JsonValidator.validate(object, type, null);
			final T result = JsonUtils.GSON.fromJson(object, type);
			StringTypeValidator.validate(object, type, result, null);
			return result;
		} catch (final IOException e) {
			throw new JsonParseException(e);
		}
	}

	@Nullable
	public static <T> T deserialize(@NotNull final String json, @NotNull final Class<T> type) {
		return JsonUtils.deserialize(new StringReader(json), type);
	}

	@Nullable
	public static <T> T deserialize(@NotNull final File file, @NotNull final Class<T> type) throws FileNotFoundException {
		return JsonUtils.deserialize(new FileReader(file), type);
	}

	@NotNull
	private static String toString(@Nullable final JsonElement json) {
		if (json == null) {
			return "null (missing)";
		} else if (json.isJsonNull()) {
			return "null (value)";
		} else if (json.isJsonArray()) {
			return "an array (" + json + ')';
		} else if (json.isJsonObject()) {
			return "an object (" + json + ')';
		} else if (json.isJsonPrimitive()) {
			final JsonPrimitive prim = json.getAsJsonPrimitive();
			if (prim.isBoolean()) {
				return "a boolean (" + json + ')';
			} else if (prim.isNumber()) {
				return "a number (" + json + ')';
			} else if (prim.isString()) {
				return "a string (" + json + ')';
			}
		}
		return json.toString();
	}

	private static JsonSyntaxException createException(@Nullable final JsonElement json, @NotNull final String name, @NotNull final String expected) {
		return new JsonSyntaxException(String.format("Expected field '%s' to be %s but found %s", name, expected, JsonUtils.toString(json)));
	}

	static {
		final GsonBuilder builder = new GsonBuilder()
				.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
				.setExclusionStrategies(new AnnotatedExclusionStrategy())
				.serializeNulls()
				.registerTypeAdapterFactory(new EnumAdapterFactory());
		MathTypeAdapters.register(builder);
		GSON = builder.create();
	}
}