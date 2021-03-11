package shattered.lib.gui;

import java.util.function.BiConsumer;
import shattered.Shattered;
import shattered.core.ITickable;
import shattered.core.event.EventBus;
import shattered.core.event.EventBusSubscriber;
import shattered.core.event.MessageEvent;
import shattered.core.event.MessageListener;
import shattered.lib.Helper;
import shattered.lib.ResourceLocation;
import shattered.lib.audio.AudioPlayer;
import shattered.lib.audio.SoundSystem;
import shattered.lib.gfx.FontRenderer;
import shattered.lib.gfx.Tessellator;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@EventBusSubscriber(Shattered.SYSTEM_BUS_NAME)
public final class GuiManager {

	private final ObjectArrayList<IGuiScreen> screens = new ObjectArrayList<>();
	private final AudioPlayer audioPlayer = SoundSystem.createPlayer();

	private GuiManager() {
	}

	public void openScreen(@NotNull final IGuiScreen screen) {
		if (this.screens.contains(screen)) {
			return;
		}
		EventBus.register(screen);

		if (EventBus.post(new ScreenEvent.Opening(screen))) {
			//Screen opening has been rejected somewhere
			EventBus.unregister(screen);
			return;
		}

		final IGuiScreen lastScreen = this.getLastScreen();
		if (lastScreen != null) {
			this.tickScreen(lastScreen);
		}
		this.screens.add(screen);
		screen.manager = this;
		screen.cacheBounds();
		this.setupComponents(screen);
		EventBus.post(new ScreenEvent.Opened(screen));
	}

	public void closeScreen(@NotNull final IGuiScreen screen) {
		if (!this.screens.contains(screen)) {
			return;
		}

		if (EventBus.post(new ScreenEvent.Closing(screen))) {
			//Screen closing has been rejected somewhere
			return;
		}

		final boolean isLastScreen = this.screens.indexOf(screen) == this.screens.size() - 1;

		this.screens.remove(screen);
		screen.manager = null;
		EventBus.post(new ScreenEvent.Closed(screen));
		EventBus.unregister(screen);

		if (isLastScreen && !this.screens.isEmpty()) {
			final IGuiScreen newScreen = this.getLastScreen();
			assert newScreen != null;
			this.setupComponents(newScreen);
			EventBus.post(new ScreenEvent.Opened(newScreen));
		}
	}

	public void closeLastScreen() {
		if (this.screens.size() > 1) {
			final IGuiScreen screen = this.getLastScreen();
			assert screen != null;
			this.closeScreen(screen);
		}
	}

	public void closeAllScreens() {
		while (!this.screens.isEmpty()) {
			final IGuiScreen screen = this.getLastScreen();
			assert screen != null;
			this.closeScreen(screen);
		}
	}

	private void tick() {
		final IGuiScreen screen = this.getLastScreen();
		if (screen == null) {
			return;
		}
		this.tickScreen(screen);
	}

	private void render(@NotNull final Tessellator tessellator, @NotNull final FontRenderer fontRenderer) {
		int renderStartIndex = 0;
		for (int i = this.screens.size() - 1; i >= 0; --i) {
			if (this.screens.get(i).isFullscreen()) {
				renderStartIndex = i;
				break;
			}
		}
		for (int i = renderStartIndex; i < this.screens.size(); ++i) {
			final IGuiScreen screen = this.screens.get(i);
			this.renderScreen(screen, tessellator, fontRenderer);
		}
	}

	private void tickScreen(@NotNull final IGuiScreen screen) {
		if (screen instanceof ITickable) {
			((ITickable) screen).tick();
		}
		screen.doForAll(component -> ((ITickable) component).tick(), component -> component instanceof ITickable);
		screen.tickTitlebar();
	}

	private void renderScreen(@NotNull final IGuiScreen screen, @NotNull final Tessellator tessellator, @NotNull final FontRenderer fontRenderer) {
		screen.renderBackground(tessellator, fontRenderer);
		screen.doForAll(component -> component.render(tessellator, fontRenderer), Helper.testTrue());
		screen.renderForeground(tessellator, fontRenderer);
		screen.renderTitlebar(tessellator, fontRenderer);
	}

	void setupComponents(@NotNull final IGuiScreen screen) {
		final Layout layout = GuiHelper.createDefaultLayout(screen);
		screen.setupComponents(layout);
		screen.doForAll(component -> ((IComponentContainer) component).setupComponents(layout), component -> component instanceof IComponentContainer);
	}

	@Nullable
	private IGuiScreen getLastScreen() {
		return !this.screens.isEmpty() ? this.screens.get(this.screens.size() - 1) : null;
	}

	public static void playAudio(@NotNull final ResourceLocation resource) {
		Shattered.getInstance().getGuiManager().audioPlayer.play(resource);
	}

	@MessageListener("init_gui")
	private static void onMessageReceived(final MessageEvent event) {
		final GuiManager manager = new GuiManager();
		event.setResponse(() -> new Object[]{
				manager,
				(Runnable) manager::tick,
				(BiConsumer<Tessellator, FontRenderer>) manager::render
		});
	}
}