package shattered.core;

import org.jetbrains.annotations.NotNull;
import shattered.lib.ResourceLocation;

public final class RuntimeScriptException extends RuntimeException {

	private static final long serialVersionUID = -3322940332745422589L;

	RuntimeScriptException(@NotNull final ResourceLocation resource, @NotNull final String msg) {
		super(String.format("Script \"%s\" raised the following error:\n\t%s", resource, msg));
		this.setStackTrace(new StackTraceElement[0]);
	}
}