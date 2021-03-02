package shattered.lib.event;

import java.util.concurrent.ConcurrentHashMap;
import org.jetbrains.annotations.NotNull;

public final class EventBusHandler {

	static final ConcurrentHashMap<String, IEventBus> BUS_REGISTRY = new ConcurrentHashMap<>();

	private EventBusHandler() {
	}

	@NotNull
	public static IEventBus createBus(@NotNull final String name) {
		String realName = name;
		while (EventBusHandler.BUS_REGISTRY.containsKey(realName)) {
			realName = realName + "_1";
		}
		final IEventBus result = new EventBusImpl(realName);
		EventBusHandler.BUS_REGISTRY.put(realName, result);
		return result;
	}

	private static final IEventBus BUS = EventBusHandler.createBus(EventBus.DEFAULT_BUS_NAME);

	public static void register(@NotNull final Object listener) {
		EventBusHandler.BUS.register(listener);
	}

	public static void unregister(@NotNull final Object listener) {
		EventBusHandler.BUS.unregister(listener);
	}

	public static boolean post(@NotNull final Event<?> event) {
		return EventBusHandler.BUS.post(event);
	}

	public static void register(@NotNull final Object listener, @NotNull final String busName) {
		final IEventBus bus = EventBusHandler.BUS_REGISTRY.get(busName);
		if (bus == null) {
			throw new NullPointerException(String.format("No EventBus with name '%s' found!", busName));
		}
		bus.register(listener);
	}
}