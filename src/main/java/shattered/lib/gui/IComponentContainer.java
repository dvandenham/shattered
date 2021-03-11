package shattered.lib.gui;

import java.util.function.Consumer;
import java.util.function.Predicate;
import org.jetbrains.annotations.NotNull;

public interface IComponentContainer {

	void add(@NotNull IGuiComponent component);

	void remove(@NotNull IGuiComponent component);

	boolean hasComponent(@NotNull IGuiComponent component);

	boolean deepHasComponent(@NotNull IGuiComponent component);

	void setupComponents(@NotNull Layout layout);

	void doForAll(Consumer<IGuiComponent> action, Predicate<IGuiComponent> predicate);
}