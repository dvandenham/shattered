package shattered.lib.gui.component;

import shattered.core.ITickable;
import shattered.lib.Color;
import shattered.lib.Input;
import shattered.lib.gfx.FontRenderer;
import shattered.lib.gfx.RenderHelper;
import shattered.lib.gfx.Tessellator;
import shattered.lib.gui.IGuiComponent;
import shattered.lib.math.Rectangle;
import org.jetbrains.annotations.NotNull;

public class GuiScrollbar extends IGuiComponent implements ITickable {

	private final boolean vertical;
	private int steps;
	private int value;

	public GuiScrollbar(final boolean vertical, final int steps, final int defaultValue) {
		this.vertical = vertical;
		this.steps = steps;
		this.value = defaultValue;
	}

	public GuiScrollbar(final boolean vertical, final int steps) {
		this(vertical, steps, 0);
	}

	public GuiScrollbar(final int steps) {
		this(true, steps);
	}

	@Override
	public void tick() {
		if (Input.isMouseLeftClicked()) {
			if (Input.containsMouse(this.getButtonPrevBounds())) {
				--this.value;
			} else if (Input.containsMouse(this.getButtonNextBounds())) {
				++this.value;
			}
		} else if (Input.isMouseLeftPressed() && Input.containsMouse(this.getInternalBounds())) {
			final Rectangle caret = this.getCaretBounds();
			int change = 0;
			if (this.vertical) {
				if (Input.getMouseY() < caret.getY()) {
					change = Input.getMouseY() - caret.getY();
				} else if (Input.getMouseY() > caret.getMaxY()) {
					change = Input.getMouseY() - caret.getMaxY();
				}
			} else {
				if (Input.getMouseX() < caret.getX()) {
					change = Input.getMouseX() - caret.getX();
				} else if (Input.getMouseX() > caret.getMaxX()) {
					change = Input.getMouseX() - caret.getMaxX();
				}
			}
			if (change != 0) {
				final double rawPlaces = (double) change / (this.vertical ? caret.getDoubleHeight() : caret.getDoubleWidth());
				final int moveTicks = (int) (rawPlaces < 0 ? Math.floor(rawPlaces) : Math.ceil(rawPlaces));
				if (moveTicks < 0) {
					--this.value;
				} else if (moveTicks > 0) {
					++this.value;
				}
			}
		}
	}

	@Override
	public void render(@NotNull final Tessellator tessellator, @NotNull final FontRenderer fontRenderer) {
		tessellator.drawQuick(this.getBounds(), Color.WHITE.withAlpha(0.5F));

		final Rectangle internal = this.getInternalBounds();
		final Rectangle caret = this.getCaretBounds();
		final Rectangle buttonPrev = this.getButtonPrevBounds();
		final Rectangle buttonNext = this.getButtonNextBounds();

		Color caretColor = Color.XEROS;
		if (this.vertical) {
			if (caret.getHeight() == internal.getHeight()) {
				caretColor = Color.WHITE;
			}
		} else if (caret.getWidth() == internal.getWidth()) {
			caretColor = Color.WHITE;
		}
		tessellator.drawQuick(caret, caretColor);

		if (this.vertical) {
			RenderHelper.drawTriangle(tessellator,
					buttonPrev.getCenterX(), buttonPrev.getY() + 2,
					buttonPrev.getX() + 2, buttonPrev.getMaxY() - 2,
					buttonPrev.getMaxX() - 2, buttonPrev.getMaxY() - 2,
					Color.BLACK
			);
			RenderHelper.drawTriangle(tessellator,
					buttonNext.getX() + 2, buttonNext.getY() + 2,
					buttonNext.getMaxX() - 2, buttonNext.getY() + 2,
					buttonNext.getCenterX(), buttonNext.getMaxY() - 2,
					Color.BLACK
			);
		} else {
			RenderHelper.drawTriangle(tessellator,
					buttonPrev.getX() + 2, buttonPrev.getCenterY(),
					buttonPrev.getMaxX() - 2, buttonPrev.getY() + 2,
					buttonPrev.getMaxX() - 2, buttonPrev.getMaxY() - 2,
					Color.BLACK
			);
			RenderHelper.drawTriangle(tessellator,
					buttonNext.getX() + 2, buttonNext.getY() + 2,
					buttonNext.getX() + 2, buttonNext.getMaxY() - 2,
					buttonNext.getMaxX() - 2, buttonNext.getCenterY(),
					Color.BLACK
			);
		}
	}

	public final void setSteps(final int steps) {
		this.steps = steps;
	}

	public final void setValue(final int value) {
		this.value = value;
	}

	public final int getSteps() {
		return this.steps;
	}

	public final int getValue() {
		return this.value;
	}

	public final boolean isVertical() {
		return this.vertical;
	}

	protected Rectangle getCaretBounds() {
		if (this.steps <= 0) {
			this.steps = 1;
		}
		final int caretSize = (this.vertical ? this.getInternalBounds().getHeight() : this.getInternalBounds().getWidth()) / this.steps;
		final int caretSteps = (this.vertical ? this.getInternalBounds().getHeight() : this.getInternalBounds().getWidth()) - caretSize / this.steps;
		final int caretWidth = this.vertical ? this.getInternalBounds().getWidth() : Math.max(caretSize, 12);
		final int caretHeight = this.vertical ? Math.max(caretSize, 12) : this.getInternalBounds().getHeight();
		final int caretX = this.getInternalBounds().getX() + (this.vertical ? 0 : this.value * caretSteps);
		final int caretY = this.getInternalBounds().getY() + (this.vertical ? this.value * caretSize : 0);
		return Rectangle.create(caretX, caretY, caretWidth, caretHeight);
	}

	protected Rectangle getInternalBounds() {
		return Rectangle.create(
				this.vertical ? this.getX() : this.getX() + this.getButtonPrevBounds().getWidth(),
				this.vertical ? this.getY() + this.getButtonPrevBounds().getHeight() : this.getY(),
				this.vertical ? this.getWidth() : this.getWidth() - this.getButtonPrevBounds().getWidth() - this.getButtonNextBounds().getWidth(),
				this.vertical ? this.getHeight() - this.getButtonPrevBounds().getHeight() - this.getButtonNextBounds().getHeight() : this.getHeight()
		);
	}

	protected Rectangle getButtonPrevBounds() {
		return Rectangle.create(
				this.getX(),
				this.getY(),
				this.vertical ? this.getWidth() : this.getHeight(),
				this.vertical ? this.getWidth() : this.getHeight()
		);
	}

	protected Rectangle getButtonNextBounds() {
		return Rectangle.create(
				this.vertical ? this.getX() : this.getX() + this.getWidth() - this.getHeight(),
				this.vertical ? this.getY() + this.getHeight() - this.getWidth() : this.getY(),
				this.vertical ? this.getWidth() : this.getHeight(),
				this.vertical ? this.getWidth() : this.getHeight()
		);
	}
}