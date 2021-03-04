package shattered.screen;

import org.jetbrains.annotations.NotNull;
import shattered.Shattered;
import shattered.lib.gfx.FontRenderer;
import shattered.lib.gfx.Tessellator;
import shattered.lib.gui.IGuiScreen;
import shattered.lib.gui.Layout;

public final class ScreenInGame extends IGuiScreen {

	private ScreenInGame() {
	}

	@Override
	public void setupComponents(@NotNull final Layout layout) {
	}

	@Override
	protected void renderBackground(@NotNull final Tessellator tessellator, @NotNull final FontRenderer fontRenderer) {
		Shattered.getInstance().gameManager.render(tessellator, fontRenderer);
	}

	@Override
	protected void renderForeground(@NotNull final Tessellator tessellator, @NotNull final FontRenderer fontRenderer) {
	}
}
