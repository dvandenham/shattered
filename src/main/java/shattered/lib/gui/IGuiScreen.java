package shattered.lib.gui;

import java.util.function.Consumer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import shattered.core.event.EventBus;

public abstract class IGuiScreen extends IGuiElement implements IComponentContainer {

	private final GuiPanel components = new GuiScreenComponentPanel();
	@Nullable
	private final String title;

	public IGuiScreen(@Nullable final String title) {
		this.title = title;
		EventBus.register(this);
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

	@Nullable
	public String getTitle() {
		return this.title;
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