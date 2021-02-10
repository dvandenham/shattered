package preboot;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;
import org.objectweb.asm.ClassReader;

public final class Preboot {

	private static final PrebootClassLoader LOADER = new PrebootClassLoader();
	static final String PREBOOT_PACKAGE_NAME = Preboot.class.getPackage().getName();
	static final Map<String, byte[]> CLASS_BYTES = new ConcurrentHashMap<>();
	static final Logger LOGGER = LogManager.getLogger("Preboot");

	public static void boot(final String[] args) {
		Configurator.setRootLevel(Level.ALL);
		Preboot.loadClasses();
		Preboot.registerAnnotations();
		Preboot.LOGGER.debug("Starting secondary boot stage");
		Configurator.setRootLevel(Level.INFO);
		Preboot.nextBoot(args);
	}

	private static void loadClasses() {
		ClassCollector.collectClasses(new URL[]{
				Preboot.class.getProtectionDomain().getCodeSource().getLocation()
		}).entrySet().stream()
				.filter(entry -> !entry.getKey().startsWith(Preboot.PREBOOT_PACKAGE_NAME))
				.forEach(entry -> Preboot.CLASS_BYTES.put(entry.getKey(), entry.getValue()));
		for (final Map.Entry<String, byte[]> entry : new HashMap<>(Preboot.CLASS_BYTES).entrySet()) {
			Preboot.loadClassRecursive(entry.getKey());
		}
	}

	private static void loadClassRecursive(String className) {
		className = className.replaceAll("/", ".");

		final byte[] bytes = Preboot.CLASS_BYTES.get(className);
		if (bytes == null) {
			return;
		}
		final ClassReader reader = new ClassReader(bytes);

		Preboot.loadClassRecursive(reader.getSuperName());
		Arrays.stream(reader.getInterfaces()).forEach(Preboot::loadClassRecursive);

		if (Preboot.CLASS_BYTES.containsKey(className)) {
			System.out.println("Loading class " + className);
			Preboot.LOADER.defineClass(className, Preboot.CLASS_BYTES.get(className));
			Preboot.CLASS_BYTES.remove(className);
		}
	}

	private static void registerAnnotations() {
		final Map<Class<? extends Annotation>, List<Class<?>>> map = new ConcurrentHashMap<>();
		Preboot.LOADER.classes.forEach((className, clazz) -> {
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
					final List<Class<?>> list = map.computeIfAbsent(annotationType, key -> new ArrayList<>());
					list.add(clazz);
				}
			}
		});
		map.forEach((annotation, list) -> AnnotationRegistry.REGISTRY.put(annotation, Collections.unmodifiableList(list)));
	}

	private static void nextBoot(final String[] args) {
		try {
			final Class<?> bootClass = AnnotationRegistry.getAnnotatedClasses(BootManager.class).get(0);
			final Method bootMethod = Arrays.stream(bootClass.getDeclaredMethods())
					.filter(method -> method.getReturnType().equals(Void.TYPE))
					.filter(method -> method.getParameterCount() == 1 && method.getParameterTypes()[0].equals(String[].class))
					.findFirst()
					.orElseThrow(() -> {
						final IllegalStateException e = new IllegalStateException("Fatal error during secondary boot stage");
						e.setStackTrace(new StackTraceElement[0]);
						return e;
					});
			bootMethod.invoke(null, (Object) args);
		} catch (final Throwable cause) {
			if (SysProps.DEVELOPER_MODE) {
				throw new RuntimeException(cause);
			} else {
				final IllegalStateException e = new IllegalStateException("Fatal error during secondary boot stage");
				e.setStackTrace(new StackTraceElement[0]);
				throw e;
			}
		}
	}
}