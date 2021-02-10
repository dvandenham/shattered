package shattered.lib.registry;

import org.jetbrains.annotations.NotNull;
import shattered.core.event.Event;
import shattered.lib.ResourceLocation;

public final class CreateRegistryEvent extends Event<Void> {

	CreateRegistryEvent() {
	}

	@NotNull
	public <T> ResourceSingletonRegistry<T> newResourceSingletonRegistry(@NotNull final ResourceLocation resource, @NotNull final Class<T> typeClass) {
		if (BaseRegistry.REGISTRIES.containsKey(resource)) {
			throw new IllegalStateException(String.format("Registry %s already exists!", resource));
		}
		final ResourceSingletonRegistry<T> result = new ResourceSingletonRegistry<>(resource);
		BaseRegistry.REGISTRIES.put(resource, result);
		return result;
	}
}