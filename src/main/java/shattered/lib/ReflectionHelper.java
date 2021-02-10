package shattered.lib;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.function.Predicate;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class ReflectionHelper {

	private ReflectionHelper() {
	}

	public static Field[] collectFields(@NotNull final Class<?> clazz, @NotNull final Predicate<Field> predicate) {
		return Arrays.stream(clazz.getDeclaredFields()).filter(predicate).toArray(Field[]::new);
	}

	public static Field[] filterFields(@NotNull final Field[] fields, @NotNull final Predicate<Field> predicate) {
		return Arrays.stream(fields).filter(predicate).toArray(Field[]::new);
	}

	public static <T> Field findField(@NotNull final Class<?> clazz, @NotNull final Class<T> fieldType) {
		for (final Field field : clazz.getDeclaredFields()) {
			if (field.getType() == fieldType) {
				return field;
			}
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public static <T> T getField(@NotNull final Class<?> clazz, @Nullable final Object instance, @NotNull final Class<T> fieldType, @Nullable final Predicate<T> predicate) {
		for (final Field field : clazz.getDeclaredFields()) {
			if (field.getType() == fieldType) {
				final T result = (T) ReflectionHelper.getField(field, instance);
				if (predicate != null && !predicate.test(result)) {
					continue;
				}
				return result;
			}
		}
		return null;
	}

	public static Object getField(@NotNull final Field field, @Nullable final Object instance) {
		try {
			final boolean accessible = field.isAccessible();
			field.setAccessible(true);
			final Object result = field.get(instance);
			field.setAccessible(accessible);
			return result;
		} catch (final IllegalAccessException e) {
			return null;
		}
	}

	public static void setField(@NotNull final Field field, @Nullable final Object instance, @Nullable final Object newValue) {
		try {
			final boolean accessible = field.isAccessible();
			field.setAccessible(true);
			final boolean isFinal = Modifier.isFinal(field.getModifiers());

			Field modifiersField = null;
			if (isFinal) {
				modifiersField = Field.class.getDeclaredField("modifiers");
				modifiersField.setAccessible(true);
				modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);
			}
			field.set(instance, newValue);
			if (isFinal) {
				modifiersField.setInt(field, field.getModifiers() & Modifier.FINAL);
				modifiersField.setAccessible(false);
			}
			field.setAccessible(accessible);
		} catch (final Throwable ignored) {
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
}