package shattered.lib.registry;

import java.util.Iterator;
import java.util.Map;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import shattered.lib.ResourceLocation;

public final class ResourceSingletonRegistry<T> extends BaseRegistry<ResourceLocation, T> {

	private final Object2ObjectArrayMap<ResourceLocation, T> mapping = new Object2ObjectArrayMap<>();

	protected ResourceSingletonRegistry(@NotNull final ResourceLocation resource) {
		super(resource);
	}

	@Override
	protected void registerUnsafe(@NotNull final ResourceLocation key, @Nullable final T value) {
		if (this.mapping.containsKey(key)) {
			throw new IllegalStateException(String.format("[%s]Registry already contains object %s", this.resource, key));
		}
		this.mapping.put(key, value);
	}

	@Override
	public boolean contains(@NotNull final ResourceLocation key) {
		return this.mapping.containsKey(key);
	}

	@Override
	@Nullable
	public T get(@NotNull final ResourceLocation key) {
		return this.mapping.get(key);
	}

	@Override
	@Nullable
	protected T removeUnsafe(@NotNull final ResourceLocation key) {
		return this.mapping.remove(key);
	}

	@Override
	@NotNull
	public Iterator<Map.Entry<ResourceLocation, T>> iterator() {
		return new ReadOnlyIterator<>(this.mapping.entrySet().iterator());
	}
}