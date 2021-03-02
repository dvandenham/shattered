package shattered.lib.gui;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.jetbrains.annotations.NotNull;
import shattered.lib.math.Rectangle;

public final class DefaultLayout extends Layout {

	private final ObjectArrayList<IGuiComponent[]> pairs = new ObjectArrayList<>();
	private final int componentHeight, componentSpacing;
	private final int originalX, originalY, originalWidth, originalHeight;
	private final int x;
	private int y;
	private final int width;
	private final int height;
	private boolean isInverted = false;
	private boolean rowMode = false;
	private int currentRow = 0;
	private int rowMaxWidth = -1;

	public DefaultLayout(final int componentHeight, final int componentSpacing, final int x, final int y, final int width, final int height) {
		this.componentHeight = componentHeight;
		this.componentSpacing = componentSpacing;
		this.originalX = this.x = x;
		this.originalY = this.y = y;
		this.originalWidth = this.width = width;
		this.originalHeight = this.height = height;
	}

	public DefaultLayout(final int componentHeight, final int componentSpacing, @NotNull final Rectangle bounds) {
		this(componentHeight, componentSpacing, bounds.getX(), bounds.getY(), bounds.getWidth(), bounds.getHeight());
	}

	@Override
	public void setInverted() {
		this.isInverted = true;
		this.y = this.y + this.height;
	}

	@Override
	public void startRow() {
		this.startRow(-1);
	}

	@Override
	public void startRow(final int maxSize) {
		this.rowMode = true;
		this.rowMaxWidth = maxSize;
	}

	@Override
	public void stopRow() {
		this.rowMode = false;
		int x = this.x - this.componentSpacing / 2;
		int width = this.width + this.componentSpacing;
		if (this.rowMaxWidth > 0) {
			x += (width - this.rowMaxWidth) / 2 - this.componentSpacing / 2;
			width = this.rowMaxWidth + this.componentSpacing;
		}
		final int pairs = this.pairs.size();
		final int pairWidthTotal = width / pairs;
		int visibleComponents = 0;
		for (final IGuiComponent[] components : this.pairs) {
			int totalFreeWidth = pairWidthTotal - (components.length * this.componentSpacing);
			int dynamicComponents = 0;
			final int[] widthArray = new int[components.length];
			for (int i = 0; i < components.length; ++i) {
				if (!components[i].isVisible()) {
					continue;
				}
				widthArray[i] = components[i].getMaximumWidth();
				if (components[i].getMaximumWidth() > 0) {
					totalFreeWidth -= (widthArray[i] + this.componentSpacing);
				} else {
					++dynamicComponents;
				}
				++visibleComponents;
			}
			for (int i = 0; i < widthArray.length; ++i) {
				if (widthArray[i] <= 0) {
					widthArray[i] = totalFreeWidth / dynamicComponents;
				}
			}
			x += this.componentSpacing / 2;
			for (int i = 0; i < widthArray.length; ++i) {
				components[i].setPosition(x, this.GetCorrectY());
				components[i].setSize(widthArray[i], this.componentHeight);
				x += widthArray[i];
			}
			x += this.componentSpacing / 2;
		}
		final IGuiComponent lastComponent = this.pairs.get(this.pairs.size() - 1)[this.pairs.get(this.pairs.size() - 1).length - 1];
		final int DeadPixels = (this.rowMaxWidth > 0 ? this.rowMaxWidth : this.width) - (lastComponent.getX() + lastComponent.getWidth());
		if (DeadPixels > 0) {
			lastComponent.setSize(lastComponent.getWidth() + DeadPixels, lastComponent.getHeight());
		}
		this.pairs.clear();
		if (visibleComponents > 0) {
			++this.currentRow;
		}
	}

	@Override
	public void add(@NotNull final IGuiComponent... components) {
		if (this.rowMode) {
			this.pairs.add(components);
		} else {
			int totalFreeWidth = this.width;
			int dynamicComponents = 0;
			final int[] widthArray = new int[components.length];
			int visibleComponents = 0;
			for (int i = 0; i < components.length; ++i) {
				if (!components[i].isVisible()) {
					continue;
				}
				widthArray[i] = components[i].getMaximumWidth();
				if (components[i].getMaximumWidth() > 0) {
					totalFreeWidth -= widthArray[i];
				} else {
					++dynamicComponents;
				}
				++visibleComponents;
			}
			for (int i = 0; i < widthArray.length; ++i) {
				if (widthArray[i] <= 0) {
					widthArray[i] = totalFreeWidth / (dynamicComponents == 0 ? 1 : dynamicComponents);
				}
			}
			int newX = this.x + totalFreeWidth / 2;
			for (int i = 0; i < widthArray.length; ++i) {
				if (components[i].getMaximumWidth() > 0) {
					components[i].setPosition(newX, this.GetCorrectY());
				} else {
					components[i].setPosition(newX - totalFreeWidth / 2, this.GetCorrectY());
				}
				components[i].setSize(widthArray[i], this.componentHeight);
				newX += widthArray[i];
			}
			final int deadPixels = this.width - (components[components.length - 1].getX() + components[components.length - 1].getWidth());
			if (deadPixels > 0) {
				if (components[components.length - 1].getMaximumWidth() == 0) {
					components[components.length - 1].setSize(
							components[components.length - 1].getWidth() + deadPixels,
							components[components.length - 1].getHeight()
					);
				}
			}
			if (visibleComponents > 0) {
				++this.currentRow;
			}
		}
	}

	@Override
	public void addEmptyRow() {
		++this.currentRow;
	}

	private int GetCorrectY() {
		if (this.isInverted) {
			return this.y - (this.currentRow * (this.componentHeight + this.componentSpacing)) - this.componentHeight;
		} else {
			return this.y + (this.currentRow * (this.componentHeight + this.componentSpacing));
		}
	}

	@Override
	public int getComponentSpacing() {
		return this.componentSpacing;
	}

	@Override
	public int getComponentHeight() {
		return this.componentHeight;
	}

	@Override
	@NotNull
	public Layout recreate(final int componentHeight, final int componentSpacing, final int x, final int y, final int width, final int height) {
		return new DefaultLayout(componentHeight, componentSpacing, x, y, width, height);
	}

	@Override
	@NotNull
	public Layout recreate(final int componentHeight, final int componentSpacing) {
		return this.recreate(componentHeight, componentSpacing, this.originalX, this.originalY, this.originalWidth, this.originalHeight);
	}

	@Override
	@NotNull
	public Layout recreate() {
		return this.recreate(this.componentHeight, this.componentSpacing);
	}

	static DefaultLayout create(@NotNull final IGuiScreen screen) {
		return new DefaultLayout(48, 4, screen.getX(), screen.getY(), screen.getWidth(), screen.getHeight());
	}
}