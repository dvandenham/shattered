package preboot;

public interface IClassLoaderClassLoadedHook extends IClassLoaderHook {

	void onClassLoaded(Class<?> clazz);

	@Override
	default void execute(final Object data) {
		if (data instanceof Class) {
			this.onClassLoaded((Class<?>) data);
		}
	}
}