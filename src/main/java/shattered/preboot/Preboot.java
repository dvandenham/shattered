package shattered.preboot;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;
import shattered.Shattered;

public final class Preboot {

	private static final PrebootClassLoader    LOADER      = new PrebootClassLoader();
	private static final Map<String, byte[]>   CLASS_BYTES = new ConcurrentHashMap<>();
	private static final Map<String, Class<?>> CLASSES     = new ConcurrentHashMap<>();
	static final         Logger                LOGGER      = LogManager.getLogger("Preboot");

	public static void boot(final String[] args) {
		Configurator.setRootLevel(Level.ALL);
		Preboot.loadClasses();
		Preboot.registerAnnotations();
		Preboot.LOGGER.debug("Starting secondary boot stage");
		Configurator.setRootLevel(Level.INFO);
		Preboot.nextBoot(args);
	}

	private static void loadClasses() {
		Preboot.CLASS_BYTES.putAll(ClassCollector.collectClasses(new URL[]{
				Preboot.class.getProtectionDomain().getCodeSource().getLocation()
		}));
		Preboot.CLASS_BYTES.keySet().forEach(Preboot::loadClass);
	}

	static Class<?> loadClass(final String className) {
		return Preboot.CLASSES.computeIfAbsent(className, k -> {
			final byte[] bytes = Preboot.CLASS_BYTES.get(className);
			if (className.startsWith(Preboot.class.getPackage().getName())) {
				return null;
			}
			if (SysProps.LOG_CLASS_EXISTENCE) {
				Preboot.LOGGER.debug("Processing class {}", className);
			}
			final byte[] transformed = TransformationRegistry.transform(className, bytes);
			return Preboot.LOADER.defineClass(className, transformed);
		});
	}

	private static void registerAnnotations() {
		final Map<Class<? extends Annotation>, List<Class<?>>> map = new ConcurrentHashMap<>();
		Preboot.CLASSES.forEach((className, clazz) -> {
			final Annotation[] annotations = clazz.getDeclaredAnnotations();
			if (annotations.length > 0) {
				if (SysProps.LOG_ANNOTATIONS) {
					Preboot.LOGGER.debug(
							"Registered {} using annotations {}",
							className,
							Arrays.toString(
									Arrays.stream(annotations)
											.map(Annotation::annotationType)
											.map(Class::getName)
											.toArray()
							)
					);
				}
				for (final Annotation annotation : annotations) {
					final Class<? extends Annotation> annotationType = annotation.annotationType();
					final List<Class<?>>              list           = map.computeIfAbsent(annotationType, key -> new ArrayList<>());
					list.add(clazz);
				}
			}
		});
		map.forEach((annotation, list) -> AnnotationRegistry.REGISTRY.put(annotation.getName(), Collections.unmodifiableList(list)));
	}

	private static void nextBoot(final String[] args) {
		final Method bootMethod = Arrays.stream(Shattered.class.getDeclaredMethods())
				.filter(method -> method.getReturnType().equals(Void.TYPE))
				.filter(method -> method.getParameterCount() == 1 && method.getParameterTypes()[0].equals(String[].class))
				.findFirst()
				.orElseThrow(() -> {
					final IllegalStateException e = new IllegalStateException("Fatal error during secondary boot stage");
					e.setStackTrace(new StackTraceElement[0]);
					return e;
				});
		try {
			bootMethod.invoke(null, (Object) args);
		} catch (final Throwable ignored) {
			final IllegalStateException e = new IllegalStateException("Fatal error during secondary boot stage");
			e.setStackTrace(new StackTraceElement[0]);
			throw e;
		}
	}
}