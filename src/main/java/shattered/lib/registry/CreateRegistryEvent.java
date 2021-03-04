package shattered.lib.registry;

import org.jetbrains.annotations.NotNull;
import shattered.core.event.Event;
import shattered.lib.ResourceLocation;

public final class CreateRegistryEvent extends Event<Void> {

	CreateRegistryEvent() {
	}

	@SuppressWarnings("unused")
	@NotNull
	public <T> Registry<T> create(@NotNull final ResourceLocation resource, @NotNull final Class<T> typeClass) {
		if (Registry.REGISTRIES.containsKey(resource)) {
			throw new IllegalStateException(String.format("Registry %s already exists!", resource));
		}
		final Registry<T> result = new Registry<>(resource, typeClass);
		Registry.REGISTRIES.put(resource, result);
		return result;
	}
}