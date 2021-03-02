package shattered.lib.gui.component;

import org.jetbrains.annotations.NotNull;
import shattered.lib.Color;
import shattered.lib.Input;
import shattered.lib.event.Event;
import shattered.lib.event.EventBus;
import shattered.lib.gfx.FontRenderer;
import shattered.lib.gfx.StringData;
import shattered.lib.gfx.Tessellator;
import shattered.lib.gui.IGuiComponent;

public class GuiButton extends IGuiComponent {

	enum ButtonState {
		DISABLED,
		DEFAULT,
		ROLLOVER,
		LEFT_PRESS,
		RIGHT_PRESS,
		LEFT_CLICK,
		RIGHT_CLICK,
	}

	private ButtonState state = ButtonState.DEFAULT;
	private ButtonState prevState = this.state;

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
		this(text, Color.WHITE);
	}

	@Override
	protected void tick() {
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
		}
	}

	@Override
	protected void renderBackground(@NotNull final Tessellator tessellator, @NotNull final FontRenderer fontRenderer) {

	}

	@Override
	protected void renderForeground(@NotNull final Tessellator tessellator, @NotNull final FontRenderer fontRenderer) {
		fontRenderer.writeQuickCentered(this.getBounds(), new StringData(this.text).localize(this.localize));
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