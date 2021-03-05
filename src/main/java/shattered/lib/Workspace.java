package shattered.lib;

import java.io.File;
import java.util.Locale;
import org.jetbrains.annotations.NotNull;

public final class Workspace {

	@NotNull
	private final File rootDir;
	@NotNull
	private final File dataDir;
	@NotNull
	private final File tempDir;
	@NotNull
	private final File logsDir;
	@NotNull
	private final File debugDir;

	private Workspace(final String name) {
		this.rootDir = Workspace.createRootDir(name);
		if (!this.rootDir.exists() && !this.rootDir.mkdirs()) {
			throw new RuntimeException("Could not create workspace root directory! (path: " + this.rootDir.getAbsolutePath() + ")");
		}
		this.dataDir = this.createDir(this.rootDir, "data");
		this.tempDir = this.createDir(this.rootDir, "temp");
		this.logsDir = this.createDir(this.tempDir, "logs");
		this.debugDir = new File(this.rootDir, "debug");
		if (this.debugDir.exists()) {
			//Delete debug directory on boot
			//noinspection ResultOfMethodCallIgnored
			this.debugDir.delete();
		}
	}

	@NotNull
	public File getRootDir() {
		return this.rootDir;
	}

	@NotNull
	public File getDataFile(@NotNull final String name) {
		return new File(this.dataDir, name);
	}

	@NotNull
	public File getTempFile(@NotNull final String name) {
		return this.getTempFile(name, true);
	}

	@NotNull
	public File getTempFile(@NotNull final String name, final boolean deleteOnExit) {
		final File result = new File(this.tempDir, name);
		if (deleteOnExit) {
			result.deleteOnExit();
		}
		return result;
	}

	@NotNull
	public File getDataDir() {
		return this.dataDir;
	}

	@NotNull
	public File getTempDir() {
		return this.tempDir;
	}

	@NotNull
	public File getLogsDir() {
		return this.logsDir;
	}

	@NotNull
	public File getDebugDir(@NotNull final String name) {
		final File result = new File(this.debugDir, name);
		if (!result.exists() && !result.mkdirs()) {
			throw new RuntimeException("Could not create workspace sub-directory! (path: " + result.getAbsolutePath() + ")");
		}
		return result;
	}

	public void openExternal() {
		try {
			java.awt.Desktop.getDesktop().open(this.rootDir);
		} catch (final Throwable ignored) {
		}
	}

	private File createDir(@NotNull final File parent, final String name) {
		final File result = new File(parent, name);
		if (!result.exists() && !result.mkdirs()) {
			throw new RuntimeException("Could not create workspace sub-directory! (path: " + result.getAbsolutePath() + ")");
		}
		return result;
	}

	private static File createRootDir(final String name) {
		final String osName = System.getProperty("os.name").toLowerCase(Locale.ROOT);
		if (osName.contains("win")) { //Windows
			final String appData = System.getenv("APPDATA");
			final String path = appData != null ? appData + File.separator + name : System.getProperty("user.home") + File.separator + "." + name;
			return new File(path);
		} else if (osName.contains("mac") || osName.contains("darwin")) { //macOS
			final String path = System.getProperty("user.home") + File.separator + "Library" + File.separator + "Application Data" + File.separator + name;
			return new File(path);
		} else if (osName.contains("nux")) { //Linux
			final String path = System.getProperty("user.home") + File.separator + ".local" + File.separator + "share" + File.separator + name;
			return new File(path);
		} else {
			return new File(System.getProperty("user.home"), "." + name);
		}
	}
}