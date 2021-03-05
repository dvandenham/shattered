package shattered.lib.gui.component;

import org.jetbrains.annotations.NotNull;
import shattered.core.event.Event;
import shattered.core.event.EventBus;
import shattered.Assets;
import shattered.lib.Color;
import shattered.lib.Input;
import shattered.lib.gfx.FontRenderer;
import shattered.lib.gfx.StringData;
import shattered.lib.gfx.Tessellator;
import shattered.lib.gui.IGuiComponent;

public class GuiToggleButton extends IGuiComponent {

	private boolean currentState = false;
	private boolean previousState = false;

	@NotNull
	private String text;
	@NotNull
	private Color textColor;
	private boolean localize = true;

	public GuiToggleButton(@NotNull final String text, @NotNull final Color textColor) {
		this.text = text;
		this.textColor = textColor;
	}

	public GuiToggleButton(@NotNull final String text) {
		this(text, Color.WHITE);
	}

	@Override
	protected void tick() {
		if (this.currentState != this.previousState) {
			this.previousState = this.currentState;
			final ToggleButtonEvent event;
			if (!this.isEnabled()) {
				event = new ToggleButtonEvent.Disabled(this);
			} else {
				event = new ToggleButtonEvent.StateChanged(this);
			}
			EventBus.post(event);
		}
		if (!this.isEnabled()) {
			this.previousState = this.currentState = false;
		}
		if (this.containsMouse() && Input.isMouseLeftClicked()) {
			this.currentState = !this.currentState;
		}
	}

	@Override
	protected void renderBackground(@NotNull final Tessellator tessellator, @NotNull final FontRenderer fontRenderer) {
		final Color bgColor;
		if (!this.isEnabled()) {
			bgColor = Color.DARK_GRAY;
		} else if (this.currentState) {
			bgColor = Color.XEROS;
		} else {
			bgColor = Color.RED;
		}
		tessellator.drawQuick(this.getBounds(), bgColor.withAlpha(0.5F));
	}

	@Override
	protected void renderForeground(@NotNull final Tessellator tessellator, @NotNull final FontRenderer fontRenderer) {
		fontRenderer.setFont(Assets.FONT_SIMPLE);
		fontRenderer.setFontSize(Math.min(this.getHeight() / 4 * 3, 48));
		final int yOffset = 2;
		fontRenderer.writeQuickCentered(this.getBounds().moveY(yOffset), new StringData(this.text, this.textColor).localize(this.localize));
		fontRenderer.revertFontSize();
		fontRenderer.resetFont();
	}

	public GuiToggleButton setState(final boolean state) {
		this.previousState = this.currentState = state;
		return this;
	}

	public final GuiToggleButton setText(@NotNull final String text) {
		this.text = text;
		return this;
	}

	public final GuiToggleButton setTextColor(@NotNull final Color textColor) {
		this.textColor = textColor;
		return this;
	}

	public final GuiToggleButton localize(final boolean localize) {
		this.localize = localize;
		return this;
	}

	public boolean isToggled() {
		return this.currentState;
	}

	@NotNull
	public final String getText() {
		return this.text;
	}

	@NotNull
	public final Color getTextColor() {
		return this.textColor;
	}

	public final boolean doLocalize() {
		return this.localize;
	}

	public static abstract class ToggleButtonEvent extends Event<GuiToggleButton> {

		private ToggleButtonEvent(@NotNull final GuiToggleButton button) {
			super(button);
		}

		public static final class Disabled extends ToggleButtonEvent {

			private Disabled(@NotNull final GuiToggleButton button) {
				super(button);
			}
		}

		public static final class StateChanged extends ToggleButtonEvent {

			private StateChanged(@NotNull final GuiToggleButton button) {
				super(button);
			}
		}
	}
}