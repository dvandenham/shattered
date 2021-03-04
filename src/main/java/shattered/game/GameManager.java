package shattered.game;

import org.jetbrains.annotations.NotNull;
import shattered.core.event.EventBusSubscriber;
import shattered.core.event.MessageEvent;
import shattered.core.event.MessageListener;
import shattered.Shattered;
import shattered.game.world.World;
import shattered.game.world.WorldType;
import shattered.lib.ReflectionHelper;
import shattered.lib.ResourceLocation;
import shattered.lib.gfx.FontRenderer;
import shattered.lib.gfx.Tessellator;
import shattered.screen.ScreenInGame;

public final class GameManager {

	private World runningWorld = null;

	private GameManager() {
	}

	public boolean loadWorld(@NotNull final ResourceLocation resource) {
		final WorldType type = GameRegistries.WORLD().get(resource);
		if (type == null) {
			return false;
		}
		//TODO handle saves
		final World world = ReflectionHelper.instantiate(World.class, ResourceLocation.class, resource, WorldType.class, type);
		if (world == null) {
			return false;
		}
		this.runningWorld = world;
		this.openInGameScreen();
		return true;
	}

	private void openInGameScreen() {
		final ScreenInGame screen = ReflectionHelper.instantiate(ScreenInGame.class);
		assert screen != null;
		Shattered.getInstance().getGuiManager().openScreen(screen);
	}

	public boolean isRunning() {
		return this.runningWorld != null;
	}

	public void tick() {
		//TODO handle errors, backup when possible
		this.runningWorld.tick();
	}

	public void render(@NotNull final Tessellator tessellator, @NotNull final FontRenderer fontRenderer) {
		//TODO handle errors, backup when possible
		this.runningWorld.render(tessellator, fontRenderer);
	}

	@EventBusSubscriber(Shattered.SYSTEM_BUS_NAME)
	private static class EventHandler {

		@MessageListener("init_game_manager")
		public static void onInitGameManager(final MessageEvent event) {
			event.setResponse(GameManager::new);
		}
	}
}