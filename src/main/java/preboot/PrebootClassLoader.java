package preboot;

import java.net.URL;
import java.util.HashMap;
import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import org.jetbrains.annotations.NotNull;

public final class PrebootClassLoader extends ClassLoader {

	private final ClassLoader parentLoader = this.getClass().getClassLoader();
	private final ObjectArraySet<IClassLoaderHook> hooks = new ObjectArraySet<>(16);
	final HashMap<String, Class<?>> classes = new HashMap<>();

	PrebootClassLoader() {
		super(null);
	}

	@Override
	public Class<?> loadClass(final String name, final boolean resolve) throws ClassNotFoundException {
		if (name.startsWith("java.")) {
			return super.loadClass(name, resolve);
		} else if (name.startsWith("javax.") || name.startsWith("sun.")) {
			return this.parentLoader.loadClass(name);
		} else {
			final Class<?> result = this.loadClass2(name);
			this.hooks.forEach(hook -> hook.execute(result));
			return result;
		}
	}

	private Class<?> loadClass2(final String name) throws ClassNotFoundException {
		if (name.startsWith(Preboot.PREBOOT_PACKAGE_NAME)) {
			if (name.equals(BootManager.class.getName())) {
				return BootManager.class;
			} else if (name.equals(IClassLoaderHook.class.getName())) {
				return IClassLoaderHook.class;
			} else if (name.equals(IClassLoaderClassLoadedHook.class.getName())) {
				return IClassLoaderClassLoadedHook.class;
			} else if (name.equals(IClassLoadedInstantiationHook.class.getName())) {
				return IClassLoadedInstantiationHook.class;
			} else if (name.equals(PrebootClassLoader.class.getName())) {
				return PrebootClassLoader.class;
			} else if (name.equals(AnnotationRegistry.class.getName())) {
				return AnnotationRegistry.class;
			} else {
				final String message = String.format(
						"Illegal access to preboot class%s!",
						Preboot.DEVELOPER_MODE ? " (" + name + ')' : ""
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

	public void registerHook(@NotNull final IClassLoaderHook hook) {
		this.hooks.add(hook);
	}

	public static Object onInstantiation(final Object obj, final Class<?> clazz) {
		Preboot.LOADER.hooks.forEach(hook -> hook.execute(new Object[]{clazz, obj}));
		return obj;
	}

	void defineClass(final String className, final byte[] bytes) {
		this.classes.put(className, this.defineClass(className, bytes, 0, bytes.length));
	}

	@Override
	protected URL findResource(final String name) {
		return this.parentLoader.getResource(name);
	}
}