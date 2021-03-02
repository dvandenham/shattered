package shattered.pack;

import nl.appelgebakje22.preboot.PrebootRegistry;
import org.jetbrains.annotations.NotNull;

public final class PluginContainer {

	@NotNull
	private final PrebootRegistry loader;
	@NotNull
	private final String namespace;

	public PluginContainer(@NotNull final PrebootRegistry loader, @NotNull final String namespace) {
		this.loader = loader;
		this.namespace = namespace;
	}

	@NotNull
	public PrebootRegistry getLoader() {
		return this.loader;
	}

	@NotNull
	public String getNamespace() {
		return this.namespace;
	}
}