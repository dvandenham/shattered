package shattered.core.event;

import java.util.concurrent.ConcurrentHashMap;
import org.jetbrains.annotations.NotNull;

public final class EventBus {

	private EventBus() {
	}

	static final ConcurrentHashMap<String, IEventBus> BUS_REGISTRY = new ConcurrentHashMap<>();

	@NotNull
	public static IEventBus createBus(@NotNull final String name) {
		String realName = name;
		while (EventBus.BUS_REGISTRY.containsKey(realName)) {
			realName = realName + "_1";
		}
		final IEventBus result = new EventBusImpl(realName);
		EventBus.BUS_REGISTRY.put(realName, result);
		return result;
	}

	private static final IEventBus BUS         = EventBus.createBus(EventBus.DEFAULT_BUS);
	static final         String    DEFAULT_BUS = "DEFAULT";

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
		final IEventBus bus = EventBus.BUS_REGISTRY.get(busName);
		if (bus == null) {
			throw new NullPointerException(String.format("No EventBus with name '%s' found!", busName));
		}
		bus.register(listener);
	}
}