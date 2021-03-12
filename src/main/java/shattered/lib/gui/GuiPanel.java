package shattered.lib.gui;

import java.util.function.Consumer;
import java.util.function.Predicate;
import shattered.lib.gfx.FontRenderer;
import shattered.lib.gfx.Tessellator;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.jetbrains.annotations.NotNull;

public abstract class GuiPanel extends IGuiComponent implements IComponentContainer {

	private final ObjectArrayList<IGuiComponent> components = new ObjectArrayList<>();

	@Override
	public void add(@NotNull final IGuiComponent component) {
		if (!this.hasComponent(component)) {
			this.components.add(component);
		}
	}

	@Override
	public void remove(@NotNull final IGuiComponent component) {
		this.components.remove(component);
	}

	@Override
	public void doForAll(final Consumer<IGuiComponent> action, final Predicate<IGuiComponent> predicate) {
		this.components.stream().filter(predicate).forEach(action);
	}

	@Override
	public void render(@NotNull final Tessellator tessellator, @NotNull final FontRenderer fontRenderer) {
	}

	@Override
	public boolean hasComponent(@NotNull final IGuiComponent component) {
		return this.components.contains(component);
	}

	@Override
	public boolean deepHasComponent(@NotNull final IGuiComponent component) {
		return this.hasComponent(component) || this.components.stream()
				.filter(c -> c instanceof IComponentContainer)
				.map(c -> (IComponentContainer) c)
				.anyMatch(c -> c.deepHasComponent(component));
	}
}