package shattered.lib.math;

import java.util.Objects;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class Dimension {

	public static final Dimension EMPTY = Dimension.create(0, 0);
	private final boolean mutable;
	private int width, height;

	private Dimension(final int width, final int height, final boolean mutable) {
		this.mutable = mutable;
		this.width = width;
		this.height = height;
	}

	@NotNull
	public Dimension setWidth(final int width) {
		if (!this.mutable) {
			return new Dimension(width, this.height, false);
		}
		this.width = width;
		return this;
	}

	@NotNull
	public Dimension setHeight(final int height) {
		if (!this.mutable) {
			return new Dimension(this.width, height, false);
		}
		this.height = height;
		return this;
	}

	@NotNull
	public Dimension addWidth(final int width) {
		return this.setWidth(this.width + width);
	}

	@NotNull
	public Dimension addHeight(final int height) {
		return this.setHeight(this.height + height);
	}

	@NotNull
	public Dimension grow(final int width, final int height) {
		if (!this.mutable) {
			return new Dimension(this.width + width, this.height + height, false);
		}
		this.width += width;
		this.height += height;
		return this;
	}

	@NotNull
	public Dimension grow(@NotNull final Dimension dimension) {
		return this.grow(dimension.getWidth(), dimension.getHeight());
	}

	@NotNull
	public Dimension shrink(@NotNull final Dimension amount) {
		return this.grow(-amount.getWidth(), -amount.getHeight());
	}

	public int getWidth() {
		return this.width;
	}

	public int getHeight() {
		return this.height;
	}

	public final int getCenterX() {
		return this.getWidth() / 2;
	}

	public final int getCenterY() {
		return this.getHeight() / 2;
	}

	@NotNull
	public final Point getCenter() {
		return Point.create(this.getCenterX(), this.getCenterY());
	}

	public boolean isMutable() {
		return this.mutable;
	}

	@NotNull
	public Dimension toMutable() {
		return this.mutable ? this : new Dimension(this.width, this.height, true);
	}

	@NotNull
	public Dimension toImmutable() {
		return this.mutable ? new Dimension(this.width, this.height, false) : this;
	}

	@NotNull
	public Dimension copy() {
		return new Dimension(this.width, this.height, this.mutable);
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.width, this.height);
	}

	@Override
	public boolean equals(@Nullable final Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof Dimension)) {
			return false;
		}
		final Dimension other = (Dimension) o;
		return this.width == other.width && this.height == other.height;
	}

	@Override
	public String toString() {
		return "Dimension[Width=" + this.width + ",Height=" + this.height + ']';
	}

	@NotNull
	public static Dimension create(final int width, final int height) {
		return new Dimension(width, height, false);
	}

	@NotNull
	public static Dimension createMutable(final int width, final int height) {
		return new Dimension(width, height, true);
	}
}