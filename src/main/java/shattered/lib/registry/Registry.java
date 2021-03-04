package shattered.lib.registry;

import java.util.Iterator;
import java.util.Map;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import shattered.core.event.EventBusSubscriber;
import shattered.core.event.MessageEvent;
import shattered.core.event.MessageListener;
import shattered.Shattered;
import shattered.lib.ResourceLocation;

public final class Registry<T> implements Iterable<Map.Entry<ResourceLocation, T>> {

	static final Object2ObjectArrayMap<ResourceLocation, Registry<?>> REGISTRIES = new Object2ObjectArrayMap<>();

	private final Object2ObjectArrayMap<ResourceLocation, T> mapping = new Object2ObjectArrayMap<>();
	@NotNull
	protected final ResourceLocation resource;
	@NotNull
	protected Class<T> typeClazz;
	private boolean frozen = false;

	Registry(@NotNull final ResourceLocation resource, @NotNull final Class<T> typeClazz) {
		this.resource = resource;
		this.typeClazz = typeClazz;
	}

	public final void register(@NotNull final ResourceLocation resource, @Nullable final T value) {
		if (this.frozen) {
			throw new RegistryFrozenException(this.resource);
		}
		if (this.mapping.containsKey(resource)) {
			throw new IllegalStateException(String.format("[%s]Registry already contains object %s", this.resource, resource));
		}
		this.mapping.put(resource, value);
	}

	public boolean contains(@NotNull final ResourceLocation resource) {
		return this.mapping.containsKey(resource);
	}

	@Nullable
	public T get(@NotNull final ResourceLocation resource) {
		return this.mapping.get(resource);
	}

	@Nullable
	public final T remove(@NotNull final ResourceLocation resource) {
		if (this.frozen) {
			throw new RegistryFrozenException(this.resource);
		}
		return this.mapping.remove(resource);
	}

	public final boolean isFrozen() {
		return this.frozen;
	}

	@Override
	@NotNull
	public Iterator<Map.Entry<ResourceLocation, T>> iterator() {
		return new ReadOnlyIterator<>(this.mapping.entrySet().iterator());
	}

	protected static final class ReadOnlyIterator<T> implements Iterator<T> {

		private final Iterator<T> delegate;

		public ReadOnlyIterator(final Iterator<T> delegate) {
			this.delegate = delegate;
		}

		@Override
		public boolean hasNext() {
			return this.delegate.hasNext();
		}

		@Override
		public T next() {
			return this.delegate.next();
		}
	}

	@EventBusSubscriber(Shattered.SYSTEM_BUS_NAME)
	private static class EventHandler {

		@MessageListener("freeze_registries")
		public static void onRegistryFreeze(final MessageEvent ignored) {
			Registry.REGISTRIES.values().forEach(registry -> registry.frozen = true);
		}
	}
}