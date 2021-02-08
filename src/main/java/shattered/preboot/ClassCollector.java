package shattered.preboot;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.function.Consumer;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;

final class ClassCollector {

	private ClassCollector() {
	}

	public static HashMap<String, byte[]> collectClasses(final URL[] urls) {
		try {
			final HashMap<String, byte[]> classes = new HashMap<>();
			for (final URL url : urls) {
				final File entry = new File(url.toURI());
				classes.putAll(ClassCollector.collectClasses(entry));
			}
			return classes;
		} catch (final IOException | URISyntaxException e) {
			throw new RuntimeException(e);
		}
	}

	private static Map<String, byte[]> collectClasses(final File file) throws IOException {
		final IClassCollector collector;
		if (file.isDirectory()) {
			collector = new CollectorDirectory(file);
		} else if (file.getAbsolutePath().endsWith(".jar")) {
			collector = new CollectorJar(new JarFile(file));
		} else {
			return Collections.emptyMap();
		}
		return collector.collect();
	}

	private static abstract class IClassCollector {

		protected abstract List<String> getEntries();

		protected abstract byte[] getBytes(String path) throws IOException;

		protected abstract String getName(String path);

		public static boolean validateClassName(final String name) {
			return !Arrays.asList(
					"Main",
					"package-info",
					"module-info"
			).contains(name);
		}

		final Map<String, byte[]> collect() throws IOException {
			final HashMap<String, byte[]> result = new HashMap<>();
			for (final String path : this.getEntries()) {
				final String name = this.getName(path);
				if (IClassCollector.validateClassName(name)) {
					result.put(name, this.getBytes(path));
				}
			}
			return result;
		}
	}

	private static final class CollectorDirectory extends IClassCollector {

		private final File directory;

		CollectorDirectory(final File directory) {
			this.directory = directory;
		}


		@Override
		protected List<String> getEntries() {
			final ArrayList<File> result = new ArrayList<>();
			CollectorDirectory.collectFiles(CollectorDirectory.createDirIterator(this.directory), result);
			return result.stream().map(File::getAbsolutePath).collect(Collectors.toList());
		}

		@Override
		protected byte[] getBytes(final String path) throws IOException {
			return Files.readAllBytes(new File(path).toPath());
		}

		@Override
		protected String getName(final String path) {
			return path
					.substring(this.directory.getAbsolutePath().length() + 1, path.length() - ".class".length())
					.replace(File.separatorChar, '.');
		}

		private static DirectoryIterator createDirIterator(final File directory) {
			return new DirectoryIterator(directory, dir -> dir.isDirectory() || dir.getAbsolutePath().endsWith(".class"));
		}

		private static void collectFiles(final DirectoryIterator iterator, final List<File> collection) {
			while (iterator.hasNext()) {
				final File child = iterator.next();
				if (child.isDirectory()) {
					CollectorDirectory.collectFiles(CollectorDirectory.createDirIterator(child), collection);
					continue;
				}
				collection.add(child);
			}
		}

		private static final class DirectoryIterator implements Iterator<File> {

			private final File[] children;
			private       int    index = -1;

			public DirectoryIterator(final File directory, final FileFilter childFilter) {
				if (!directory.isDirectory()) {
					throw new IllegalArgumentException("File " + directory + " is not a directory!");
				}
				if (!directory.canRead() || !directory.canExecute()) {
					throw new RuntimeException("Cannot read directory " + directory);
				}
				this.children = directory.listFiles(childFilter);
			}

			@Override
			public boolean hasNext() {
				final int index = this.index + 1;
				return index >= 0 && index < this.children.length;
			}

			@Override
			public File next() {
				if (!this.hasNext()) {
					throw new NoSuchElementException();
				}
				return this.children[++this.index];
			}

			@Override
			public void forEachRemaining(final Consumer<? super File> action) {
				while (this.hasNext()) {
					action.accept(this.next());
				}
			}
		}
	}

	private static final class CollectorJar extends IClassCollector {

		private final JarFile jar;

		CollectorJar(final JarFile jar) {
			this.jar = jar;
		}

		@Override
		protected List<String> getEntries() {
			final ArrayList<String> result = new ArrayList<>();
			for (final Enumeration<JarEntry> entries = this.jar.entries(); entries.hasMoreElements(); ) {
				final JarEntry entry = entries.nextElement();
				result.add(entry.getName());
			}
			return result;
		}

		@Override
		protected byte[] getBytes(final String path) throws IOException {
			final InputStream           input  = this.jar.getInputStream(this.jar.getJarEntry(path));
			final ByteArrayOutputStream array  = new ByteArrayOutputStream();
			int                         read;
			final byte[]                buffer = new byte[1024];
			while ((read = input.read(buffer)) != -1) {
				array.write(buffer, 0, read);
			}
			return array.toByteArray();
		}

		@Override
		protected String getName(final String path) {
			return path.substring(0, path.length() - ".class".length()).replace('/', '.');
		}
	}
}