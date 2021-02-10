package shattered;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import preboot.AnnotationRegistry;
import preboot.BootManager;
import shattered.core.event.EventBus;
import shattered.core.event.EventBusSubscriber;
import shattered.lib.ReflectionHelper;
import shattered.lib.registry.CreateRegistryEvent;

@BootManager
public final class Shattered {

	public static final String NAME = "Shattered";
	public static final Logger LOGGER = LogManager.getLogger(Shattered.NAME);
	private static Shattered instance;

	@SuppressWarnings("unused")
	public static void start(final String[] args) {
		Shattered.LOGGER.info("{} is starting!", Shattered.NAME);
		//Register all automatic EventBus subscribers
		AnnotationRegistry.getAnnotatedClasses(EventBusSubscriber.class).forEach(listener ->
				EventBus.register(listener, listener.getDeclaredAnnotation(EventBusSubscriber.class).value())
		);
	}

	private Shattered(final String[] args) {
		this.initRegistries();
	}

	private void initRegistries() {
		final CreateRegistryEvent event = ReflectionHelper.instantiate(CreateRegistryEvent.class);
		assert event != null;
		EventBus.post(event);
	}

	public static Shattered getInstance() {
		return Shattered.instance;
	}
}