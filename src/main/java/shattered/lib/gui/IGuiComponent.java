package shattered.lib.gui;

import shattered.core.ICacheable;
import shattered.lib.Input;
import shattered.lib.gfx.FontRenderer;
import shattered.lib.gfx.Tessellator;
import shattered.lib.math.Dimension;
import shattered.lib.math.Point;
import shattered.lib.math.Rectangle;
import org.jetbrains.annotations.NotNull;

public abstract class IGuiComponent {

	private final Rectangle bounds = Rectangle.createMutable(0, 0, -1, -1);
	private boolean enabled = true;
	private boolean visible = true;
	private int maximumWidth = -1;

	public abstract void render(@NotNull Tessellator tessellator, @NotNull FontRenderer fontRenderer);

	@NotNull
	public IGuiComponent setEnabled(final boolean enabled) {
		this.enabled = enabled;
		return this;
	}

	@NotNull
	public final IGuiComponent setVisible(final boolean visible) {
		this.visible = visible;
		return this;
	}

	@NotNull
	public final IGuiComponent setMaximumWidth(final int maximumWidth) {
		if (this.maximumWidth != maximumWidth) {
			this.maximumWidth = maximumWidth;
			this.tryCache();
		}
		return this;
	}

	public final void setX(final int x) {
		if (this.bounds.getX() != x) {
			this.bounds.setX(x);
			this.tryCache();
		}
	}

	public final void setY(final int y) {
		if (this.bounds.getY() != y) {
			this.bounds.setY(y);
			this.tryCache();
		}
	}

	public final void setPosition(final int x, final int y) {
		if (x != this.bounds.getX() || y != this.bounds.getY()) {
			this.bounds.setPosition(x, y);
			this.tryCache();
		}
	}

	public final void setPosition(@NotNull final Point position) {
		if (this.bounds.getDoubleX() != position.getDoubleX() || this.bounds.getDoubleY() != position.getDoubleY()) {
			this.bounds.setPosition(position);
			this.tryCache();
		}
	}

	public final void setWidth(final int width) {
		if (this.bounds.getWidth() != width) {
			this.bounds.setWidth(width);
			this.tryCache();
		}
	}

	public final void setHeight(final int height) {
		if (this.bounds.getHeight() != height) {
			this.bounds.setHeight(height);
			this.tryCache();
		}
	}

	public final void setSize(final int width, final int height) {
		if (this.bounds.getWidth() != width || this.bounds.getHeight() != height) {
			this.bounds.setSize(width, height);
			this.tryCache();
		}
	}

	public final void setSize(@NotNull final Dimension size) {
		if (this.bounds.getDoubleWidth() != size.getDoubleWidth() || this.bounds.getDoubleHeight() != size.getDoubleHeight()) {
			this.bounds.setSize(size);
			this.tryCache();
		}
	}

	public final void setBounds(final int x, final int y, final int width, final int height) {
		this.setPosition(x, y);
		this.setSize(width, height);
	}

	public final void setBounds(@NotNull final Point position, final int width, final int height) {
		this.setPosition(position);
		this.setSize(width, height);
	}

	public final void setBounds(final int x, final int y, @NotNull final Dimension size) {
		this.setPosition(x, y);
		this.setSize(size);
	}

	public final void setBounds(@NotNull final Point position, @NotNull final Dimension size) {
		this.setPosition(position);
		this.setSize(size);
	}

	public final void setBounds(@NotNull final Rectangle bounds) {
		this.setPosition(bounds.getPosition());
		this.setSize(bounds.getSize());
	}

	public final boolean isEnabled() {
		return this.enabled;
	}

	public final boolean isVisible() {
		return this.visible;
	}

	public final int getMaximumWidth() {
		return this.maximumWidth;
	}

	protected final boolean containsMouse() {
		return Input.containsMouse(this.getBounds());
	}

	@NotNull
	public final Rectangle getBounds() {
		return this.bounds.toImmutable();
	}

	public final int getX() {
		return this.bounds.getX();
	}

	public final int getY() {
		return this.bounds.getY();
	}

	public final int getWidth() {
		return this.bounds.getWidth();
	}

	public final int getHeight() {
		return this.bounds.getHeight();
	}

	private void tryCache() {
		if (this instanceof ICacheable) {
			((ICacheable) this).cache();
		}
	}
}