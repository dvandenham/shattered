package shattered.lib.gui.component;

import java.util.Arrays;
import java.util.function.Consumer;
import java.util.function.Predicate;
import shattered.Assets;
import shattered.core.ITickable;
import shattered.lib.Color;
import shattered.lib.gfx.FontRenderer;
import shattered.lib.gfx.StringData;
import shattered.lib.gfx.Tessellator;
import shattered.lib.gui.IComponentContainer;
import shattered.lib.gui.IGuiComponent;
import shattered.lib.gui.Layout;
import shattered.lib.math.Rectangle;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.jetbrains.annotations.NotNull;

public class GuiList extends IGuiComponent implements ITickable, IComponentContainer {

	private final ObjectArrayList<String> data = new ObjectArrayList<>();
	private final ObjectArrayList<IGuiComponent[]> components = new ObjectArrayList<>();
	private final GuiScrollbar scrollbar = new GuiScrollbar(0);
	private boolean localize = true;

	public void add(@NotNull final String text, @NotNull final IGuiComponent... components) {
		this.data.add(text);
		this.components.add(components);
	}

	public void reset() {
		this.data.clear();
		this.components.clear();
	}

	@Override
	public void setupComponents(@NotNull final Layout layout) {
		this.scrollbar.setSteps(this.getPageCount());
		this.scrollbar.setSize(this.getScrollbarSize(), this.getInternalBounds().getHeight());
		this.scrollbar.setPosition(this.getInternalBounds().getMaxX() - this.scrollbar.getWidth(), this.getInternalBounds().getY());

		final IGuiComponent[][] visibleComponents = this.getVisibleComponents();
		for (int i = 0; i < visibleComponents.length; ++i) {
			final int y = this.getInternalBounds().getY() + this.getRowHeight() * i;
			int totalWidth = 0;
			for (int j = 0; j < visibleComponents[i].length; ++j) {
				final IGuiComponent component = visibleComponents[i][j];
				totalWidth += component.getMaximumWidth();
				component.setPosition(this.scrollbar.getX() - totalWidth, y);
				component.setSize(component.getMaximumWidth(), this.getRowHeight());
			}
		}
		Arrays.stream(visibleComponents).flatMap(Arrays::stream)
				.filter(component -> component instanceof IComponentContainer)
				.forEach(component -> ((IComponentContainer) component).setupComponents(layout));
	}

	@Override
	public void doForAll(final Consumer<IGuiComponent> action, final Predicate<IGuiComponent> predicate) {
		this.components.stream().flatMap(Arrays::stream).filter(predicate).forEach(action);
	}

	@Override
	public void add(@NotNull final IGuiComponent component) {
	}

	@Override
	public void remove(@NotNull final IGuiComponent component) {
	}

	@Override
	public boolean hasComponent(@NotNull final IGuiComponent component) {
		return this.components.stream().flatMap(Arrays::stream).anyMatch(c -> c == component);
	}

	@Override
	public boolean deepHasComponent(@NotNull final IGuiComponent component) {
		return this.hasComponent(component);
	}

	@Override
	public void tick() {
		this.scrollbar.tick();
		final IGuiComponent[][] visibleComponents = this.getVisibleComponents();
		for (int i = 0; i < this.getVisibleLines().length; ++i) {
			if (visibleComponents.length - 1 >= i) {
				for (int j = 0; j < visibleComponents[i].length; ++j) {
					final IGuiComponent component = visibleComponents[i][j];
					if (component instanceof ITickable) {
						((ITickable) component).tick();
					}
				}
			}
		}
	}

	@Override
	public void render(@NotNull final Tessellator tessellator, @NotNull final FontRenderer fontRenderer) {
		final String[] visibleLines = this.getVisibleLines();
		final int rowHeight = this.getRowHeight();
		final Rectangle internalBounds = this.getInternalBounds();

		fontRenderer.setFont(Assets.FONT_SIMPLE);
		fontRenderer.setFontSize(rowHeight / 4 * 3);
		for (int i = 0; i < visibleLines.length; ++i) {
			final int y = internalBounds.getY() + rowHeight * i;
			tessellator.drawQuick(internalBounds.getX(), y, internalBounds.getWidth(), rowHeight, (i % 2 == 0 ? Color.DARK_GRAY : Color.WHITE).withAlpha(0.5F));
			fontRenderer.writeQuick(internalBounds.getX() + 4, y, new StringData(visibleLines[i], i % 2 == 0 ? Color.WHITE : Color.BLACK).centerY(rowHeight).localize(this.localize));
		}
		fontRenderer.revertFontSize();
		fontRenderer.resetFont();

		final IGuiComponent[][] visibleComponents = this.getVisibleComponents();
		for (int i = 0; i < visibleLines.length; ++i) {
			if (visibleComponents.length - 1 >= i) {
				for (int j = 0; j < visibleComponents[i].length; ++j) {
					visibleComponents[i][j].render(tessellator, fontRenderer);
				}
			}
		}

		this.scrollbar.render(tessellator, fontRenderer);
	}

	protected int getScrollbarSize() {
		return 32;
	}

	protected int getRowHeight() {
		return 48;
	}

	@NotNull
	protected Rectangle getInternalBounds() {
		return Rectangle.create(
				this.getX(), this.getY(),
				this.getWidth() - this.scrollbar.getWidth(), this.getHeight()
		);
	}

	protected final int getPageRowCount() {
		return this.getInternalBounds().getHeight() / this.getRowHeight();
	}

	protected final int getPageCount() {
		final double pages = (double) this.data.size() / this.getPageRowCount();
		final int floor = (int) pages;
		return floor + (pages - floor > 0 ? 1 : 0);
	}

	protected final int getRowsForPage() {
		final int currentPage = this.scrollbar.getValue();
		final int leftover = this.getPageCount() - 1 - currentPage;
		if (leftover >= 1) {
			return this.getPageRowCount();
		} else {
			final int currentIndex = currentPage * this.getPageRowCount();
			return this.data.size() - currentIndex;
		}
	}

	@NotNull
	protected final String[] getVisibleLines() {
		if (this.getRowsForPage() < 0) {
			return new String[0];
		} else {
			final String[] result = new String[this.getRowsForPage()];
			for (int i = 0; i < result.length; ++i) {
				result[i] = this.data.get(this.scrollbar.getValue() * this.getPageRowCount() + i);
			}
			return result;
		}
	}

	@NotNull
	protected final IGuiComponent[][] getVisibleComponents() {
		if (this.getRowsForPage() < 0) {
			return new IGuiComponent[0][0];
		} else {
			final IGuiComponent[][] result = new IGuiComponent[this.getRowsForPage()][];
			for (int i = 0; i < result.length; ++i) {
				result[i] = this.components.get(this.scrollbar.getValue() * this.getPageRowCount() + i);
			}
			return result;
		}
	}

	public final GuiList localize(final boolean localize) {
		this.localize = localize;
		return this;
	}

	public final boolean doLocalize() {
		return this.localize;
	}
}