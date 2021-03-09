package shattered.lib.gui;

import java.util.function.Consumer;
import shattered.Assets;
import shattered.Shattered;
import shattered.core.event.EventListener;
import shattered.lib.Color;
import shattered.lib.Input;
import shattered.lib.gfx.Display;
import shattered.lib.gfx.DisplayResizedEvent;
import shattered.lib.gfx.FontRenderer;
import shattered.lib.gfx.PolygonBuilder;
import shattered.lib.gfx.StringData;
import shattered.lib.gfx.Tessellator;
import shattered.lib.math.Dimension;
import shattered.lib.math.Point;
import shattered.lib.math.Rectangle;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class IGuiScreen implements IComponentContainer {

	private final GuiPanel components = new GuiScreenComponentPanel();
	private final Rectangle bounds = GuiHelper.BOUNDS_FULLSCREEN.toMutable();
	private final Dimension sizeMin = GuiHelper.BOUNDS_IGNORE.toMutable();
	private final Dimension sizeMax = GuiHelper.BOUNDS_IGNORE.toMutable();
	private final Rectangle boundsCached = Rectangle.createMutable(0, 0, 0, 0);
	private final Rectangle titlebarBoundsCached = Rectangle.createMutable(0, 0, 0, 0);
	private final Rectangle closeButtonBoundsCached = Rectangle.createMutable(0, 0, 0, 0);
	private final Rectangle internalBoundsCached = Rectangle.createMutable(0, 0, 0, 0);
	@Nullable
	private String title = null;
	private boolean localizeTitle = true;
	private boolean hasTitlebar = true;
	private boolean hasCloseButton = true;
	private Point draggingOriginPoint = null;
	private StringData titleDataCached = null;
	GuiManager manager = null;

	public IGuiScreen(@NotNull final String title) {
		this.title = title;
	}

	public IGuiScreen() {
	}

	protected final void setHasTitlebar(final boolean hasTitlebar) {
		if (this.hasTitlebar == hasTitlebar) {
			return;
		}
		this.hasTitlebar = hasTitlebar;
		this.cacheBounds();
	}

	protected final void setHasCloseButton(final boolean hasCloseButton) {
		if (this.hasCloseButton == hasCloseButton) {
			return;
		}
		this.hasCloseButton = hasCloseButton;
		this.cacheBounds();
	}

	protected final void localizeTitle(final boolean localize) {
		this.localizeTitle = localize;
	}

	protected void tick() {
	}

	protected abstract void renderBackground(@NotNull Tessellator tessellator, @NotNull FontRenderer fontRenderer);

	protected abstract void renderForeground(@NotNull Tessellator tessellator, @NotNull FontRenderer fontRenderer);

	protected void renderTitlebar(@NotNull final Tessellator tessellator, @NotNull final FontRenderer fontRenderer) {
		if (this.hasTitlebar) {
			tessellator.drawQuick(this.titlebarBoundsCached, Color.WHITE.withAlpha(0.5F));
			if (!this.isFullscreen()) {
				tessellator.drawQuick(this.getX(), this.titlebarBoundsCached.getMaxY(),
						GuiHelper.BORDER_SIZE, this.getHeight() - this.titlebarBoundsCached.getHeight(),
						Color.WHITE.withAlpha(0.5F)
				);
				tessellator.drawQuick(this.getBounds().getMaxX() - GuiHelper.BORDER_SIZE, this.titlebarBoundsCached.getMaxY(),
						GuiHelper.BORDER_SIZE, this.getHeight() - this.titlebarBoundsCached.getHeight(),
						Color.WHITE.withAlpha(0.5F)
				);
				tessellator.drawQuick(this.getX(), this.getBounds().getMaxY() - GuiHelper.BORDER_SIZE,
						this.getWidth(), GuiHelper.BORDER_SIZE,
						Color.WHITE.withAlpha(0.5F)
				);
			}
			if (this.titleDataCached != null) {
				fontRenderer.setFont(Assets.FONT_SIMPLE);
				fontRenderer.setFontSize(this.titlebarBoundsCached.getHeight() / 3 * 2);
				fontRenderer.writeQuick(this.titlebarBoundsCached.getPosition().moveX(GuiHelper.BORDER_SIZE), this.titleDataCached);
				fontRenderer.revertFontSize();
				fontRenderer.resetFont();
			}
			if (this.hasCloseButton) {
				tessellator.drawQuick(this.closeButtonBoundsCached, Color.RED.withAlpha(0.5F));
				final PolygonBuilder polygons = tessellator.createPolygon();
				polygons.start(Color.WHITE);
				polygons.add(this.closeButtonBoundsCached.getPosition().move(GuiHelper.BORDER_SIZE, GuiHelper.BORDER_SIZE));
				polygons.add(this.closeButtonBoundsCached.getPosition().move(GuiHelper.BORDER_SIZE * 2, GuiHelper.BORDER_SIZE));
				polygons.add(this.closeButtonBoundsCached.getMaxPosition().move(-GuiHelper.BORDER_SIZE, -(GuiHelper.BORDER_SIZE * 2)));
				polygons.add(this.closeButtonBoundsCached.getMaxPosition().move(-GuiHelper.BORDER_SIZE, -GuiHelper.BORDER_SIZE));
				polygons.add(this.closeButtonBoundsCached.getMaxPosition().move(-(GuiHelper.BORDER_SIZE * 2), -GuiHelper.BORDER_SIZE));
				polygons.add(this.closeButtonBoundsCached.getPosition().move(GuiHelper.BORDER_SIZE, GuiHelper.BORDER_SIZE * 2));
				polygons.draw();
				polygons.start(Color.WHITE);
				polygons.add(this.closeButtonBoundsCached.getPosition().move(this.closeButtonBoundsCached.getWidth() - GuiHelper.BORDER_SIZE, GuiHelper.BORDER_SIZE));
				polygons.add(this.closeButtonBoundsCached.getPosition().move(this.closeButtonBoundsCached.getWidth() - GuiHelper.BORDER_SIZE, GuiHelper.BORDER_SIZE * 2));
				polygons.add(this.closeButtonBoundsCached.getPosition().move(GuiHelper.BORDER_SIZE * 2, this.closeButtonBoundsCached.getHeight() - GuiHelper.BORDER_SIZE));
				polygons.add(this.closeButtonBoundsCached.getPosition().move(GuiHelper.BORDER_SIZE, this.closeButtonBoundsCached.getHeight() - GuiHelper.BORDER_SIZE));
				polygons.add(this.closeButtonBoundsCached.getPosition().move(GuiHelper.BORDER_SIZE, this.closeButtonBoundsCached.getHeight() - (GuiHelper.BORDER_SIZE * 2)));
				polygons.add(this.closeButtonBoundsCached.getPosition().move(this.closeButtonBoundsCached.getWidth() - GuiHelper.BORDER_SIZE * 2, GuiHelper.BORDER_SIZE));
				polygons.draw();
			}
		}
	}

	void tickTitlebar() {
		if (this.hasTitlebar) {
			if (this.draggingOriginPoint != null || (Input.containsMouse(this.titlebarBoundsCached) && Input.isMouseDragging())) {
				if (!Input.isMouseDragging()) {
					this.draggingOriginPoint = null;
				} else {
					if (this.draggingOriginPoint == null) {
						this.draggingOriginPoint = this.getPosition();
					}
					this.setX(Math.max(this.draggingOriginPoint.getX() + Input.getDraggedDX(), 0));
					this.setY(Math.max(this.draggingOriginPoint.getY() + Input.getDraggedDY(), 0));
					this.manager.setupComponents(this);
				}
			} else if (this.hasCloseButton && Input.containsMouse(this.closeButtonBoundsCached)) {
				if (Input.isMouseLeftClicked()) {
					this.closeScreen();
				}
			}
		}
	}

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

	protected void openScreen(@NotNull final IGuiScreen screen) {
		if (screen != this) {
			Shattered.getInstance().getGuiManager().openScreen(screen);
		}
	}

	protected void closeScreen() {
		Shattered.getInstance().getGuiManager().closeScreen(this);
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

	protected final void setMinSize(final int width, final int height) {
		this.sizeMin.setWidth(width).setHeight(height);
		this.cacheBounds();
	}

	protected final void setMinSize(@NotNull final Dimension size) {
		this.sizeMin.setWidth(size.getWidth()).setHeight(size.getHeight());
		this.cacheBounds();
	}

	protected final void setMaxSize(final int width, final int height) {
		this.sizeMax.setWidth(width).setHeight(height);
		this.cacheBounds();
	}

	protected final void setMaxSize(@NotNull final Dimension size) {
		this.sizeMax.setWidth(size.getWidth()).setHeight(size.getHeight());
		this.cacheBounds();
	}

	public final int getX() {
		return this.boundsCached.getX();
	}

	public final int getY() {
		return this.boundsCached.getY();
	}

	@NotNull
	public final Point getPosition() {
		return this.boundsCached.getPosition().toImmutable();
	}

	public final int getWidth() {
		return this.boundsCached.getWidth();
	}

	public final int getHeight() {
		return this.boundsCached.getHeight();
	}

	@NotNull
	public final Dimension getSize() {
		return this.boundsCached.getSize().toImmutable();
	}

	@NotNull
	public final Rectangle getBounds() {
		return this.boundsCached.toImmutable();
	}

	public final int getInternalX() {
		return this.internalBoundsCached.getX();
	}

	public final int getInternalY() {
		return this.internalBoundsCached.getY();
	}

	@NotNull
	public final Point getInternalPosition() {
		return this.internalBoundsCached.getPosition().toImmutable();
	}

	public final int getInternalWidth() {
		return this.internalBoundsCached.getWidth();
	}

	public final int getInternalHeight() {
		return this.internalBoundsCached.getHeight();
	}

	@NotNull
	public final Dimension getInternalSize() {
		return this.internalBoundsCached.getSize().toImmutable();
	}

	@NotNull
	public final Rectangle getInternalBounds() {
		return this.internalBoundsCached.toImmutable();
	}

	@NotNull
	public final Rectangle getTitlebarBounds() {
		return this.titlebarBoundsCached;
	}

	public final boolean isFullscreen() {
		return this.boundsCached.equals(Display.getBounds());
	}

	public final boolean hasTitlebar() {
		return this.hasTitlebar;
	}

	@Nullable
	public final String getTitle() {
		return this.title;
	}

	void cacheBounds() {
		final Rectangle newBounds = GuiHelper.getCorrectBounds(this.bounds, this.sizeMin, this.sizeMax);
		this.boundsCached.setPosition(newBounds.getPosition()).setSize(newBounds.getSize());
		this.titlebarBoundsCached.setPosition(newBounds.getPosition());
		if (this.hasTitlebar) {
			this.titlebarBoundsCached.setSize(newBounds.getWidth(), GuiHelper.TITLEBAR_SIZE);
			if (this.hasCloseButton) {
				this.titlebarBoundsCached.shrinkX(GuiHelper.CLOSE_BUTTON_WIDTH);
				this.closeButtonBoundsCached
						.setPosition(this.titlebarBoundsCached.getMaxX(), this.titlebarBoundsCached.getY())
						.setSize(GuiHelper.CLOSE_BUTTON_WIDTH, GuiHelper.CLOSE_BUTTON_HEIGHT);
			}
			if (this.title != null) {
				this.titleDataCached = new StringData(this.title, Color.BLACK)
						.wrap(this.titlebarBoundsCached.getWidth(), true)
						.centerY(this.titlebarBoundsCached.getHeight())
						.localize(this.localizeTitle);
			}
		} else {
			this.titlebarBoundsCached.setSize(0, 0);
		}
		this.internalBoundsCached
				.setPosition(this.titlebarBoundsCached.getX(), this.titlebarBoundsCached.getMaxY())
				.move(GuiHelper.BORDER_SIZE, GuiHelper.BORDER_SIZE)
				.setSize(newBounds.getSize())
				.shrink(GuiHelper.BORDER_SIZE * 2, GuiHelper.BORDER_SIZE * 2)
				.shrink(0, this.titlebarBoundsCached.getHeight());
		if (this.hasTitlebar && !this.isFullscreen()) {
			this.internalBoundsCached
					.move(GuiHelper.BORDER_SIZE, 0)
					.shrink(GuiHelper.BORDER_SIZE * 2, GuiHelper.BORDER_SIZE);
		}
	}

	@EventListener
	private void onDisplayResized(final DisplayResizedEvent ignored) {
		this.cacheBounds();
		this.setupComponents(GuiHelper.createDefaultLayout(this));
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