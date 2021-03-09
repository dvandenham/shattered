package shattered.game;

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

public final class GameManager implements IKeyListener {

	private World runningWorld = null;
	private IGuiScreen screenPaused;

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
		//TODO move this
		Shattered.getInstance().getGuiManager().closeAllScreens();
		return true;
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
		//TODO serialize world
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
			this.runningWorld.tick();
		}
	}

	public void render(@NotNull final Tessellator tessellator, @NotNull final FontRenderer fontRenderer) {
		//TODO handle errors, backup when possible
		Display.setLogicalResolution(600, 480);
		this.runningWorld.render(tessellator, fontRenderer);
		Display.resetLogicalResolution();
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
					//Moving up and down cannot happen simultaneously
					if (keybind == Keybinds.gameMoveUp) {
						player.getBounds().moveY(Entity.MOVE_STRENGTH);
					} else if (keybind == Keybinds.gameMoveDown) {
						player.getBounds().moveY(-Entity.MOVE_STRENGTH);
					}

					//Moving left and right cannot happen simultaneously
					if (keybind == Keybinds.gameMoveLeft) {
						if (player.canMove(Direction.LEFT)) {
							player.getBounds().moveX(-Entity.MOVE_STRENGTH);
						}
					} else if (keybind == Keybinds.gameMoveRight) {
						if (player.canMove(Direction.RIGHT)) {
							player.getBounds().moveX(Entity.MOVE_STRENGTH);
						}
					}
				}
			}
		}
	}

	@EventBusSubscriber(Shattered.SYSTEM_BUS_NAME)
	private static class EventHandler {

		@MessageListener("init_game_manager")
		public static void onInitGameManager(final MessageEvent event) {
			event.setResponse(GameManager::new);
		}
	}
}