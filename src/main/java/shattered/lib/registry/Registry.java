package shattered.lib.registry;

import java.util.Iterator;
import java.util.Map;
import shattered.lib.ResourceLocation;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class Registry<T> implements Iterable<Map.Entry<ResourceLocation, T>> {

	private final Object2ObjectArrayMap<ResourceLocation, T> mapping = new Object2ObjectArrayMap<>();
	@NotNull
	protected final ResourceLocation resource;
	@NotNull
	protected final Class<T> typeClazz;
	private final boolean unfreezable;
	boolean frozen = false;

	Registry(@NotNull final ResourceLocation resource, @NotNull final Class<T> typeClazz, final boolean unfreezable) {
		this.resource = resource;
		this.typeClazz = typeClazz;
		this.unfreezable = unfreezable;
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

	public final boolean contains(@NotNull final ResourceLocation resource) {
		return this.mapping.containsKey(resource);
	}

	@Nullable
	public final T get(@NotNull final ResourceLocation resource) {
		return this.mapping.get(resource);
	}

	@Nullable
	public final T remove(@NotNull final ResourceLocation resource) {
		if (this.frozen) {
			throw new RegistryFrozenException(this.resource);
		}
		return this.mapping.remove(resource);
	}

	public final void unfreeze() {
		if (!this.unfreezable) {
			throw new SecurityException(String.format("Unfreezing registry %s is forbidden!", this.resource));
		} else if (!this.frozen) {
			throw new IllegalStateException(String.format("Unfrozen registry %s cannot be unfrozen again!", this.resource));
		}
		this.frozen = false;
	}

	public final void refreeze() {
		if (!this.unfreezable) {
			throw new SecurityException(String.format("Freezing registry %s is forbidden!", this.resource));
		} else if (this.frozen) {
			throw new RegistryFrozenException(this.resource);
		} else {
			this.frozen = true;
		}
	}

	public void clearRegistry() {
		if (!this.unfreezable) {
			throw new SecurityException(String.format("Freezing registry %s is forbidden!", this.resource));
		} else if (this.frozen) {
			throw new RegistryFrozenException(this.resource);
		} else {
			this.mapping.clear();
		}
	}

	public final boolean isFrozen() {
		return this.frozen;
	}

	@Override
	@NotNull
	public final Iterator<Map.Entry<ResourceLocation, T>> iterator() {
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
}