package preboot;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Arrays;
import nl.appelgebakje22.preboot.PrebootRegistry;
import nl.appelgebakje22.preboot.PrebootRegistryImpl;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;

public final class Preboot {

	static final boolean DEVELOPER_MODE = Boolean.getBoolean("shattered.developer");
	static final String PREBOOT_PACKAGE_NAME = Preboot.class.getPackage().getName();
	static final Logger LOGGER = LogManager.getLogger("Preboot");

	public static void boot(final String[] args) {
		if (Preboot.DEVELOPER_MODE) {
			final LoggerContext context = (LoggerContext) LogManager.getContext(false);
			final Configuration configuration = context.getConfiguration();
			final LoggerConfig loggerConfig = configuration.getLoggerConfig(LogManager.ROOT_LOGGER_NAME);
			loggerConfig.setLevel(Level.ALL);
			context.updateLoggers();
		}

		final PrebootRegistryImpl booter = new PrebootRegistryImpl(new URL[]{
				Preboot.class.getProtectionDomain().getCodeSource().getLocation(),
		}, new Class[]{BootManager.class}, className -> !className.startsWith(Preboot.PREBOOT_PACKAGE_NAME));

		booter.registerTransformer(new TransformerEventBusSubscriber());
		booter.registerTransformer(new TransformerEventListener());
		booter.registerTransformer(new TransformerMessageListener());
		booter.registerTransformer(new TransformerJson());

		booter.load();

		try {
			final Class<?> bootClass = booter.getAnnotatedClasses(BootManager.class).get(0);
			final Method bootMethod = Arrays.stream(bootClass.getDeclaredMethods())
					.filter(method -> method.getReturnType().equals(Void.TYPE))
					.filter(method -> method.getParameterCount() == 2
							&& method.getParameterTypes()[0].equals(PrebootRegistry.class)
							&& method.getParameterTypes()[1].equals(String[].class))
					.findFirst()
					.orElseGet(() -> {
						Preboot.LOGGER.fatal("Fatal error during secondary boot stage!");
						Runtime.getRuntime().halt(-1);
						return null;
					});
			bootMethod.invoke(null, booter, args);
		} catch (Throwable cause) {
			if (Preboot.DEVELOPER_MODE) {
				if (cause instanceof InvocationTargetException) {
					cause = cause.getCause();
				}
				Preboot.LOGGER.fatal("Fatal error during secondary boot stage", cause);
			} else {
				Preboot.LOGGER.fatal("Fatal error during secondary boot stage!");
			}
			Runtime.getRuntime().halt(-1);
		}
	}
}