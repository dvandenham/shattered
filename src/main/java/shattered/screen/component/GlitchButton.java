package shattered.screen.component;

import org.jetbrains.annotations.NotNull;
import shattered.lib.gfx.FontRenderer;
import shattered.lib.gfx.RenderHelper;
import shattered.lib.gfx.StringData;
import shattered.lib.gfx.Tessellator;
import shattered.lib.gui.component.GuiButton;

public class GlitchButton extends GuiButton {

	public GlitchButton(@NotNull final String text) {
		super(text);
	}
	
	@Override
	protected void renderForeground(@NotNull final Tessellator tessellator, @NotNull final FontRenderer fontRenderer) {
		fontRenderer.setFontSize(Math.min(this.getHeight() / 4 * 3, 48));
		final int yOffset = 2;
		RenderHelper.writeGlitchedCentered(fontRenderer, this.getBounds().moveY(yOffset), new StringData(this.getText()).localize(this.doLocalize()));
		fontRenderer.revertFontSize();
	}
}