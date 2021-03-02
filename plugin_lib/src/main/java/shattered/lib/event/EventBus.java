package shattered.lib.event;

import java.util.function.BiConsumer;
import java.util.function.Function;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings({"unused", "ConstantConditions"})
public final class EventBus {

	public static final String DEFAULT_BUS_NAME = "DEFAULT";
	private static final IEventBus BUS = null;
	private static final Function<String, IEventBus> CREATOR_FUNCTION = null;
	private static final BiConsumer<Object, String> REGISTER_FUNCTION = null;

	private EventBus() {
	}

	@NotNull
	public static IEventBus createBus(@NotNull final String name) {
		return EventBus.CREATOR_FUNCTION.apply(name);
	}

	public static void register(@NotNull final Object listener) {
		EventBus.BUS.register(listener);
	}

	public static void unregister(@NotNull final Object listener) {
		EventBus.BUS.unregister(listener);
	}

	public static boolean post(@NotNull final Event<?> event) {
		return EventBus.BUS.post(event);
	}

	public static void register(@NotNull final Object listener, @NotNull final String busName) {
		EventBus.REGISTER_FUNCTION.accept(listener, busName);
	}
}