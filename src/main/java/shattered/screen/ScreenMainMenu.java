package shattered.screen;

import shattered.Assets;
import shattered.Shattered;
import shattered.core.event.EventListener;
import shattered.lib.ResourceLocation;
import shattered.lib.gui.Layout;
import shattered.lib.gui.component.GuiButton;
import shattered.lib.gui.component.GuiTextureLabel;
import shattered.screen.component.GlitchButton;
import org.jetbrains.annotations.NotNull;

public final class ScreenMainMenu extends AbstractScreen {

	private final GuiTextureLabel labelLogo = (GuiTextureLabel) new GuiTextureLabel(Assets.TEXTURE_LOGO).setUseAspectY(true).setMaximumWidth(768);
	private final GuiButton buttonContinue = new GlitchButton("screen.main_menu.button.continue");
	private final GuiButton buttonSaveList = new GlitchButton("screen.main_menu.button.save_list");
	private final GuiButton buttonNewGame = new GlitchButton("screen.main_menu.button.new_game");
	private final GuiButton buttonSettings = new GlitchButton("screen.main_menu.button.settings");
	private final GuiButton buttonShutdown = new GlitchButton("screen.main_menu.button.shutdown");

	public ScreenMainMenu() {
		this.setHasTitlebar(false);
		this.add(this.labelLogo);
		this.add(this.buttonContinue);
		this.add(this.buttonSaveList);
		this.add(this.buttonNewGame);
		this.add(this.buttonSettings);
		this.add(this.buttonShutdown);
	}

	@Override
	public void setupComponents(@NotNull final Layout layout) {
		final Layout logoLayout = layout.recreate();
		logoLayout.addEmptyRow();
		logoLayout.add(this.labelLogo);

		layout.setInverted();
		layout.add(this.buttonShutdown);
		layout.add(this.buttonSettings);
		layout.addEmptyRow();
		layout.add(this.buttonNewGame);
		layout.add(this.buttonSaveList);
		layout.add(this.buttonContinue);
	}

	@EventListener(GuiButton.ButtonEvent.LeftClick.class)
	private void onButtonClicked(final GuiButton.ButtonEvent.LeftClick event) {
		final GuiButton button = event.get();
		if (button == this.buttonContinue) {
			//TODO implement this
		} else if (button == this.buttonSaveList) {
			this.openScreen(new ScreenSaveList());
		} else if (button == this.buttonNewGame) {
			//TODO temporary
			Shattered.getInstance().getGameManager().loadWorld(new ResourceLocation("1-1"));
			this.closeScreen();
		} else if (button == this.buttonSettings) {
			this.openScreen(new ScreenSettings());
		} else if (button == this.buttonShutdown) {
			Shattered.getInstance().stop();
		}
	}
}