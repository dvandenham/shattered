package shattered.pack;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import nl.appelgebakje22.preboot.PrebootRegistry;
import nl.appelgebakje22.preboot.PrebootRegistryImpl;
import org.jetbrains.annotations.NotNull;
import shattered.Shattered;
import shattered.plugin.Plugin;

final class PluginLoader {

	@NotNull
	private final File file;
	private final PrebootRegistry loader;
	private final Plugin info;

	PluginLoader(@NotNull final File file) {
		this.file = file;

		try {
			this.loader = new PrebootRegistryImpl(new URL[]{file.toURI().toURL()}, new Class[]{
					Plugin.class
			}, name -> !name.startsWith(Shattered.class.getPackage().getName()));
		} catch (final MalformedURLException e) {
			throw new RuntimeException(e);
		}
		this.loader.load();

		final Class<?> pluginClass = this.loader.getAnnotatedClasses(Plugin.class).get(0);
		this.info = pluginClass.getAnnotation(Plugin.class);
	}

	public PrebootRegistry getLoader() {
		return this.loader;
	}

	public Plugin getInfo() {
		return this.info;
	}
}