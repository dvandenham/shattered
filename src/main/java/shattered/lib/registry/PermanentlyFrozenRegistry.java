package shattered.lib.registry;

import shattered.lib.ResourceLocation;
import org.jetbrains.annotations.NotNull;

final class PermanentlyFrozenRegistry<T> extends Registry<T> {

	PermanentlyFrozenRegistry(@NotNull final ResourceLocation resource, @NotNull final Class<T> typeClazz) {
		super(resource, typeClazz, false);
	}
}