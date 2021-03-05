package shattered.screen;

import org.jetbrains.annotations.NotNull;
import shattered.Assets;
import shattered.lib.Color;
import shattered.lib.gfx.Display;
import shattered.lib.gfx.FontRenderer;
import shattered.lib.gfx.Tessellator;
import shattered.lib.gui.IGuiScreen;

abstract class AbstractScreen extends IGuiScreen {

	public AbstractScreen(@NotNull final String title) {
		super(title);
	}

	public AbstractScreen() {
	}

	@Override
	protected void renderBackground(@NotNull final Tessellator tessellator, @NotNull final FontRenderer fontRenderer) {
		if (!this.isFullscreen()) {
			tessellator.drawQuick(Display.getBounds(), Color.DARK_GRAY.withAlpha(0.75F));
		}
		tessellator.drawQuick(this.getBounds(), Assets.TEXTURE_ARGON);
	}
}