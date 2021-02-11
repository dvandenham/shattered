package shattered.lib.gui;

import java.util.function.Consumer;
import org.jetbrains.annotations.NotNull;
import shattered.core.event.EventListener;
import shattered.lib.gfx.Display;
import shattered.lib.gfx.DisplayResizedEvent;
import shattered.lib.gfx.FontRenderer;
import shattered.lib.gfx.Tessellator;
import shattered.lib.math.Dimension;
import shattered.lib.math.Point;
import shattered.lib.math.Rectangle;

public abstract class IGuiScreen implements IComponentContainer {

	private final GuiPanel components = new GuiScreenComponentPanel();
	private final Rectangle bounds = Rectangle.createMutable(0, 0, -1, -1);
	private final Rectangle boundsCached = Rectangle.createMutable(0, 0, 0, 0);

	protected void tick() {
	}

	protected abstract void renderBackground(@NotNull Tessellator tessellator, @NotNull FontRenderer fontRenderer);

	protected abstract void renderForeground(@NotNull Tessellator tessellator, @NotNull FontRenderer fontRenderer);

	@Override
	public final void add(@NotNull final IGuiComponent component) {
		this.components.add(component);
	}

	@Override
	public final void remove(@NotNull final IGuiComponent component) {
		this.components.remove(component);
	}

	@Override
	public final boolean hasComponent(@NotNull final IGuiComponent component) {
		return this.components.hasComponent(component);
	}

	@Override
	public final boolean deepHasComponent(@NotNull final IGuiComponent component) {
		return this.components.deepHasComponent(component);
	}

	@Override
	public void doForAll(final Consumer<IGuiComponent> action) {
		this.components.doForAll(action);
	}

	protected final void setX(final int x) {
		if (x != this.bounds.getX()) {
			this.bounds.setX(x);
			this.cacheBounds();
		}
	}

	protected final void setY(final int y) {
		if (y != this.bounds.getY()) {
			this.bounds.setY(y);
			this.cacheBounds();
		}
	}

	protected final void setPosition(final int x, final int y) {
		if (x != this.bounds.getX() || y != this.bounds.getY()) {
			this.bounds.setPosition(x, y);
			this.cacheBounds();
		}
	}

	protected final void setPosition(@NotNull final Point position) {
		if (!position.equals(this.bounds.getPosition())) {
			this.bounds.setPosition(position);
			this.cacheBounds();
		}
	}

	protected final void setWidth(final int width) {
		if (width != this.bounds.getWidth()) {
			this.bounds.setWidth(width);
			this.cacheBounds();
		}
	}

	protected final void setHeight(final int height) {
		if (height != this.bounds.getHeight()) {
			this.bounds.setHeight(height);
			this.cacheBounds();
		}
	}

	protected final void setSize(final int width, final int height) {
		if (width != this.bounds.getWidth() || height != this.bounds.getHeight()) {
			this.bounds.setSize(width, height);
			this.cacheBounds();
		}
	}

	protected final void setSize(@NotNull final Dimension size) {
		if (!size.equals(this.bounds.getSize())) {
			this.bounds.setSize(size);
			this.cacheBounds();
		}
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
		return this.boundsCached.toImmutable();
	}

	public final int getX() {
		return this.boundsCached.getX();
	}

	public final int getY() {
		return this.boundsCached.getY();
	}

	public final int getWidth() {
		return this.boundsCached.getWidth();
	}

	public final int getHeight() {
		return this.boundsCached.getHeight();
	}

	public final boolean isFullscreen() {
		return this.boundsCached.equals(Display.getBounds());
	}

	void cacheBounds() {
		final Rectangle newBounds = IGuiScreen.getCorrectBounds(this.bounds);
		this.boundsCached.setPosition(newBounds.getPosition()).setSize(newBounds.getSize());
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

	@EventListener
	private void onDisplayResized(final DisplayResizedEvent ignored) {
		this.cacheBounds();
	}

	private static class GuiScreenComponentPanel extends GuiPanel {

		@Override
		public void tick() {
		}

		@Override
		public void setupComponents(@NotNull final Layout layout) {
		}
	}
}