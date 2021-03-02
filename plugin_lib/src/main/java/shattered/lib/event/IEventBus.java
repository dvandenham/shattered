package shattered.lib.event;

import org.jetbrains.annotations.NotNull;

public interface IEventBus {

	void register(@NotNull final Object listener);

	void unregister(@NotNull final Object listener);

	@SuppressWarnings("UnusedReturnValue")
	boolean post(@NotNull final Event<?> event);
}