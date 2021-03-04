package shattered.screen;

import org.jetbrains.annotations.NotNull;
import shattered.core.event.EventListener;
import shattered.Assets;
import shattered.Shattered;
import shattered.lib.gfx.FontRenderer;
import shattered.lib.gfx.Tessellator;
import shattered.lib.gui.Layout;
import shattered.lib.gui.component.GuiButton;

public final class ScreenMainMenu extends AbstractScreen {

	private final GuiButton buttonContinue = new GuiButton("screen.main_menu.button.continue");
	private final GuiButton buttonNewGame = new GuiButton("screen.main_menu.button.new_game");
	private final GuiButton buttonSettings = new GuiButton("screen.main_menu.button.settings");
	private final GuiButton buttonShutdown = new GuiButton("screen.main_menu.button.shutdown");

	public ScreenMainMenu() {
		this.add(this.buttonContinue);
		this.add(this.buttonNewGame);
		this.add(this.buttonSettings);
		this.add(this.buttonShutdown);
	}

	@Override
	public void setupComponents(@NotNull final Layout layout) {
		layout.setInverted();
		layout.add(this.buttonShutdown);
		layout.add(this.buttonSettings);
		layout.addEmptyRow();
		layout.add(this.buttonNewGame);
		layout.add(this.buttonContinue);
	}

	@Override
	protected void renderForeground(@NotNull final Tessellator tessellator, @NotNull final FontRenderer fontRenderer) {
		tessellator.start();
		tessellator.set(this.getPosition(), Assets.TEXTURE_LOGO);
		tessellator.scale(1.5F, 1.5F);
		tessellator.center(this.getWidth(), this.getHeight() / 4);
		tessellator.draw();
	}

	@EventListener(GuiButton.ButtonEvent.LeftClick.class)
	private void onButtonClicked(final GuiButton.ButtonEvent.LeftClick event) {
		final GuiButton button = event.get();
		if (button == this.buttonShutdown) {
			Shattered.getInstance().stop();
		}
	}
}