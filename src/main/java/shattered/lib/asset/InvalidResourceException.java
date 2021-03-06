package shattered.lib.asset;

import org.jetbrains.annotations.NotNull;
import shattered.lib.ResourceLocation;

public final class InvalidResourceException extends RuntimeException {

	private static final long serialVersionUID = 2226553876956307478L;

	public InvalidResourceException(@NotNull final ResourceLocation requester, @NotNull final String type, @NotNull final ResourceLocation resource) {
		super(String.format("Requested %s %s doesn't exist. Requested by: %s", type, resource, requester));
	}
}