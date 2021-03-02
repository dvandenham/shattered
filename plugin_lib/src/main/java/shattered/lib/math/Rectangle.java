package shattered.lib.math;

import java.util.Objects;
import org.jetbrains.annotations.NotNull;

public final class Rectangle {

	public static final Rectangle EMPTY = Rectangle.create(Point.EMPTY, Dimension.EMPTY);
	private final boolean mutable;
	private final Point position;
	private final Dimension size;

	private Rectangle(final int x, final int y, final int width, final int height, final boolean mutable) {
		this.mutable = mutable;
		this.position = Point.createMutable(x, y);
		this.size = Dimension.createMutable(width, height);
	}

	@NotNull
	public Rectangle setX(final int x) {
		if (!this.mutable) {
			return new Rectangle(x, this.getY(), this.getWidth(), this.getHeight(), false);
		}
		this.position.setX(x);
		return this;
	}

	@NotNull
	public Rectangle setY(final int y) {
		if (!this.mutable) {
			return new Rectangle(this.getX(), y, this.getWidth(), this.getHeight(), false);
		}
		this.position.setY(y);
		return this;
	}

	@NotNull
	public Rectangle setPosition(final int x, final int y) {
		if (!this.mutable) {
			return new Rectangle(x, y, this.getWidth(), this.getHeight(), false);
		}
		this.position.setX(x);
		this.position.setY(y);
		return this;
	}

	@NotNull
	public Rectangle setPosition(@NotNull final Point position) {
		return this.setPosition(position.getX(), position.getY());
	}

	@NotNull
	public Rectangle setWidth(final int width) {
		if (!this.mutable) {
			return new Rectangle(this.getX(), this.getY(), width, this.getHeight(), false);
		}
		this.size.setWidth(width);
		return this;
	}

	@NotNull
	public Rectangle setHeight(final int height) {
		if (!this.mutable) {
			return new Rectangle(this.getX(), this.getY(), this.getWidth(), height, false);
		}
		this.size.setHeight(height);
		return this;
	}

	@NotNull
	public Rectangle setSize(final int width, final int height) {
		if (!this.mutable) {
			return new Rectangle(this.getX(), this.getY(), width, height, false);
		}
		this.size.setWidth(width);
		this.size.setHeight(height);
		return this;
	}

	@NotNull
	public Rectangle setSize(@NotNull final Dimension size) {
		return this.setSize(size.getWidth(), size.getHeight());
	}

	@NotNull
	public Rectangle moveX(final int amount) {
		return this.setX(this.getX() + amount);
	}

	@NotNull
	public Rectangle moveY(final int amount) {
		return this.setY(this.getY() + amount);
	}

	@NotNull
	public Rectangle move(final int amountX, final int amountY) {
		return this.setPosition(this.getX() + amountX, this.getY() + amountY);
	}

	@NotNull
	public Rectangle move(@NotNull final Dimension amount) {
		return this.move(amount.getWidth(), amount.getHeight());
	}

	@NotNull
	public Rectangle growX(final int amount) {
		return this.setWidth(this.getWidth() + amount);
	}

	@NotNull
	public Rectangle growY(final int amount) {
		return this.setHeight(this.getHeight() + amount);
	}

	@NotNull
	public Rectangle grow(final int amountX, final int amountY) {
		return this.setSize(this.getWidth() + amountX, this.getHeight() + amountY);
	}

	@NotNull
	public Rectangle shrinkX(final int amount) {
		return this.setWidth(this.getWidth() - amount);
	}

	@NotNull
	public Rectangle shrinkY(final int amount) {
		return this.setHeight(this.getHeight() - amount);
	}

	@NotNull
	public Rectangle shrink(final int amountX, final int amountY) {
		return this.setSize(this.getWidth() - amountX, this.getHeight() - amountY);
	}

	@NotNull
	public Rectangle grow(@NotNull final Dimension size) {
		return this.grow(size.getWidth(), size.getHeight());
	}

	public boolean contains(final int x, final int y) {
		return x >= this.getX() && x <= this.getMaxX() && y >= this.getY() && y <= this.getMaxY();
	}

	public boolean contains(@NotNull final Dimension position) {
		return this.contains(position.getWidth(), position.getHeight());
	}

	public boolean contains(final int x, final int y, final int width, final int height) {
		return x >= this.getX() && x + width <= this.getMaxX() && y >= this.getY() && y + height <= this.getMaxY();
	}

	public boolean contains(final int x, final int y, @NotNull final Dimension size) {
		return this.contains(x, y, size.getWidth(), size.getHeight());
	}

	public boolean contains(@NotNull final Dimension position, @NotNull final Dimension size) {
		return this.contains(position.getWidth(), position.getHeight(), size.getWidth(), size.getHeight());
	}

	public boolean contains(@NotNull final Dimension position, final int width, final int height) {
		return this.contains(position.getWidth(), position.getHeight(), width, height);
	}

	public boolean contains(@NotNull final Rectangle rectangle) {
		return this.contains(rectangle.getX(), rectangle.getY(), rectangle.getWidth(), rectangle.getHeight());
	}

	@NotNull
	public Point getPosition() {
		return this.position.toImmutable();
	}

	@NotNull
	public Dimension getSize() {
		return this.size.toImmutable();
	}

	public int getX() {
		return this.position.getX();
	}

	public int getY() {
		return this.position.getY();
	}

	public int getWidth() {
		return this.size.getWidth();
	}

	public int getHeight() {
		return this.size.getHeight();
	}

	public final int getCenterX() {
		return this.getX() + this.getWidth() / 2;
	}

	public final int getCenterY() {
		return this.getY() + this.getHeight() / 2;
	}

	@NotNull
	public final Point getCenter() {
		return Point.create(this.getCenterX(), this.getCenterY());
	}

	public int getMaxX() {
		return this.position.getX() + this.size.getWidth();
	}

	public int getMaxY() {
		return this.position.getY() + this.size.getHeight();
	}

	@NotNull
	public Point getMaxPosition() {
		return Point.create(this.getMaxX(), this.getMaxY());
	}

	@NotNull
	public Rectangle toMutable() {
		return this.mutable ? this : new Rectangle(this.getX(), this.getY(), this.getWidth(), this.getHeight(), true);
	}

	@NotNull
	public Rectangle toImmutable() {
		return this.mutable ? new Rectangle(this.getX(), this.getY(), this.getWidth(), this.getHeight(), false) : this;
	}

	public boolean isMutable() {
		return this.mutable;
	}

	@NotNull
	public Rectangle copy() {
		return new Rectangle(this.getX(), this.getY(), this.getWidth(), this.getHeight(), this.mutable);
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.getX(), this.getY(), this.getWidth(), this.getHeight());
	}

	@Override
	public boolean equals(final Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof Rectangle)) {
			return false;
		}
		final Rectangle other = (Rectangle) o;
		return this.getX() == other.getX() && this.getY() == other.getY() && this.getWidth() == other.getWidth() && this.getHeight() == other.getHeight();
	}

	@Override
	public String toString() {
		return "Rectangle[X=" + this.getX() + ",Y=" + this.getY() + ",Width=" + this.getWidth() + ",Height=" + this.getHeight() + ']';
	}

	@NotNull
	public static Rectangle create(final int x, final int y, final int width, final int height) {
		return new Rectangle(x, y, width, height, false);
	}

	@NotNull
	public static Rectangle create(@NotNull final Point position, @NotNull final Dimension size) {
		return new Rectangle(position.getX(), position.getY(), size.getWidth(), size.getHeight(), false);
	}

	@NotNull
	public static Rectangle create(final int x, final int Y, @NotNull final Dimension size) {
		return new Rectangle(x, Y, size.getWidth(), size.getHeight(), false);
	}

	@NotNull
	public static Rectangle create(@NotNull final Point position, final int width, final int height) {
		return new Rectangle(position.getX(), position.getY(), width, height, false);
	}

	@NotNull
	public static Rectangle createMutable(final int x, final int y, final int width, final int height) {
		return new Rectangle(x, y, width, height, true);
	}

	@NotNull
	public static Rectangle createMutable(@NotNull final Point position, @NotNull final Dimension size) {
		return new Rectangle(position.getX(), position.getY(), size.getWidth(), size.getHeight(), true);
	}

	@NotNull
	public static Rectangle createMutable(final int x, final int y, @NotNull final Dimension size) {
		return new Rectangle(x, y, size.getWidth(), size.getHeight(), true);
	}

	@NotNull
	public static Rectangle createMutable(@NotNull final Point position, final int width, final int height) {
		return new Rectangle(position.getX(), position.getY(), width, height, true);
	}
}