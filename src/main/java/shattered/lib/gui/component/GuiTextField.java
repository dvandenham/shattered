package shattered.lib.gui.component;

import shattered.Assets;
import shattered.core.ITickable;
import shattered.lib.Color;
import shattered.lib.Input;
import shattered.lib.KeyEvent;
import shattered.lib.gfx.FontRenderer;
import shattered.lib.gfx.RenderHelper;
import shattered.lib.gfx.StringData;
import shattered.lib.gfx.Tessellator;
import shattered.lib.gui.IGuiComponent;
import org.jetbrains.annotations.NotNull;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_BACKSPACE;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_ENTER;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_RIGHT;

public class GuiTextField extends IGuiComponent implements ITickable {

	private final StringBuilder builder = new StringBuilder();
	@NotNull
	private Color colorBorder = Color.WHITE;
	@NotNull
	private Color colorText = Color.WHITE;
	@NotNull
	private Color colorCaret = Color.XEROS;
	protected boolean focussed = false;
	protected int caretPos = 0;

	@Override
	public void tick() {
		if (Input.isMouseLeftClicked()) {
			if (this.containsMouse() && !this.focussed) {
				this.focussed = true;
				Input.setKeyManagerBlocked(true);
			}
			if (!this.containsMouse() && this.focussed) {
				this.focussed = false;
				Input.setKeyManagerBlocked(false);
			}
		}
		if (this.focussed) {
			while (Input.hasKeyEventQueued()) {
				final KeyEvent event = Input.nextQueuedKey();
				assert event != null;
				if (event.isPressed()) {
					final int keyCode = event.getKeyCode();
					final char keyChar = Input.getKeyChar(keyCode, Input.isKeyboardShiftDown());
					switch (keyCode) {
						case GLFW_KEY_BACKSPACE:
							if (this.builder.length() >= 1) {
								this.builder.deleteCharAt(this.caretPos - 1);
								--this.caretPos;
							}
							continue;
						case GLFW_KEY_ENTER:
							Input.setKeyManagerBlocked(false);
							this.focussed = false;
							continue;
						case GLFW_KEY_LEFT:
							if (this.caretPos > 0) {
								--this.caretPos;
							}
							continue;
						case GLFW_KEY_RIGHT:
							if (this.caretPos < this.builder.length()) {
								++this.caretPos;
							}
							continue;
					}
					if (keyChar != 0) {
						if (Character.isWhitespace(keyChar) && keyChar != ' ') {
							continue;
						}
						if (keyChar < 256) {
							this.builder.insert(this.caretPos++, keyChar);
						}
					}
				}
			}
		}
	}

	@Override
	public void render(@NotNull final Tessellator tessellator, @NotNull final FontRenderer fontRenderer) {
		tessellator.drawQuick(this.getBounds(), Color.BLACK.withAlpha(0.5F));
		RenderHelper.renderFrame(tessellator, this.getBounds(), this.getBorderSize(), this.focussed ? this.colorBorder : Color.DARK_GRAY);
		if (!this.getText().isEmpty()) {
			fontRenderer.setFont(Assets.FONT_SIMPLE);
			fontRenderer.setFontSize(24);
			final String textReversed = new StringBuilder(this.getText()).reverse().toString();
			final String textWrapped = fontRenderer.getWrappedText(
					new StringData(textReversed).localize(false).wrap(this.getWidth() - this.getBorderSize() * 8, true)
			)[0];
			if (textWrapped != null) {
				fontRenderer.start();
				fontRenderer.add(
						this.getX() + this.getBorderSize() * 3, this.getY(),
						new StringData(new StringBuffer(textWrapped).reverse().toString(), this.colorText).localize(false).centerY(this.getHeight()));
				fontRenderer.write();
			}
			if (this.focussed) {
				int caretX = this.getBorderSize() * 4 + fontRenderer.getWidth(new StringData(this.getTextBeforeCaret()).localize(false));
				if (caretX > this.getWidth() - this.getBorderSize() * 6) {
					caretX = this.getWidth() - this.getBorderSize() * 6;
				}
				this.renderCaret(tessellator, caretX);
			}
			fontRenderer.revertFontSize();
			fontRenderer.resetFont();
		} else if (this.focussed) {
			this.renderCaret(tessellator, this.getBorderSize() * 2);
		}
	}

	protected void renderCaret(@NotNull final Tessellator tessellator, final int offsetX) {
		final int caretX = this.getX() + offsetX;
		tessellator.drawQuick(
				caretX, this.getY() + this.getBorderSize() * 2,
				this.getBorderSize(), this.getHeight() - this.getBorderSize() * 4,
				this.colorCaret
		);
	}

	@NotNull
	public GuiTextField setTextColor(@NotNull final Color textColor) {
		this.colorText = textColor;
		return this;
	}

	@NotNull
	public GuiTextField setBorderColor(@NotNull final Color borderColor) {
		this.colorBorder = borderColor;
		return this;
	}

	@NotNull
	public GuiTextField setCaretColor(@NotNull final Color caretColor) {
		this.colorCaret = caretColor;
		return this;
	}

	public void setText(@NotNull final String text) {
		if (this.builder.length() > 0) {
			this.builder.replace(0, this.builder.length() - 1, text);
		} else {
			this.builder.append(text);
		}
		this.caretPos = this.builder.length();
	}

	public void setFocussed(final boolean focussed) {
		this.focussed = focussed;
		Input.setKeyManagerBlocked(true);
	}

	@NotNull
	public String getText() {
		return this.builder.toString();
	}

	@NotNull
	protected String getTextBeforeCaret() {
		return this.builder.isEmpty() ? "" : this.builder.substring(0, this.caretPos);
	}

	protected int getBorderSize() {
		return 4;
	}

	public final boolean isFocussed() {
		return this.focussed;
	}
}