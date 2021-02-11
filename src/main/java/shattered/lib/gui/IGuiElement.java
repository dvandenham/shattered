package shattered.lib.gui;

import org.jetbrains.annotations.NotNull;
import shattered.lib.ITickable;
import shattered.lib.gfx.Display;
import shattered.lib.gfx.IRenderableLayered;
import shattered.lib.math.Dimension;
import shattered.lib.math.Point;
import shattered.lib.math.Rectangle;

public abstract class IGuiElement implements ITickable, IRenderableLayered {

	private final Rectangle bounds = Rectangle.createMutable(0, 0, -1, -1);

	protected final void setX(final int x) {
		this.bounds.setX(x);
	}

	protected final void setY(final int y) {
		this.bounds.setY(y);
	}

	protected final void setPosition(final int x, final int y) {
		this.bounds.setPosition(x, y);
	}

	protected final void setPosition(@NotNull final Point position) {
		this.bounds.setPosition(position);
	}

	protected final void setWidth(final int width) {
		this.bounds.setWidth(width);
	}

	protected final void setHeight(final int height) {
		this.bounds.setHeight(height);
	}

	protected final void setSize(final int width, final int height) {
		this.bounds.setSize(width, height);
	}

	protected final void setSize(@NotNull final Dimension size) {
		this.bounds.setSize(size);
	}

	protected final void setBounds(final int x, final int y, final int width, final int height) {
		this.setPosition(x, y);
		this.setSize(width, height);
	}

	protected final void setBounds(@NotNull final Point position, final int width, final int height) {
		this.setPosition(position);
		this.setSize(width, height);
	}

	protected final void setBounds(final int x, final int y, @NotNull final Dimension size) {
		this.setPosition(x, y);
		this.setSize(size);
	}

	protected final void setBounds(@NotNull final Point position, @NotNull final Dimension size) {
		this.setPosition(position);
		this.setSize(size);
	}

	protected final void setBounds(@NotNull final Rectangle bounds) {
		this.setPosition(bounds.getPosition());
		this.setSize(bounds.getSize());
	}

	@NotNull
	public final Rectangle getBounds() {
		return IGuiElement.getCorrectBounds(this.bounds);
	}

	public final int getX() {
		return this.getBounds().getX();
	}

	public final int getY() {
		return this.getBounds().getY();
	}

	public final int getWidth() {
		return this.getBounds().getWidth();
	}

	public final int getHeight() {
		return this.getBounds().getHeight();
	}

	private static Rectangle getCorrectBounds(final Rectangle bounds) {
		int width = bounds.getWidth();
		if (width < 0) {
			width = Display.getWidth() / -width;
		} else if (width == 0 || width > Display.getWidth()) {
			width = Display.getWidth();
		}

		int height = bounds.getHeight();
		if (height < 0) {
			height = Display.getHeight() / -height;
		} else if (height == 0 || height > Display.getHeight()) {
			height = Display.getHeight();
		}

		final int availableX = Display.getWidth() - width;
		final int availableY = Display.getHeight() - height;

		int x = bounds.getX();
		if (x < 0) {
			x = availableX / 2;
		} else if (x > availableX) {
			x = availableX;
		}

		int y = bounds.getY();
		if (y < 0) {
			y = availableY / 2;
		} else if (y > availableY) {
			y = availableY;
		}

		return Rectangle.create(x, y, width, height);
	}
}