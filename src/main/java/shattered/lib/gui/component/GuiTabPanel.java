package shattered.lib.gui.component;

import java.util.function.Consumer;
import java.util.function.Predicate;
import shattered.Assets;
import shattered.core.event.EventBus;
import shattered.lib.Color;
import shattered.lib.Helper;
import shattered.lib.Input;
import shattered.lib.gfx.FontRenderer;
import shattered.lib.gfx.StringData;
import shattered.lib.gfx.Tessellator;
import shattered.lib.gui.GuiHelper;
import shattered.lib.gui.IComponentContainer;
import shattered.lib.gui.IGuiCacheable;
import shattered.lib.gui.IGuiComponent;
import shattered.lib.gui.IGuiTickable;
import shattered.lib.gui.Layout;
import shattered.lib.math.Rectangle;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GuiTabPanel extends IGuiComponent implements IComponentContainer, IGuiTickable, IGuiCacheable {

	private final ObjectArrayList<GuiTab> tabs = new ObjectArrayList<>();
	private final Object2ObjectArrayMap<GuiTab, Rectangle> cachedTabButtons = new Object2ObjectArrayMap<>();
	private final boolean topTabs;
	private int currentTab = 0;
	@Nullable
	private Rectangle cachedInternalBounds;

	public GuiTabPanel(final boolean topTabs) {
		this.topTabs = topTabs;
	}

	public GuiTabPanel() {
		this(true);
	}

	@Override
	public void setupComponents(@NotNull final Layout layout) {
		final Rectangle bounds = this.getInternalBounds();
		for (int i = 0; i < this.getTabCount(); ++i) {
			final Layout newLayout = layout.recreate(
					layout.getComponentHeight(),
					layout.getComponentSpacing(),
					bounds.getX(), bounds.getY(),
					bounds.getWidth(), bounds.getHeight()
			);
			this.tabs.get(i).setupComponents(newLayout);
		}
	}

	@Override
	public void add(@NotNull final IGuiComponent component) {
		final GuiTab tab = this.getCurrentTab();
		if (tab != null) {
			tab.add(component);
		}
	}

	@Override
	public void remove(@NotNull final IGuiComponent component) {
		final GuiTab tab = this.getCurrentTab();
		if (tab != null) {
			tab.remove(component);
		}
	}

	@Override
	public boolean hasComponent(@NotNull final IGuiComponent component) {
		if (component instanceof GuiTab) {
			return this.tabs.contains(component);
		} else {
			final GuiTab tab = this.getCurrentTab();
			return tab != null && tab.hasComponent(component);
		}
	}

	@Override
	public boolean deepHasComponent(@NotNull final IGuiComponent component) {
		if (component instanceof GuiTab) {
			return this.tabs.contains(component);
		} else {
			final GuiTab tab = this.getCurrentTab();
			return tab != null && tab.deepHasComponent(component);
		}
	}

	@Override
	public void doForAll(final Consumer<IGuiComponent> action, final Predicate<IGuiComponent> predicate) {
		final GuiTab tab = this.getCurrentTab();
		if (tab != null) {
			tab.doForAll(action, predicate);
		}
	}

	@Override
	public void cache() {
		this.cachedInternalBounds = null;
		this.cachedTabButtons.clear();
		for (int i = 0; i < this.tabs.size(); ++i) {
			final GuiTab tab = this.tabs.get(i);
			this.cachedTabButtons.put(tab, this.getTabButtonBounds(i));
			tab.setBounds(this.getInternalBounds());
			if (tab instanceof IGuiCacheable) {
				((IGuiCacheable) tab).cache();
			}
			tab.doForAll(component -> ((IGuiCacheable) component).cache(), component -> component instanceof IGuiCacheable);
		}
	}

	@Override
	public void tick() {
		for (int i = 0; i < this.getTabCount(); ++i) {
			if (i != this.currentTab) {
				final Rectangle tabBounds = this.cachedTabButtons.get(this.tabs.get(i));
				if (Input.containsMouse(tabBounds) && Input.isMouseLeftClicked()) {
					this.currentTab = i;
					break;
				}
			}
		}
		final GuiTab tab = this.getCurrentTab();
		if (tab != null) {
			if (tab instanceof IGuiTickable) {
				((IGuiTickable) tab).tick();
			}
			this.doForAll(component -> ((IGuiTickable) component).tick(), component -> component instanceof IGuiTickable);
		}
	}

	@Override
	public void render(@NotNull final Tessellator tessellator, @NotNull final FontRenderer fontRenderer) {
		fontRenderer.setFont(Assets.FONT_SIMPLE);
		fontRenderer.setFontSize(24);
		for (int i = 0; i < this.getTabCount(); ++i) {
			final GuiTab tab = this.tabs.get(i);
			final Rectangle tabBounds = this.cachedTabButtons.get(tab);

			final Color bgColor;
			final Color textColor;
			if (i == this.currentTab) {
				bgColor = Color.XEROS;
				textColor = Color.WHITE;
			} else if (Input.containsMouse(tabBounds)) {
				bgColor = Color.RED;
				textColor = Color.WHITE;
			} else {
				bgColor = Color.WHITE;
				textColor = Color.BLACK;
			}

			tessellator.drawQuick(tabBounds, bgColor.withAlpha(0.5F));
			fontRenderer.writeQuickCentered(tabBounds, new StringData(tab.getTitle(), textColor).localize(tab.localizeTitle()));
		}
		fontRenderer.revertFontSize();
		fontRenderer.resetFont();
		final GuiTab tab = this.getCurrentTab();
		if (tab != null) {
			tab.render(tessellator, fontRenderer);
			this.doForAll(component -> component.render(tessellator, fontRenderer), Helper.testTrue());
		}
	}

	@NotNull
	public final GuiTabPanel addTab(@NotNull final GuiTab tab) {
		tab.panel = this;
		if (!this.hasTab(tab)) {
			this.tabs.add(tab);
			EventBus.register(tab);
			this.cache();
		}
		return this;
	}

	@NotNull
	public final GuiTabPanel removeTab(@NotNull final GuiTab tab) {
		tab.panel = null;
		if (this.hasTab(tab)) {
			this.tabs.remove(tab);
			EventBus.unregister(tab);
			this.cache();
		}
		return this;
	}

	public final boolean hasTab(@NotNull final GuiTab tab) {
		return this.tabs.contains(tab);
	}

	protected final int getTabCount() {
		return this.tabs.size();
	}

	@Nullable
	protected final GuiTab getCurrentTab() {
		if (this.currentTab < 0 || this.currentTab >= this.getTabCount()) {
			return null;
		} else {
			return this.tabs.get(this.currentTab);
		}
	}

	public final boolean topTabs() {
		return this.topTabs;
	}

	protected int getTabButtonHeight() {
		return GuiHelper.COMPONENT_HEIGHT;
	}

	@NotNull
	protected final Rectangle getTabButtonBounds(final int tabIndex) {
		if (tabIndex < 0 || tabIndex >= this.getTabCount()) {
			return Rectangle.EMPTY;
		} else {
			final int tabButtonWidth = this.getWidth() / this.getTabCount();
			if (this.topTabs()) {
				return Rectangle.create(
						this.getX() + tabButtonWidth * tabIndex, this.getY(),
						tabButtonWidth, this.getTabButtonHeight()
				);
			} else {
				return Rectangle.create(
						this.getX() + tabButtonWidth * tabIndex, this.getY() + this.getHeight() - this.getTabButtonHeight(),
						tabButtonWidth, this.getTabButtonHeight()
				);
			}
		}
	}

	@NotNull
	protected final Rectangle getInternalBounds() {
		if (this.cachedInternalBounds == null) {
			this.cachedInternalBounds = Rectangle.create(
					this.getX(), this.getY() + (this.topTabs() ? this.getTabButtonHeight() : 0),
					this.getWidth(), this.getHeight() - this.getTabButtonHeight()
			);
		}
		return this.cachedInternalBounds;
	}
}