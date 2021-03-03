package shattered.lib.gfx;

import org.jetbrains.annotations.NotNull;
import shattered.lib.Color;
import shattered.lib.math.Dimension;

public final class StringData {

	private final String text;
	private final Color color;
	private boolean doCenterX = false;
	private boolean doCenterY = false;
	private int centerX = 0;
	private int centerY = 0;
	private boolean doWrapText = false;
	private boolean doWrapTextStop = false;
	private int wrapText = 0;
	private boolean doLocalize = true;

	public StringData(@NotNull final String text, @NotNull final Color color) {
		this.text = text;
		this.color = color;
	}

	public StringData(@NotNull final String text) {
		this(text, Color.WHITE);
	}

	public StringData(@NotNull final String text, @NotNull final Color color, final int centerWidth) {
		this(text, color);
		this.centerX(centerWidth);
	}

	public StringData(@NotNull final String text, @NotNull final Color color, @NotNull final Dimension centerSize) {
		this(text, color, centerSize.getWidth(), centerSize.getHeight());
	}

	public StringData(@NotNull final String text, @NotNull final Color color, final int centerWidth, final int centerHeight) {
		this(text, color);
		this.center(centerWidth, centerHeight);
	}

	@NotNull
	public StringData withColor(@NotNull final Color color) {
		final StringData result = new StringData(this.text, color);
		result.doCenterX = this.doCenterX;
		result.doCenterY = this.doCenterY;
		result.centerX = this.centerX;
		result.centerY = this.centerY;
		result.doWrapText = this.doWrapText;
		result.doWrapTextStop = this.doWrapTextStop;
		result.wrapText = this.wrapText;
		result.doLocalize = this.doLocalize;
		return result;
	}

	@NotNull
	public StringData centerX(final int centerWidth) {
		if (centerWidth > 0) {
			this.doCenterX = true;
			this.centerX = centerWidth;
		}
		return this;
	}

	@NotNull
	public StringData centerY(final int centerHeight) {
		if (centerHeight > 0) {
			this.doCenterY = true;
			this.centerY = centerHeight;
		}
		return this;
	}

	@NotNull
	public StringData center(@NotNull final Dimension centerSize) {
		return this.centerX(centerSize.getWidth()).centerY(centerSize.getHeight());
	}

	@NotNull
	public StringData center(final int centerWidth, final int centerHeight) {
		return this.centerX(centerWidth).centerY(centerHeight);
	}

	@NotNull
	public StringData wrap(final int maximumWidth, final boolean stopAtRow) {
		if (maximumWidth > 0) {
			this.doWrapText = true;
			this.wrapText = maximumWidth;
			this.doWrapTextStop = stopAtRow;
		}
		return this;
	}

	@NotNull
	public StringData wrap(final int maximumWidth) {
		return this.wrap(maximumWidth, false);
	}

	@NotNull
	public StringData localize(final boolean localize) {
		this.doLocalize = localize;
		return this;
	}

	@NotNull
	public String getText() {
		return this.text;
	}

	@NotNull
	public Color getColor() {
		return this.color;
	}

	public boolean doCenterX() {
		return this.doCenterX;
	}

	public boolean doCenterY() {
		return this.doCenterY;
	}

	public int getCenterX() {
		return this.centerX;
	}

	public int getCenterY() {
		return this.centerY;
	}

	public boolean doWrapText() {
		return this.doWrapText;
	}

	public boolean doWrapTextStop() {
		return this.doWrapTextStop;
	}

	public int getWrapText() {
		return this.wrapText;
	}

	public boolean doLocalize() {
		return this.doLocalize;
	}
}