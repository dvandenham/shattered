package shattered.game;

import org.jetbrains.annotations.NotNull;
import shattered.core.event.EventBusSubscriber;
import shattered.core.event.EventListener;
import shattered.Shattered;
import shattered.game.world.WorldType;
import shattered.lib.ResourceLocation;
import shattered.lib.registry.CreateRegistryEvent;
import shattered.lib.registry.Registry;

@EventBusSubscriber(Shattered.SYSTEM_BUS_NAME)
public final class GameRegistries {

	private static Registry<WorldType> WORLD;

	@NotNull
	public static Registry<WorldType> WORLD() {
		return GameRegistries.WORLD;
	}

	@EventListener(CreateRegistryEvent.class)
	private static void onCreateRegistry(final CreateRegistryEvent event) {
		GameRegistries.WORLD = event.create(new ResourceLocation("world"), WorldType.class);
	}
}