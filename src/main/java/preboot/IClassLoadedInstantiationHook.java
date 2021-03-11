package preboot;

public interface IClassLoadedInstantiationHook extends IClassLoaderHook {

	void onInstantiation(Class<?> clazz, Object instance);

	@Override
	default void execute(final Object data) {
		if (data instanceof Object[]) {
			final Object[] data2 = (Object[]) data;
			this.onInstantiation((Class<?>) data2[0], data2[1]);
		}
	}
}