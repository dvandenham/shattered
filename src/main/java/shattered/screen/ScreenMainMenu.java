package shattered.screen;

import org.jetbrains.annotations.NotNull;
import shattered.core.event.EventListener;
import shattered.Assets;
import shattered.Shattered;
import shattered.lib.gfx.FontRenderer;
import shattered.lib.gfx.Tessellator;
import shattered.lib.gui.IGuiScreen;
import shattered.lib.gui.Layout;
import shattered.lib.gui.component.GuiButton;

public final class ScreenMainMenu extends IGuiScreen {

	private final GuiButton buttonShutdown = new GuiButton("shattered.screen.main_menu.button.shutdown");

	public ScreenMainMenu() {
		this.add(this.buttonShutdown);
	}

	@Override
	public void setupComponents(@NotNull final Layout layout) {
		layout.setInverted();
		layout.add(this.buttonShutdown);
	}

	@Override
	protected void renderBackground(@NotNull final Tessellator tessellator, @NotNull final FontRenderer fontRenderer) {
	}

	@Override
	protected void renderForeground(@NotNull final Tessellator tessellator, @NotNull final FontRenderer fontRenderer) {
		tessellator.start();
		tessellator.set(this.getPosition(), Assets.TEXTURE_LOGO);
		tessellator.center(this.getWidth(), this.getHeight() / 4);
		tessellator.draw();
	}

	@EventListener(GuiButton.ButtonEvent.LeftClick.class)
	private void onButtonClicked(final GuiButton.ButtonEvent.LeftClick event) {
		if (event.get() == this.buttonShutdown) {
			Shattered.getInstance().stop();
		}
	}
}