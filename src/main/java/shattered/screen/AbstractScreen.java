package shattered.screen;

import org.jetbrains.annotations.NotNull;
import shattered.Assets;
import shattered.lib.gfx.FontRenderer;
import shattered.lib.gfx.Tessellator;
import shattered.lib.gui.IGuiScreen;

abstract class AbstractScreen extends IGuiScreen {

	@Override
	protected void renderBackground(@NotNull final Tessellator tessellator, @NotNull final FontRenderer fontRenderer) {
		tessellator.drawQuick(this.getBounds(), Assets.TEXTURE_ARGON);
	}
}