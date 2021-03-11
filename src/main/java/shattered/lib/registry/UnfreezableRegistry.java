package shattered.lib.registry;

import shattered.lib.ResourceLocation;
import org.jetbrains.annotations.NotNull;

final class UnfreezableRegistry<T> extends Registry<T> {

	UnfreezableRegistry(@NotNull final ResourceLocation resource, @NotNull final Class<T> typeClazz) {
		super(resource, typeClazz, true);
	}
}
