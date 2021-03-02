package shattered.lib.registry;

import java.util.Iterator;
import java.util.Map;
import shattered.lib.ResourceLocation;
import shattered.lib.event.EventBusSubscriber;
import shattered.lib.event.MessageEvent;
import shattered.lib.event.MessageListener;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

abstract class BaseRegistry<K, V> implements Iterable<Map.Entry<K, V>> {

	static final Object2ObjectArrayMap<ResourceLocation, BaseRegistry<?, ?>> REGISTRIES = new Object2ObjectArrayMap<>();
	@NotNull
	protected final ResourceLocation resource;
	private boolean frozen = false;

	protected BaseRegistry(@NotNull final ResourceLocation resource) {
		this.resource = resource;
	}

	public final void register(@NotNull final K key, @Nullable final V value) {
		if (this.frozen) {
			throw new RegistryFrozenException(this.resource);
		}
		this.registerUnsafe(key, value);
	}

	protected abstract void registerUnsafe(@NotNull K key, @Nullable V value);

	public abstract boolean contains(@NotNull K key);

	@Nullable
	public abstract V get(@NotNull K key);

	@Nullable
	public final V remove(@NotNull final K key) {
		if (this.frozen) {
			throw new RegistryFrozenException(this.resource);
		}
		return this.removeUnsafe(key);
	}

	@Nullable
	protected abstract V removeUnsafe(@NotNull K key);

	public final boolean isFrozen() {
		return this.frozen;
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

	@EventBusSubscriber
	private static class EventHandler {

		@MessageListener("registry_freeze")
		public static void onRegistryFreeze(final MessageEvent ignored) {
			BaseRegistry.REGISTRIES.values().forEach(registry -> registry.frozen = true);
		}
	}
}