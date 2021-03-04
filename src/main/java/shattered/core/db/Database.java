package shattered.core.db;

import java.io.File;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import shattered.core.event.EventBusSubscriber;
import shattered.core.event.MessageEvent;
import shattered.core.event.MessageListener;
import shattered.Shattered;
import shattered.lib.ResourceLocation;

@EventBusSubscriber(Shattered.SYSTEM_BUS_NAME)
public final class Database {

	static final Logger LOGGER = LogManager.getLogger("Database");
	@NotNull
	private final File rootDir;

	private Database(@NotNull final File rootDir) {
		this.rootDir = rootDir;
		if (!this.rootDir.exists() && !this.rootDir.mkdirs()) {
			Shattered.crash("Could not create database store-directory!");
		}
	}

	@Nullable
	public File requestDataFile(@NotNull final ResourceLocation resource) {
		final File namespaceDir = this.getNamespaceDir(resource.getNamespace());
		String filename = resource.getResource().replace(".", "/");
		String subDirs = null;
		if (filename.contains("/")) {
			subDirs = filename.substring(0, filename.lastIndexOf('/'));
			filename = filename.substring(filename.lastIndexOf('/') + 1);
		}
		if (subDirs == null) {
			return new File(namespaceDir, filename + ".dat");
		} else {
			final File parentDir = new File(namespaceDir, subDirs);
			if (!parentDir.exists() && !parentDir.mkdirs()) {
				Database.LOGGER.warn("Could not create directory: {}", parentDir.getAbsolutePath());
				return null;
			}
			return new File(parentDir, filename + ".dat");
		}
	}

	@NotNull
	private File getNamespaceDir(@NotNull final String namespace) {
		final File result = new File(this.rootDir, namespace);
		if (!result.exists() && !result.mkdirs()) {
			Database.LOGGER.warn("Could not create database store for namespace \"{}\"", namespace);
		}
		return result;
	}

	@MessageListener("init_database")
	private static void onInitDatabase(final MessageEvent event) {
		event.setResponse(() -> new Database((File) event.getData()[0]));
	}
}