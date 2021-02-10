package shattered;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import shattered.core.event.EventBus;
import shattered.lib.ReflectionHelper;
import shattered.lib.registry.CreateRegistryEvent;

public final class Shattered {

	public static final String NAME = "Shattered";
	public static final Logger LOGGER = LogManager.getLogger(Shattered.NAME);
	private static Shattered instance;

	public static void start(final String[] args) {
		Shattered.LOGGER.info("{} is starting!", Shattered.NAME);
		Shattered.instance = new Shattered(args);
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