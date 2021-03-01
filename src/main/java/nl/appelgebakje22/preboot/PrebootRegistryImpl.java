package nl.appelgebakje22.preboot;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.ClassReader;

public final class PrebootRegistryImpl implements PrebootRegistry {

	static final boolean DEVELOPER_MODE = Boolean.getBoolean("preboot.developer");
	static final boolean DUMP_CLASSES = Boolean.getBoolean("preboot.dump_transformed_classes");
	static final Logger LOGGER = LogManager.getLogger("Preboot");

	private final HashMap<Class<? extends Annotation>, List<Class<?>>> annotations = new HashMap<>();
	private final TreeSet<ITransformer> transformers = new TreeSet<>((o1, o2) -> o2.priority() - o1.priority());

	private final HashMap<String, byte[]> classByteCache = new HashMap<>();

	private final URL[] urls;
	private final PrebootClassLoader loader;

	public PrebootRegistryImpl(final URL[] urls, final Class<?>[] allowedClasses, final Predicate<String> accessValidator) {
		Objects.requireNonNull(allowedClasses);
		Objects.requireNonNull(accessValidator);
		this.urls = Objects.requireNonNull(urls);
		//Add ITransformer to allowed classes
		final Class<?>[] newAllowedClasses = new Class[allowedClasses.length + 1];
		System.arraycopy(allowedClasses, 0, newAllowedClasses, 0, allowedClasses.length);
		newAllowedClasses[newAllowedClasses.length - 1] = ITransformer.class;
		this.loader = new PrebootClassLoader(newAllowedClasses, accessValidator);
	}

	@Override
	public void registerTransformer(final ITransformer transformer) {
		this.transformers.add(transformer);
	}

	@Override
	public void load() {
		this.loadClasses();
		this.registerAnnotations();
	}

	@Override
	public List<Class<?>> getAnnotatedClasses(final Class<? extends Annotation> annotation) {
		if (annotation == null) {
			return null;
		} else {
			return this.annotations.computeIfAbsent(annotation, k -> Collections.emptyList());
		}
	}

	/**
	 * Discover, force-load and transform all classes in the provided archives
	 */
	private void loadClasses() {
		ClassCollector.collectClasses(this.urls).entrySet().stream()
				.filter(entry -> !entry.getKey().startsWith(this.getClass().getPackage().getName()))
				.filter(entry -> !this.loader.allowed.containsKey(entry.getKey()))
				.forEach(entry -> {
					final byte[] transformed = this.transform(entry.getKey(), entry.getValue());
					this.classByteCache.put(entry.getKey(), transformed);
				});
		for (final Map.Entry<String, byte[]> entry : new HashMap<>(this.classByteCache).entrySet()) {
			this.loadClassRecursive(entry.getKey());
		}
	}

	private void registerAnnotations() {
		final Map<Class<? extends Annotation>, List<Class<?>>> map = new ConcurrentHashMap<>();
		this.loader.classes.forEach((className, clazz) -> {
			final Annotation[] annotations = clazz.getDeclaredAnnotations();
			if (annotations.length > 0) {
				if (PrebootRegistryImpl.DEVELOPER_MODE) {
					PrebootRegistryImpl.LOGGER.debug(
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
		map.forEach((annotation, list) -> this.annotations.put(annotation, Collections.unmodifiableList(list)));
	}

	private void loadClassRecursive(String className) {
		className = className.replaceAll("/", ".");

		final byte[] bytes = this.classByteCache.get(className);
		if (bytes == null) {
			return;
		}
		final ClassReader reader = new ClassReader(bytes);

		this.loadClassRecursive(reader.getSuperName());
		Arrays.stream(reader.getInterfaces()).forEach(this::loadClassRecursive);

		if (this.classByteCache.containsKey(className)) {
			if (PrebootRegistryImpl.DEVELOPER_MODE) {
				PrebootRegistryImpl.LOGGER.debug("Loading class {}", className);
			}
			this.loader.defineClass(className, this.classByteCache.get(className));
			this.classByteCache.remove(className);
		}
	}

	private byte[] transform(final String className, byte[] bytes) {
		boolean hasTransformed = false;
		for (final ITransformer transformer : this.transformers) {
			try {
				final byte[] result = transformer.transform(className, bytes);
				if (result != null) {
					hasTransformed = true;
					bytes = result;
					if (PrebootRegistryImpl.DEVELOPER_MODE) {
						PrebootRegistryImpl.LOGGER.debug("Transformed class {} using transformer {}", className, transformer.getClass().getName());
					}
				}
			} catch (final Throwable e) {
				PrebootRegistryImpl.LOGGER.fatal("Error while transforming class {} with transformer {}", className, transformer.getClass().getName());
				if (PrebootRegistryImpl.DEVELOPER_MODE) {
					PrebootRegistryImpl.LOGGER.fatal(e);
				}
			}
		}
		if (hasTransformed && PrebootRegistryImpl.DUMP_CLASSES) {
			PrebootRegistryImpl.dumpClass(className, bytes);
		}
		return bytes;
	}

	private static void dumpClass(final String className, final byte[] data) {
		final File dir = new File("class_dump");
		if (dir.mkdir()) {
			try (final FileOutputStream stream = new FileOutputStream(new File(dir, className.replaceAll("\\.", "_") + ".class"))) {
				stream.write(data);
			} catch (final IOException ignored) {
			}
		}
	}
}