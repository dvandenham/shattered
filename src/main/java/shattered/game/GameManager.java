package shattered.game;

import java.io.IOException;
import shattered.Config;
import shattered.Keybinds;
import shattered.Shattered;
import shattered.core.event.EventBusSubscriber;
import shattered.core.event.MessageEvent;
import shattered.core.event.MessageListener;
import shattered.game.entity.Entity;
import shattered.game.entity.EntityAction;
import shattered.game.world.World;
import shattered.game.world.WorldType;
import shattered.lib.IKeyListener;
import shattered.lib.Input;
import shattered.lib.KeyBind;
import shattered.lib.ReflectionHelper;
import shattered.lib.ResourceLocation;
import shattered.lib.gfx.Display;
import shattered.lib.gfx.FontRenderer;
import shattered.lib.gfx.Tessellator;
import shattered.lib.gui.GuiManager;
import shattered.lib.gui.IGuiScreen;
import shattered.screen.ScreenInGamePaused;
import shattered.screen.ScreenMainMenu;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class GameManager implements IKeyListener {

	private final SaveManager saveManager;
	private World runningWorld = null;
	private IGuiScreen screenPaused;

	private GameManager() {
		this.saveManager = new SaveManager(this, Shattered.WORKSPACE.getDataFile("saves"));
	}

	public boolean loadWorld(@NotNull final ResourceLocation resource) {
		//TODO handle saves
		final World world = this.createWorld(resource);
		if (world == null) {
			return false;
		} else {
			this.runningWorld = world;
			//TODO move this
			Shattered.getInstance().getGuiManager().closeAllScreens();
			return true;
		}
	}

	@Nullable
	World createWorld(@NotNull final ResourceLocation resource) {
		final WorldType type = GameRegistries.WORLD().get(resource);
		return type != null ? ReflectionHelper.instantiate(World.class, ResourceLocation.class, resource, WorldType.class, type) : null;
	}

	public void pause() {
		this.screenPaused = ReflectionHelper.instantiate(ScreenInGamePaused.class);
		assert this.screenPaused != null;
		Shattered.getInstance().getGuiManager().openScreen(this.screenPaused);
	}

	public void unpause() {
		if (this.screenPaused != null) {
			Shattered.getInstance().getGuiManager().closeScreen(this.screenPaused);
		}
		this.screenPaused = null;
	}

	public void stop() {
		try {
			this.saveManager.serializeWorld(this.runningWorld);
		} catch (final IOException | InvalidSaveException e) {
			//TODO handle error
			e.printStackTrace();
		}
		this.runningWorld = null;
		final GuiManager manager = Shattered.getInstance().getGuiManager();
		if (this.screenPaused != null) {
			manager.closeScreen(this.screenPaused);
		}
		//TODO move this
		manager.openScreen(new ScreenMainMenu());
	}

	public boolean isRunning() {
		return this.runningWorld != null;
	}

	public void tick() {
		if (this.screenPaused == null) {
			//TODO handle errors, backup when possible
			this.handleMovement();
			this.runningWorld.tick();
		}
	}

	public void render(@NotNull final Tessellator tessellator, @NotNull final FontRenderer fontRenderer) {
		//TODO handle errors, backup when possible
		Display.setLogicalResolution(600, 480);
		this.runningWorld.render(tessellator, fontRenderer);
		Display.resetLogicalResolution();
	}

	private void handleMovement() {
		final Entity player = this.runningWorld.getPlayer();
		//Moving up and down cannot happen simultaneously
//		if (Input.isKeyDown(Config.)) {
//			player.move(Direction.UP);
//		} else if (keybind == Keybinds.gameMoveDown) {
//			player.move(Direction.DOWN);
//		}
		//Moving left and right cannot happen simultaneously
		if (Input.isKeyDown(Config.KEY_GAME_LEFT.get())) {
			player.move(Direction.LEFT);
		} else if (Input.isKeyDown(Config.KEY_GAME_RIGHT.get())) {
			player.move(Direction.RIGHT);
		}
	}

	@Override
	public void onKeybindChanged(@NotNull final KeyBind keybind) {
		if (this.isRunning()) {
			if (this.screenPaused != null) {
				if (keybind == Keybinds.gamePause) {
					this.unpause();
				}
			} else {
				if (keybind == Keybinds.gamePause) {
					this.pause();
				} else {
					final Entity player = this.runningWorld.getPlayer();
					if (keybind == Keybinds.gameJump) {
						player.execute(EntityAction.JUMPING, Entity.JUMP_TIMER);
					}
				}
			}
		}
	}

	@NotNull
	public SaveManager getSaveManager() {
		return this.saveManager;
	}

	@EventBusSubscriber(Shattered.SYSTEM_BUS_NAME)
	private static class EventHandler {

		@MessageListener("init_game_manager")
		public static void onInitGameManager(final MessageEvent event) {
			event.setResponse(GameManager::new);
		}
	}
}