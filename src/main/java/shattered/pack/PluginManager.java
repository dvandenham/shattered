package shattered.pack;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import shattered.Shattered;
import shattered.lib.FastNamedObjectMap;
import shattered.lib.event.MessageEvent;
import shattered.lib.event.MessageListener;
import shattered.plugin.Plugin;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;

public final class PluginManager {

	private static final Logger LOGGER = LogManager.getLogger("Plugins");
	private final FastNamedObjectMap<PluginContainer> loadedPlugins = new FastNamedObjectMap<>();
	private final File packDir;

	public PluginManager(@NotNull final File dataDir) {
		this.packDir = new File(dataDir, "plugins");
		if (!this.packDir.exists() && !this.packDir.mkdirs()) {
			Shattered.crash("Could not create plugin directory! Expected path: (" + this.packDir.getAbsolutePath() + ')');
		}
		Shattered.SYSTEM_BUS.register(this);
	}

	@MessageListener("load_plugins")
	private void onLoadPlugins(final MessageEvent ignored) {
		PluginManager.LOGGER.debug("Discovering plugins");
		final HashSet<File> candidates = new HashSet<>();
		this.discoverCandidates(candidates, this.packDir);

		PluginManager.LOGGER.debug("Validating plugins");
		final HashMap<File, String> pluginClassMap = new HashMap<>();
		candidates.forEach(candidate -> {
			final String pluginClassName = this.findPluginClass(candidate);
			if (pluginClassName != null) {
				pluginClassMap.put(candidate, pluginClassName);
			}
		});

		PluginManager.LOGGER.debug("Loading plugins");
		pluginClassMap.keySet().forEach(jarFile -> {
			final PluginLoader loader = new PluginLoader(jarFile);

			final PluginContainer container = new PluginContainer(loader.getLoader(), loader.getInfo().value());
			PluginManager.LOGGER.debug("\tLoaded plugin {}", container.getNamespace());
			this.loadedPlugins.put(container.getNamespace(), container);
		});
	}

	private void discoverCandidates(final Set<File> set, @NotNull final File parentDir) {
		PluginManager.LOGGER.debug("Scanning directory {} for plugin candidates", parentDir.getAbsolutePath());
		for (final File file : parentDir.listFiles(file -> file.isDirectory() || file.getName().endsWith(".jar"))) {
			if (file.isDirectory()) {
				this.discoverCandidates(set, file);
			} else {
				PluginManager.LOGGER.debug("\tFound plugin candidate file {}", file.getAbsolutePath());
				set.add(file);
			}
		}
	}

	private String findPluginClass(@NotNull final File plugin) {
		try (final JarFile jar = new JarFile(plugin)) {
			for (final Enumeration<JarEntry> entries = jar.entries(); entries.hasMoreElements(); ) {
				final JarEntry entry = entries.nextElement();
				try (final InputStream input = jar.getInputStream(entry)) {
					final ClassReader classReader = new ClassReader(input);
					final ClassNode node = new ClassNode();
					classReader.accept(node, 0);
					final boolean hasAnnotation = node.visibleAnnotations != null && node.visibleAnnotations.stream().anyMatch(el ->
							el.desc != null && el.desc.equals(Type.getDescriptor(Plugin.class))
					);
					if (hasAnnotation) {
						return entry.getName().substring(0, entry.getName().length() - ".class".length()).replace('/', '.');
					}
				}
			}
		} catch (final IOException e) {
			PluginManager.LOGGER.warn("Could not read plugin " + plugin.getAbsolutePath(), e);
		}
		return null;
	}
}