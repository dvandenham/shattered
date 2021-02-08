package shattered.preboot;

final class PrebootClassLoader extends ClassLoader {

	private static final boolean     LOG_ILLEGAL_CLASS  = "true".equals(System.getProperty("shattered.preboot.log.illegal"));
	private final        ClassLoader systemLoader       = ClassLoader.getSystemClassLoader();
	private final        String      prebootPackageName = this.getClass().getPackage().getName();

	public PrebootClassLoader() {
		super(null);
	}

	@Override
	protected Class<?> findClass(final String name) throws ClassNotFoundException {
		if (name.startsWith("java") || name.startsWith("sun")) {
			return this.systemLoader.loadClass(name);
		} else if (name.startsWith(this.prebootPackageName) || name.equals("Main")) {
			final IllegalStateException exception = new IllegalStateException(
					"Illegal access to preboot classes" + (PrebootClassLoader.LOG_ILLEGAL_CLASS ? " " + name : "")
			);
			exception.setStackTrace(new StackTraceElement[0]);
			throw exception;
		} else {
			final Class<?> result = Preboot.loadClass(name);
			if (result == null) {
				throw new ClassNotFoundException(name);
			} else {
				return result;
			}
		}
	}

	Class<?> defineClass(final String name, final byte[] bytes) {
		return this.defineClass(name, bytes, 0, bytes.length);
	}
}