package shattered.lib.registry;

import java.util.Iterator;
import java.util.Map;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import org.jetbrains.annotations.NotNull;
import shattered.lib.ResourceLocation;

public final class ResourceSingletonRegistry<T> extends BaseRegistry<ResourceLocation, T> {

	private final Object2ObjectArrayMap<ResourceLocation, T> map = new Object2ObjectArrayMap<>();

	@NotNull
	@Override
	public Iterator<Map.Entry<ResourceLocation, T>> iterator() {
		return new ReadOnlyIterator(this.map.entrySet().iterator());
	}
}