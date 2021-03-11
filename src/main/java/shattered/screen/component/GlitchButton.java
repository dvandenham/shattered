package shattered.screen.component;

import shattered.lib.Color;
import shattered.lib.gfx.FontRenderer;
import shattered.lib.gfx.RenderHelper;
import shattered.lib.gfx.StringData;
import shattered.lib.gfx.Tessellator;
import shattered.lib.gui.component.GuiButton;
import org.jetbrains.annotations.NotNull;

public class GlitchButton extends GuiButton {

	public GlitchButton(@NotNull final String text) {
		super(text);
	}

	@Override
	public void render(@NotNull final Tessellator tessellator, @NotNull final FontRenderer fontRenderer) {
		final Color bgColor;
		switch (this.state) {
			case ROLLOVER:
				bgColor = Color.RED;
				break;
			case LEFT_PRESS:
			case RIGHT_PRESS:
			case LEFT_CLICK:
			case RIGHT_CLICK:
				bgColor = Color.XEROS;
				break;
			default:
				bgColor = Color.WHITE;
				break;
		}
		tessellator.drawQuick(this.getBounds(), bgColor.withAlpha(0.5F));
		fontRenderer.setFontSize(Math.min(this.getHeight() / 4 * 3, 48));
		final int yOffset = 2;
		RenderHelper.writeGlitchedCentered(fontRenderer, this.getBounds().moveY(yOffset), new StringData(this.getText()).localize(this.doLocalize()));
		fontRenderer.revertFontSize();
	}
}