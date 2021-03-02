package shattered.lib;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.function.Predicate;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import shattered.Shattered;

@SuppressWarnings("unchecked")
public final class ReflectionHelper {

	@Target(ElementType.METHOD)
	@Retention(RetentionPolicy.RUNTIME)
	public @interface Reflectable {
	}

	private ReflectionHelper() {
	}

	public static Field[] collectFields(@NotNull final Class<?> clazz, @NotNull final Predicate<Field> predicate) {
		return Arrays.stream(clazz.getDeclaredFields()).filter(predicate).toArray(Field[]::new);
	}

	public static Field[] filterFields(@NotNull final Field[] fields, @NotNull final Predicate<Field> predicate) {
		return Arrays.stream(fields).filter(predicate).toArray(Field[]::new);
	}

	public static <T> T invokeMethod(@NotNull final Class<?> clazz, @Nullable final Object instance, @NotNull final Class<T> returnType, final Object... args) {
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
			final Method method = Arrays.stream(clazz.getDeclaredMethods())
					.filter(m -> m.isAnnotationPresent(Reflectable.class))
					.filter(m -> returnType.equals(m.getReturnType()))
					.filter(m -> Arrays.equals(paramClasses, m.getParameterTypes()))
					.findFirst()
					.orElseThrow(NoSuchMethodException::new);
			method.setAccessible(true);
			return (T) method.invoke(instance, paramValues);
		} catch (final NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
			if (Shattered.DEVELOPER_MODE) {
				Shattered.LOGGER.error("Error during invocation of method in class " + clazz.getName(), e);
			}
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
		} catch (final NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
			if (Shattered.DEVELOPER_MODE) {
				Shattered.LOGGER.error("Error during instantiation of class " + clazz.getName(), e);
			}
			return null;
		}
	}
}