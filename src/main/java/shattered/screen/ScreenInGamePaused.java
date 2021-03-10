package shattered.screen;

import shattered.Shattered;
import shattered.core.event.EventListener;
import shattered.lib.Color;
import shattered.lib.gfx.Display;
import shattered.lib.gfx.FontRenderer;
import shattered.lib.gfx.RenderHelper;
import shattered.lib.gfx.Tessellator;
import shattered.lib.gui.IGuiScreen;
import shattered.lib.gui.Layout;
import shattered.lib.gui.component.GuiButton;
import shattered.screen.component.GlitchButton;
import org.jetbrains.annotations.NotNull;

public final class ScreenInGamePaused extends IGuiScreen {

	private final GuiButton buttonResume = new GlitchButton("screen.in_game.paused.button.resume");
	private final GuiButton buttonSave = new GlitchButton("screen.in_game.paused.button.save");
	private final GuiButton buttonExit = new GlitchButton("screen.in_game.paused.button.exit");

	private ScreenInGamePaused() {
		this.setHasTitlebar(false);
		this.setMaxSize(640, 400);
		this.add(this.buttonResume);
		this.add(this.buttonSave);
		this.add(this.buttonExit);
	}

	@Override
	public void setupComponents(@NotNull final Layout layout) {
		layout.addEmptyRow();
		layout.addEmptyRow();
		layout.add(this.buttonResume);
		layout.add(this.buttonSave);
		layout.add(this.buttonExit);
	}

	@Override
	protected void renderBackground(@NotNull final Tessellator tessellator, @NotNull final FontRenderer fontRenderer) {
		tessellator.drawQuick(Display.getBounds(), Color.BLACK.withAlpha(0.5F));
	}

	@Override
	protected void renderForeground(@NotNull final Tessellator tessellator, @NotNull final FontRenderer fontRenderer) {
		RenderHelper.writeGlitchedCentered(
				fontRenderer,
				this.getBounds().setHeight(this.getHeight() / 4),
				"screen.in_game.paused.label.paused"
		);
	}

	@EventListener(GuiButton.ButtonEvent.LeftClick.class)
	private void onButtonClicked(final GuiButton.ButtonEvent.LeftClick event) {
		if (event.get() == this.buttonResume) {
			Shattered.getInstance().getGameManager().unpause();
		} else if (event.get() == this.buttonSave) {
			Shattered.getInstance().getGameManager().stop(true);
		} else if (event.get() == this.buttonExit) {
			Shattered.getInstance().getGameManager().stop(false);
		}
	}
}