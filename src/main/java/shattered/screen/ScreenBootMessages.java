package shattered.screen;

import org.jetbrains.annotations.NotNull;
import shattered.Assets;
import shattered.BootMessageQueue;
import shattered.Shattered;
import shattered.lib.Color;
import shattered.lib.gfx.FontRenderer;
import shattered.lib.gfx.StringData;
import shattered.lib.gfx.Tessellator;
import shattered.lib.gui.IGuiScreen;
import shattered.lib.gui.Layout;

public final class ScreenBootMessages extends IGuiScreen {

	private final String[] textToRender;

	ScreenBootMessages() {
		final BootMessageQueue.BootMessage[] messages = Shattered.MESSAGES.getMessages();
		this.textToRender = new String[messages.length];
		for (int i = 0; i < messages.length; ++i) {
			this.textToRender[i] = "[" + messages[i].getSeverity() + ']' + messages[i].getMessage();
		}
	}

	@Override
	public void setupComponents(@NotNull final Layout layout) {
	}

	@Override
	protected void renderBackground(@NotNull final Tessellator tessellator, @NotNull final FontRenderer fontRenderer) {
		tessellator.drawQuick(this.getBounds(), Color.WHITE);
	}

	@Override
	protected void renderForeground(@NotNull final Tessellator tessellator, @NotNull final FontRenderer fontRenderer) {
		fontRenderer.setFont(Assets.FONT_SIMPLE);
		fontRenderer.setFontSize(16);
		fontRenderer.writeQuick(this.getInternalPosition(), new StringData(this.textToRender, Color.BLACK).wrap(this.getInternalWidth()).localize(false));
		fontRenderer.revertFontSize();
		fontRenderer.resetFont();
	}
}