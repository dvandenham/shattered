package nl.appelgebakje22.preboot;

import java.net.URL;
import java.util.HashMap;
import java.util.function.Predicate;

final class PrebootClassLoader extends ClassLoader {

	private static final String PACKAGE_NAME = PrebootClassLoader.class.getPackage().getName();
	private final ClassLoader parentLoader = this.getClass().getClassLoader();
	final HashMap<String, Class<?>> allowed = new HashMap<>();
	final HashMap<String, Class<?>> classes = new HashMap<>();
	private final Predicate<String> accessValidator;

	public PrebootClassLoader(final Class<?>[] allowedClasses, final Predicate<String> accessValidator) {
		super(null);
		for (final Class<?> allowedClass : allowedClasses) {
			this.allowed.put(allowedClass.getName(), allowedClass);
		}
		this.accessValidator = accessValidator;
	}

	@Override
	public Class<?> loadClass(final String name, final boolean resolve) throws ClassNotFoundException {
		if (name.startsWith("java.") || name.startsWith("javax.") || name.startsWith("sun.")) {
			return super.loadClass(name, resolve);
		} else {
			final Class<?> allowedClass = this.allowed.get(name);
			if (allowedClass != null) {
				return allowedClass;
			} else {
				if (!this.accessValidator.test(name)) {
					final String message = String.format(
							"Illegal access to class%s!",
							PrebootRegistryImpl.DEVELOPER_MODE ? " (" + name + ')' : ""
					);
					throw new SecurityException(message);
				} else {
					Class<?> result;
					if ((result = this.classes.get(name)) != null) {
						return result;
					} else if ((result = this.findLoadedClass(name)) != null) {
						return result;
					} else {
						return this.parentLoader.loadClass(name);
					}
				}
			}
		}
	}

	void defineClass(final String className, final byte[] bytes) {
		this.classes.put(className, this.defineClass(className, bytes, 0, bytes.length));
	}

	@Override
	protected URL findResource(final String name) {
		return this.parentLoader.getResource(name);
	}
}