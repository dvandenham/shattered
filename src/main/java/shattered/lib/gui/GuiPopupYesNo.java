package shattered.lib.gui;

import shattered.Assets;
import shattered.core.event.Event;
import shattered.core.event.EventBus;
import shattered.core.event.EventListener;
import shattered.lib.Color;
import shattered.lib.gfx.FontRenderer;
import shattered.lib.gfx.StringData;
import shattered.lib.gfx.Tessellator;
import shattered.lib.gui.component.GuiButton;
import org.jetbrains.annotations.NotNull;

public class GuiPopupYesNo extends IGuiScreen {

	private final GuiButton buttonNo = new GuiButton("popup.yes_no.button.no", Color.BLACK);
	private final GuiButton buttonYes = new GuiButton("popup.yes_no.button.yes", Color.BLACK);
	@NotNull
	private final String identifier, description;

	public GuiPopupYesNo(@NotNull final String identifier, @NotNull final String description) {
		this.setHasTitlebar(false);
		this.setBounds(-1, -1, -2, -2);
		this.setMaxSize(480, 320);
		this.identifier = identifier;
		this.description = description;
		this.add(this.buttonYes);
		this.add(this.buttonNo);
	}


	@Override
	public void setupComponents(@NotNull final Layout layout) {
		layout.setInverted();
		layout.add(this.buttonYes, this.buttonNo);
	}

	@Override
	protected void renderBackground(@NotNull final Tessellator tessellator, @NotNull final FontRenderer fontRenderer) {
		GuiHelper.renderGuiPanel(tessellator, this.getBounds());
	}

	@Override
	protected void renderForeground(@NotNull final Tessellator tessellator, @NotNull final FontRenderer fontRenderer) {
		fontRenderer.setFont(Assets.FONT_SIMPLE);
		fontRenderer.setFontSize(24);
		fontRenderer.writeQuick(this.getInternalX(), this.getInternalY(), new StringData(this.description, Color.BLACK).centerX(this.getInternalWidth()).wrap(this.getInternalWidth()));
		fontRenderer.revertFontSize();
		fontRenderer.resetFont();
	}

	@EventListener(GuiButton.ButtonEvent.LeftClick.class)
	public void onButtonClicked(final GuiButton.ButtonEvent.LeftClick event) {
		if (event.get() == this.buttonNo) {
			this.closeScreen();
			EventBus.post(new ResultEvent(this.identifier, false));
		}
		if (event.get() == this.buttonYes) {
			this.closeScreen();
			EventBus.post(new ResultEvent(this.identifier, true));
		}
	}

	public static final class ResultEvent extends Event<String> {

		public final boolean answer;

		private ResultEvent(@NotNull final String identifier, final boolean answer) {
			super(identifier);
			this.answer = answer;
		}
	}
}