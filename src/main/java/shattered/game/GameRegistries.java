package shattered.game;

import shattered.Shattered;
import shattered.core.event.EventBusSubscriber;
import shattered.core.event.EventListener;
import shattered.game.entity.EntityType;
import shattered.game.tile.Tile;
import shattered.game.world.WorldType;
import shattered.lib.ResourceLocation;
import shattered.lib.registry.CreateRegistryEvent;
import shattered.lib.registry.Registry;
import org.jetbrains.annotations.NotNull;

@EventBusSubscriber(Shattered.SYSTEM_BUS_NAME)
public final class GameRegistries {

	private static Registry<EntityType> ENTITY;
	private static Registry<Tile> TILE;
	private static Registry<WorldType> WORLD;

	@NotNull
	public static Registry<EntityType> ENTITY() {
		return GameRegistries.ENTITY;
	}

	@NotNull
	public static Registry<Tile> TILE() {
		return GameRegistries.TILE;
	}

	@NotNull
	public static Registry<WorldType> WORLD() {
		return GameRegistries.WORLD;
	}

	@EventListener(CreateRegistryEvent.class)
	private static void onCreateRegistry(final CreateRegistryEvent event) {
		GameRegistries.ENTITY = event.create(new ResourceLocation("entity"), EntityType.class);
		GameRegistries.TILE = event.create(new ResourceLocation("tile"), Tile.class);
		GameRegistries.WORLD = event.create(new ResourceLocation("world"), WorldType.class, new ResourceLocation("tile"), new ResourceLocation("entity"));
	}
}