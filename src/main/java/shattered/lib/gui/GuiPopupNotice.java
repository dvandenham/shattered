package shattered.lib.gui;

import shattered.Assets;
import shattered.core.event.EventListener;
import shattered.lib.Color;
import shattered.lib.gfx.FontRenderer;
import shattered.lib.gfx.StringData;
import shattered.lib.gfx.Tessellator;
import shattered.lib.gui.component.GuiButton;
import org.jetbrains.annotations.NotNull;

public class GuiPopupNotice extends IGuiScreen {

	private final GuiButton buttonClose = new GuiButton("popup.notice.button.close", Color.BLACK);
	@NotNull
	private final StringData message;
	private final boolean error;

	public GuiPopupNotice(@NotNull final StringData message, final boolean error) {
		this.setHasTitlebar(false);
		this.setBounds(-1, -1, -2, -2);
		this.setMaxSize(480, 320);
		this.message = new StringData(message.getText(), error ? Color.WHITE : Color.BLACK).localize(false);
		this.error = error;
		this.add(this.buttonClose);
	}

	public GuiPopupNotice(@NotNull final String message, final boolean error) {
		this.setHasTitlebar(false);
		this.setBounds(-1, -1, -2, -2);
		this.setMaxSize(480, 320);
		this.message = new StringData(message, error ? Color.WHITE : Color.BLACK);
		this.error = error;
		this.add(this.buttonClose);
	}

	@Override
	public void setupComponents(@NotNull final Layout layout) {
		layout.setInverted();
		layout.add(this.buttonClose);
	}

	@Override
	protected void renderBackground(@NotNull final Tessellator tessellator, @NotNull final FontRenderer fontRenderer) {
		GuiHelper.renderGuiPanel(tessellator, this.getBounds(), this.error ? Color.RED : null);
	}

	@Override
	protected void renderForeground(@NotNull final Tessellator tessellator, @NotNull final FontRenderer fontRenderer) {
		fontRenderer.setFont(Assets.FONT_SIMPLE);
		fontRenderer.setFontSize(24);
		fontRenderer.writeQuick(
				this.getInternalX(), this.getInternalY(),
				this.message.centerX(this.getInternalWidth()).wrap(this.getInternalWidth())
		);
		fontRenderer.revertFontSize();
		fontRenderer.resetFont();
	}

	@EventListener(GuiButton.ButtonEvent.LeftClick.class)
	public void onButtonClicked(final GuiButton.ButtonEvent.LeftClick event) {
		if (event.get() == this.buttonClose) {
			this.closeScreen();
		}
	}
}