package shattered.preboot;

import java.lang.annotation.Annotation;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;

public final class Preboot {

	private static final PrebootClassLoader                               LOADER      = new PrebootClassLoader();
	private static final Map<String, byte[]>                              CLASS_BYTES = new ConcurrentHashMap<>();
	private static final Map<String, Class<?>>                            CLASSES     = new ConcurrentHashMap<>();
	private static final Map<Class<? extends Annotation>, List<Class<?>>> ANNOTATIONS = new ConcurrentHashMap<>();
	static final         Logger                                           LOGGER      = LogManager.getLogger("Preboot");

	public static void boot(final String[] args) {
		Configurator.setRootLevel(Level.ALL);
		Preboot.loadClasses();
		Preboot.registerAnnotations();
		Configurator.setRootLevel(Level.INFO);
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
					final List<Class<?>>              list           = Preboot.ANNOTATIONS.computeIfAbsent(annotationType, key -> new ArrayList<>());
					list.add(clazz);
				}
			}
		});
	}
}