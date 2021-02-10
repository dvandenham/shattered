package shattered.lib.registry;

import shattered.lib.ResourceLocation;

public final class RegistryFrozenException extends RuntimeException {

	private static final long serialVersionUID = -3316605169398999327L;

	RegistryFrozenException(final ResourceLocation registry) {
		super(registry.toString());
	}
}