package preboot;

import java.util.HashMap;

final class PrebootClassLoader extends ClassLoader {

	private static final boolean LOG_ILLEGAL_CLASS = SysProps.DEVELOPER_MODE ||
			"true".equals(System.getProperty("shattered.preboot.log.illegal"));
	private final ClassLoader parentLoader = this.getClass().getClassLoader();
	final HashMap<String, Class<?>> classes = new HashMap<>();

	public PrebootClassLoader() {
		super(null);
	}

	@Override
	public Class<?> loadClass(final String name, final boolean resolve) throws ClassNotFoundException {
		if (name.startsWith("java.") || name.startsWith("javax.") || name.startsWith("sun.")) {
			return super.loadClass(name, resolve);
		} else if (name.startsWith(Preboot.PREBOOT_PACKAGE_NAME)) {
			if (name.equals(BootManager.class.getName())) {
				return BootManager.class;
			} else {
				final String message = String.format(
						"Illegal access to preboot class%s!",
						PrebootClassLoader.LOG_ILLEGAL_CLASS ? "( " + name + ')' : ""
				);
				throw new SecurityException(message);
			}
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

	void defineClass(final String className, final byte[] bytes) {
		this.classes.put(className, this.defineClass(className, bytes, 0, bytes.length));
	}
}