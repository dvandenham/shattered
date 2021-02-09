package shattered.core.event;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.WeakHashMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

public final class EventBusImpl implements IEventBus {

	private static final Logger                                  LOGGER    = LogManager.getLogger("EventBus");
	private final        WeakHashMap<Object, List<EventHandler>> listeners = new WeakHashMap<>();
	private final        String                                  name;

	EventBusImpl(@NotNull final String name) {
		this.name = name;
		EventBus.BUS_REGISTRY.put(name, this);
	}

	@Override
	public void register(@NotNull final Object listener) {
		if (this.listeners.containsKey(listener)) {
			EventBusImpl.LOGGER.warn("[{}]Registered listener twice! Listener class: {}", this.name, listener.getClass().getName());
			return;
		}
		final Set<Method> methods = this.getListenerMethods(listener);
		if (methods.isEmpty()) {
			EventBusImpl.LOGGER.debug("[{}]Skipping registration of invalid listener: {}", this.name, listener.getClass().getName());
			return;
		}
		try {
			final List<EventHandler> listeners = Collections.synchronizedList(new ArrayList<>());
			for (final Method method : methods) {
				listeners.add(new EventHandler(listener, method, method.getGenericParameterTypes()[0] instanceof ParameterizedType));
			}
			this.listeners.put(listener, listeners);
		} catch (final NoSuchMethodException | IllegalAccessException | InvocationTargetException | InstantiationException e) {
			EventBusImpl.LOGGER.error('[' + this.name + "]Could not complete registration of listener: " + listener.getClass().getName(), e);
		}
	}

	@Override
	public void unregister(@NotNull final Object listener) {
		this.listeners.remove(listener);
	}

	@Override
	public boolean post(@NotNull final Event<?> event) {
		for (final List<EventHandler> listeners : new HashSet<>(this.listeners.values())) {
			for (final EventHandler listener : listeners) {
				if (EventBusImpl.canAcceptEvent(listener, event)) {
					listener.invoke(event);
				}
				if (event.isCancelled()) {
					return true;
				}
			}
		}
		return false;
	}

	@NotNull
	private Set<Method> getListenerMethods(@NotNull final Object listener) {
		final Class<?>        clazz  = listener instanceof Class ? (Class<?>) listener : listener.getClass();
		final HashSet<Method> result = new HashSet<>();

		Arrays.stream(clazz.getMethods()).filter(this::isValidListenerMethod).forEach(result::add);
		Arrays.stream(clazz.getDeclaredMethods()).filter(this::isValidListenerMethod).forEach(result::add);

		return result;
	}

	private boolean isValidListenerMethod(@NotNull final Method method) {
		final boolean hasEventListener   = method.isAnnotationPresent(EventListener.class);
		final boolean hasMessageListener = method.isAnnotationPresent(MessageListener.class);
		if (!hasEventListener && !hasMessageListener) {
			return false;
		}
		if (hasEventListener && hasMessageListener) {
			EventBusImpl.LOGGER.error("[{}]Listener {} cannot have an @EventListener and @MessageListener annotation!", this.name, method.getName());
			return false;
		}
		final EventListener info = hasEventListener ? method.getAnnotation(EventListener.class) : method.getAnnotation(MessageListener.class).listenerInfo();
		if (!info.bus().isEmpty() && !info.bus().equals(this.name)) {
			return false;
		}
		if (method.getParameterCount() != 1 || !Event.class.isAssignableFrom(method.getParameterTypes()[0])) {
			EventBusImpl.LOGGER.error("[{}]Listener {} doesn't have valid parameters! Method should have exactly 1 parameter that extends {}", this.name, method.getName(), Event.class.getName());
			return false;
		}
		if (hasMessageListener && !MessageEvent.class.isAssignableFrom(method.getParameterTypes()[0])) {
			EventBusImpl.LOGGER.error("[{}]Listener {} doesn't have valid parameters! Method should have exactly 1 parameter that extends {}", this.name, method.getName(), MessageEvent.class.getName());
			return false;
		}
		return true;
	}

	private static boolean canAcceptEvent(@NotNull final EventHandler handler, @NotNull final Event<?> event) {
		if (!handler.getListenerEventType().isAssignableFrom(event.getClass())) {
			return false;
		}
		final MessageListener message = handler.getMessageInfo();
		if (message != null && !message.value().equals(event.get())) {
			return false;
		}
		final EventListener listener = handler.getListenerInfo();
		return listener.value().length == 0 || Arrays.stream(listener.value()).anyMatch(Type -> Type == event.getClass());
	}
}