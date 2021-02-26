package shattered.lib.json;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.google.gson.internal.bind.ReflectiveTypeAdapterFactory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import shattered.lib.ReflectionHelper;

final class JsonValidator {

	private JsonValidator() {
	}

	public static void validate(@NotNull final JsonObject object, @NotNull final Class<?> type, @Nullable final String path) {
		//Collect all non-ignored fields
		final Field[] allFields = ReflectionHelper.collectFields(type, field -> {
			if (Modifier.isStatic(field.getModifiers())) {
				return false;
			}
			if (field.isAnnotationPresent(Json.Ignore.class)) {
				return false;
			}
			if (!field.isAnnotationPresent(Expose.class)) {
				return true;
			}
			final Expose expose = field.getAnnotation(Expose.class);
			return expose.deserialize();
		});
		//Collect all required fields that do not belong to a typed relationship
		final Field[] requiredFields = ReflectionHelper.filterFields(allFields, field -> field.isAnnotationPresent(Json.Required.class) && !field.isAnnotationPresent(Json.TypeValue.class));
		for (final Field field : requiredFields) {
			final String name = JsonValidator.getFieldName(field);
			final Json.Required required = field.getAnnotation(Json.Required.class);
			if (required.group().length == 0 && JsonUtils.isNull(object, name)) {
				throw JsonValidator.createException("Missing (or invalid) required attribute '%s'", path, name);
			}
		}
		JsonValidator.validateRequiredGroups(object, requiredFields, path);
		final Field[] nestedClassFields = ReflectionHelper.filterFields(allFields, field -> JsonUtils.GSON.getAdapter(field.getType()) instanceof ReflectiveTypeAdapterFactory.Adapter);
		for (final Field nested : nestedClassFields) {
			final String name = JsonValidator.getFieldName(nested);
			JsonValidator.validate(object.getAsJsonObject(name), nested.getType(), (path == null ? "" : path + ".") + name);
		}
	}

	static void validateRequiredGroups(@NotNull final JsonObject object, @NotNull Field[] fields, @Nullable final String path) {
		fields = ReflectionHelper.filterFields(fields, field -> field.getAnnotation(Json.Required.class).group().length > 0);
		if (fields.length == 0) {
			return;
		}
		final Set<String> groups = Arrays.stream(fields)
				.map(field -> field.getAnnotation(Json.Required.class).group()[0])
				.map(Json.Required.OR::groupName)
				.collect(Collectors.toSet());
		final HashMap<String, Map<String, List<Field>>> groupedFields = new HashMap<>();
		for (final String group : groups) {
			final Map<String, List<Field>> indexedFields = new HashMap<>();
			for (final Field field : ReflectionHelper.filterFields(fields, field -> field.getAnnotation(Json.Required.class).group()[0].groupName().equals(group))) {
				final String index = field.getAnnotation(Json.Required.class).group()[0].groupIndex();
				if (!indexedFields.containsKey(index)) {
					indexedFields.put(index, new ArrayList<>());
				}
				indexedFields.get(index).add(field);
			}
			groupedFields.put(group, indexedFields);
		}
		for (final Map.Entry<String, Map<String, List<Field>>> entry : groupedFields.entrySet()) {
			String foundGroup = null;
			groupLoop:
			for (final Map.Entry<String, List<Field>> entry2 : entry.getValue().entrySet()) {
				for (final Field field : entry2.getValue()) {
					if (JsonUtils.isNull(object, JsonValidator.getFieldName(field))) {
						continue groupLoop;
					}
				}
				if (foundGroup == null) {
					foundGroup = entry.getKey();
				} else {
					JsonValidator.throwGroupRequirementException("Only one of the following groups can be defined: ", entry.getValue(), path);
				}
			}
			if (foundGroup == null) {
				JsonValidator.throwGroupRequirementException("One of the following groups MUST be defined: ", entry.getValue(), path);
			}
		}
	}

	private static void throwGroupRequirementException(@NotNull final String prefix, @NotNull final Map<String, List<Field>> fields, @Nullable final String path) {
		final StringBuilder builder = new StringBuilder(prefix);
		boolean first = true;
		for (final List<Field> allFields : fields.values()) {
			builder.append(Arrays.toString(allFields.stream().map(JsonValidator::getFieldName).toArray(String[]::new)));
			if (first) {
				builder.append(", ");
				first = false;
			}
		}
		throw JsonValidator.createException(builder.toString(), path);
	}

	@NotNull
	public static String getFieldName(@NotNull final Field field) {
		if (field.isAnnotationPresent(SerializedName.class)) {
			return field.getAnnotation(SerializedName.class).value();
		}
		return JsonUtils.GSON.fieldNamingStrategy().translateName(field);
	}

	@NotNull
	public static JsonParseException createException(@NotNull final String msg, @Nullable final String path, final Object... format) {
		final StringBuilder builder = new StringBuilder(msg);
		if (path != null) {
			builder.append(String.format(" in object: '%s'", path));
		}
		return new JsonParseException(String.format(builder.toString(), format));
	}
}
