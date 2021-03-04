package shattered.game;

import shattered.core.event.EventBusSubscriber;
import shattered.core.event.EventListener;
import shattered.Shattered;
import shattered.lib.registry.CreateRegistryEvent;

@EventBusSubscriber(Shattered.SYSTEM_BUS_NAME)
public final class GameRegistries {

	@EventListener(CreateRegistryEvent.class)
	private static void onCreateRegistry(final CreateRegistryEvent event) {
	}
}