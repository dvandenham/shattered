package shattered.screen;

import org.jetbrains.annotations.NotNull;
import shattered.StaticAssets;
import shattered.lib.gfx.FontRenderer;
import shattered.lib.gfx.Tessellator;
import shattered.lib.gui.IGuiScreen;

abstract class AbstractScreen extends IGuiScreen {

	@Override
	protected void renderBackground(@NotNull final Tessellator tessellator, @NotNull final FontRenderer fontRenderer) {
		tessellator.drawQuick(this.getBounds(), StaticAssets.RESOURCE_TEXTURE_ARGON);
	}
}