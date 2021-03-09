package shattered.lib.registry;

import shattered.lib.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public final class NotRegisteredException extends RuntimeException {
	private static final long serialVersionUID = -1293755119320972696L;

	public NotRegisteredException(@NotNull final String type, @NotNull final ResourceLocation resource) {
		super("Resource (type: " + type + ") " + resource + " has not been registered!");
	}
}