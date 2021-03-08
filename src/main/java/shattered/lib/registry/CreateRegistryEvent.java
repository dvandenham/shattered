package shattered.lib.registry;

import shattered.core.event.Event;
import shattered.lib.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public final class CreateRegistryEvent extends Event<Void> {

	CreateRegistryEvent() {
	}

	@SuppressWarnings("unused")
	@NotNull
	public <T> Registry<T> create(@NotNull final ResourceLocation resource, @NotNull final Class<T> typeClass, @NotNull final ResourceLocation... dependencies) {
		if (MasterRegistry.registryExists(resource)) {
			throw new IllegalStateException(String.format("Registry %s already exists!", resource));
		}
		final Registry<T> result = new Registry<>(resource, typeClass);
		MasterRegistry.register(resource, result, dependencies);
		return result;
	}
}