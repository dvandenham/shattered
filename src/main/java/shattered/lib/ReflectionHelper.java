package shattered.lib;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
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

	@Nullable
	public static <T> T instantiate(@NotNull final Class<T> clazz, final Object... args) {
		if (args.length % 2 == 1) {
			throw new RuntimeException("args array should contain class-value pairs!");
		}
		final Class<?>[] paramClasses = new Class[args.length / 2];
		final Object[] paramValues = new Object[args.length / 2];
		for (int i = 0, j = 0; i < args.length; i += 2) {
			if (args[i] == null || args[i].getClass() != Class.class) {
				throw new RuntimeException(String.format("Argument %s should be a class!", i));
			}
			paramClasses[j] = (Class<?>) args[i];
			paramValues[j++] = args[i + 1];
		}
		try {
			final Constructor<T> constructor = clazz.getDeclaredConstructor(paramClasses);
			constructor.setAccessible(true);
			return constructor.newInstance(paramValues);
		} catch (final NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException ignored) {
			return null;
		}
	}

	private ReflectionHelper() {
	}
}