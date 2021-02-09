package shattered.lib;

import java.lang.reflect.Field;
import java.util.function.Predicate;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class ReflectionHelper {

	@Nullable
	public static Field getField(@NotNull final Class<?> clazz, @NotNull final Predicate<Field> predicate) {
		for (final Field field : clazz.getDeclaredFields()) {
			if (predicate.test(field)) {
				return field;
			}
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	@Nullable
	public static <T> T getFieldValue(@NotNull final Class<?> clazz, @Nullable final Object instance, @NotNull final Class<T> fieldType) {
		final Field field = ReflectionHelper.getField(clazz, f -> f.getType().equals(fieldType));
		if (field == null) {
			return null;
		}
		try {
			field.setAccessible(true);
			return (T) field.get(instance);
		} catch (final IllegalAccessException e) {
			return null;
		}
	}

	private ReflectionHelper() {
	}
}