package shattered.lib.gui.component;

import shattered.Assets;
import shattered.core.ITickable;
import shattered.core.event.Event;
import shattered.core.event.EventBus;
import shattered.lib.Color;
import shattered.lib.Input;
import shattered.lib.gfx.FontRenderer;
import shattered.lib.gfx.StringData;
import shattered.lib.gfx.Tessellator;
import shattered.lib.gui.GuiManager;
import shattered.lib.gui.IGuiComponent;
import org.jetbrains.annotations.NotNull;

public class GuiButton extends IGuiComponent implements ITickable {

	public enum ButtonState {
		DISABLED,
		DEFAULT,
		ROLLOVER,
		LEFT_PRESS,
		RIGHT_PRESS,
		LEFT_CLICK,
		RIGHT_CLICK,
	}

	protected ButtonState state = ButtonState.DEFAULT;
	protected ButtonState prevState = this.state;

	@NotNull
	private String text;
	@NotNull
	private Color textColor;
	private boolean localize = true;

	public GuiButton(@NotNull final String text, @NotNull final Color textColor) {
		this.text = text;
		this.textColor = textColor;
	}

	public GuiButton(@NotNull final String text) {
		this(text, Color.BLACK);
	}

	@Override
	public void tick() {
		if (this.state != this.prevState) {
			this.prevState = this.state;
			final ButtonEvent event;
			if (!this.isEnabled()) {
				event = new ButtonEvent.Disabled(this);
			} else {
				switch (this.state) {
					case DEFAULT:
						event = new ButtonEvent.Default(this);
						break;
					case ROLLOVER:
						event = new ButtonEvent.Rollover(this);
						break;
					case LEFT_PRESS:
						event = new ButtonEvent.LeftPress(this);
						break;
					case RIGHT_PRESS:
						event = new ButtonEvent.RightPress(this);
						break;
					case LEFT_CLICK:
						event = new ButtonEvent.LeftClick(this);
						break;
					case RIGHT_CLICK:
						event = new ButtonEvent.RightClick(this);
						break;
					default:
						event = null;
						break;
				}
			}
			if (event != null) {
				EventBus.post(event);
			}
		}
		if (!this.isEnabled()) {
			this.state = ButtonState.DISABLED;
		} else if (!this.containsMouse()) {
			this.state = ButtonState.DEFAULT;
		} else {
			if (Input.isMouseLeftClicked()) {
				this.state = ButtonState.LEFT_CLICK;
			} else if (Input.isMouseRightClicked()) {
				this.state = ButtonState.RIGHT_CLICK;
			} else if (Input.isMouseLeftPressed()) {
				this.state = ButtonState.LEFT_PRESS;
			} else if (Input.isMouseRightPressed()) {
				this.state = ButtonState.RIGHT_PRESS;
			} else {
				this.state = ButtonState.ROLLOVER;
			}
			if (this.state != this.prevState) {
				switch (this.state) {
					case LEFT_CLICK:
					case RIGHT_CLICK:
						GuiManager.playAudio(Assets.AUDIO_UI_CLICK);
						break;
				}
			}
		}
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
		fontRenderer.setFont(Assets.FONT_SIMPLE);
		fontRenderer.setFontSize(Math.min(this.getHeight() / 4 * 3, 48));
		final int yOffset = 2;
		fontRenderer.writeQuickCentered(this.getBounds().moveY(yOffset), new StringData(this.text, this.textColor).localize(this.localize));
		fontRenderer.revertFontSize();
		fontRenderer.resetFont();
	}

	public final GuiButton setText(@NotNull final String text) {
		this.text = text;
		return this;
	}

	public final GuiButton setTextColor(@NotNull final Color textColor) {
		this.textColor = textColor;
		return this;
	}

	public final GuiButton localize(final boolean localize) {
		this.localize = localize;
		return this;
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

	public static abstract class ButtonEvent extends Event<GuiButton> {

		private ButtonEvent(@NotNull final GuiButton button) {
			super(button);
		}

		public static final class Disabled extends ButtonEvent {

			private Disabled(@NotNull final GuiButton button) {
				super(button);
			}
		}

		public static final class Default extends ButtonEvent {

			private Default(@NotNull final GuiButton button) {
				super(button);
			}
		}

		public static final class Rollover extends ButtonEvent {

			private Rollover(@NotNull final GuiButton button) {
				super(button);
			}
		}

		public static final class LeftPress extends ButtonEvent {

			private LeftPress(@NotNull final GuiButton button) {
				super(button);
			}
		}

		public static final class RightPress extends ButtonEvent {

			private RightPress(@NotNull final GuiButton button) {
				super(button);
			}
		}

		public static final class LeftClick extends ButtonEvent {

			private LeftClick(@NotNull final GuiButton button) {
				super(button);
			}
		}

		public static final class RightClick extends ButtonEvent {

			private RightClick(@NotNull final GuiButton button) {
				super(button);
			}
		}
	}
}