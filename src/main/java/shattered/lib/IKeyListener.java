package shattered.lib;

import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface IKeyListener {

	void onKeybindChanged(@NotNull final KeyBind keybind);
}