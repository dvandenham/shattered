package shattered.lib.registry;

import shattered.lib.ResourceLocation;

public final class RegistryFrozen extends RuntimeException {

	private static final long serialVersionUID = -3316605169398999327L;

	RegistryFrozen(final ResourceLocation registry) {
		super(registry.toString());
	}
}