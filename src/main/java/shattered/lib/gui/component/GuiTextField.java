package shattered.lib.gui.component;

import shattered.Assets;
import shattered.core.event.EventListener;
import shattered.lib.Color;
import shattered.lib.Input;
import shattered.lib.KeyEvent;
import shattered.lib.gfx.FontRenderer;
import shattered.lib.gfx.GLHelper;
import shattered.lib.gfx.MatrixUtils;
import shattered.lib.gfx.RenderHelper;
import shattered.lib.gfx.StringData;
import shattered.lib.gfx.Tessellator;
import shattered.lib.gui.IGuiComponent;
import shattered.lib.gui.IGuiTickable;
import shattered.lib.math.Rectangle;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_BACKSPACE;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_ENTER;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_RIGHT;

public class GuiTextField extends IGuiComponent implements IGuiTickable {

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
				this.setFocussed(true);
			}
			if (!this.containsMouse() && this.focussed) {
				this.setFocussed(false);
			}
		}
	}

	@Override
	public void render(@NotNull final Tessellator tessellator, @NotNull final FontRenderer fontRenderer) {
		tessellator.drawQuick(this.getBounds(), Color.BLACK.withAlpha(0.5F));
		RenderHelper.renderFrame(tessellator, this.getBounds(), this.getBorderSize(), this.focussed ? this.colorBorder : Color.DARK_GRAY);

		fontRenderer.setFont(Assets.FONT_SIMPLE);
		fontRenderer.setFontSize(24);

		if (!this.builder.isEmpty()) {
			final Rectangle internalBounds = this.getInternalBounds();
			GLHelper.scissor(internalBounds);
			if (!this.focussed) {
				fontRenderer.writeQuick(
						internalBounds.getX(), internalBounds.getY(),
						new StringData(this.getText(), this.colorText).localize(false).centerY(this.getHeight())
				);
			} else {
				final StringData[] data = this.getStringDataToRender();
				if (data[0] != null && data[1] == null) { //Caret is at the end of the string
					final int width = fontRenderer.getWidth(data[0]);
					if (width > internalBounds.getWidth() - 8) {
						tessellator.setUniformMatrix(
								MatrixUtils.identity().translate(internalBounds.getWidth() - 8 - width, 0, 0)
						);
					}
					fontRenderer.writeQuick(internalBounds.getX(), internalBounds.getY(), data[0]);
					tessellator.resetUniformMatrix();
				} else if (data[0] == null && data[1] != null) { //Caret is at the beginning of the string
					fontRenderer.writeQuick(internalBounds.getX() + 8, internalBounds.getY(), data[1]);
				} else {
					assert data[0] != null;
					final int widthLeft = fontRenderer.getWidth(data[0]);
					final int widthRight = fontRenderer.getWidth(data[1]);
					final int totalWidth = widthLeft + 12 + widthRight;
					if (totalWidth > internalBounds.getWidth()) {
						tessellator.setUniformMatrix(
								MatrixUtils.identity().translate(internalBounds.getWidth() - totalWidth, 0, 0)
						);
					}
					fontRenderer.writeQuick(internalBounds.getX(), internalBounds.getY(), data[0]);
					fontRenderer.writeQuick(internalBounds.getX() + widthLeft + 12, internalBounds.getY(), data[1]);
					tessellator.resetUniformMatrix();
				}
			}
			GLHelper.disableScissor();
		}

		if (this.focussed) {
			tessellator.drawQuick(this.getCaretBounds(fontRenderer), this.colorCaret);
		}

		fontRenderer.revertFontSize();
		fontRenderer.resetFont();
	}

	@Nullable
	protected StringData[] getStringDataToRender() {
		if (this.caretPos == this.builder.length()) {
			return new StringData[]{
					new StringData(this.getText(), this.colorText).localize(false).centerY(this.getHeight()),
					null
			};
		} else {
			final String textBefore = this.getTextBeforeCaret();
			if (textBefore == null) {
				return new StringData[]{
						null,
						new StringData(this.getText(), this.colorText).localize(false).centerY(this.getHeight())
				};
			} else {
				return new StringData[]{
						new StringData(textBefore, this.colorText).localize(false).centerY(this.getHeight()),
						new StringData(this.getText().substring(textBefore.length()), this.colorText).localize(false).centerY(this.getHeight())
				};
			}
		}
	}

	@NotNull
	public final GuiTextField setTextColor(@NotNull final Color textColor) {
		this.colorText = textColor;
		return this;
	}

	@NotNull
	public final GuiTextField setBorderColor(@NotNull final Color borderColor) {
		this.colorBorder = borderColor;
		return this;
	}

	@NotNull
	public final GuiTextField setCaretColor(@NotNull final Color caretColor) {
		this.colorCaret = caretColor;
		return this;
	}

	public final void setText(@NotNull final String text) {
		if (this.builder.length() > 0) {
			this.builder.replace(0, this.builder.length() - 1, text);
		} else {
			this.builder.append(text);
		}
		this.caretPos = this.builder.length();
	}

	public final void setFocussed(final boolean focussed) {
		if (this.focussed != focussed) {
			this.focussed = focussed;
			if (this.focussed) {
				Input.INPUT_BUS.register(this);
				Input.enableEventBusMode();
			} else {
				Input.INPUT_BUS.unregister(this);
				Input.disableEventBusMode();
			}
		}
	}

	@NotNull
	protected Rectangle getCaretBounds(@NotNull final FontRenderer fontRenderer) {
		final Rectangle internalBounds = this.getInternalBounds();
		return Rectangle.create(
				this.getCaretPosX(fontRenderer),
				internalBounds.getY() + this.getBorderSize(),
				4,
				internalBounds.getHeight() - this.getBorderSize() * 2
		);
	}

	protected int getCaretPosX(@NotNull final FontRenderer fontRenderer) {
		final Rectangle internalBounds = this.getInternalBounds();
		final String textBefore = this.getTextBeforeCaret();
		if (textBefore == null) {
			return internalBounds.getX();
		} else {
			final int textWidth = fontRenderer.getWidth(new StringData(textBefore).localize(false));
			int caretX = internalBounds.getX() + textWidth + 4;
			if (caretX >= this.getInternalBounds().getMaxX() - 4) {
				caretX = this.getInternalBounds().getMaxX() - 4;
			}
			return caretX;
		}
	}

	@NotNull
	protected Rectangle getInternalBounds() {
		return Rectangle.create(
				this.getX() + this.getBorderSize(),
				this.getY() + this.getBorderSize(),
				this.getWidth() - this.getBorderSize() * 2,
				this.getHeight() - this.getBorderSize() * 2
		);
	}

	@NotNull
	public final String getText() {
		return this.builder.toString();
	}

	@Nullable
	protected final String getTextBeforeCaret() {
		if (this.caretPos == 0) {
			return null;
		} else if (this.caretPos == this.builder.length()) {
			return this.builder.toString();
		} else {
			return this.builder.substring(0, this.caretPos);
		}
	}

	protected int getBorderSize() {
		return 4;
	}

	public final boolean isFocussed() {
		return this.focussed;
	}

	@EventListener(KeyEvent.class)
	private void onKeyEvent(final KeyEvent event) {
		if (event.isPressed() || event.isRepeat()) {
			final int keyCode = event.getKeyCode();
			final char keyChar = Input.getKeyChar(keyCode, Input.isKeyboardShiftDown());
			switch (keyCode) {
				case GLFW_KEY_BACKSPACE:
					if (this.builder.length() >= 1) {
						this.builder.deleteCharAt(this.caretPos - 1);
						--this.caretPos;
					}
					return;
				case GLFW_KEY_ENTER:
				case GLFW_KEY_ESCAPE:
					this.setFocussed(false);
					return;
				case GLFW_KEY_LEFT:
					if (this.caretPos > 0) {
						--this.caretPos;
					}
					return;
				case GLFW_KEY_RIGHT:
					if (this.caretPos < this.builder.length()) {
						++this.caretPos;
					}
					return;
			}
			if (keyChar != 0) {
				if (Character.isWhitespace(keyChar) && keyChar != ' ') {
					return;
				}
				if (keyChar < 256) {
					this.builder.insert(this.caretPos++, keyChar);
				}
			}
		}
	}
}