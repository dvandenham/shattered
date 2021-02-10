package shattered.lib;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import shattered.Shattered;

public final class FileUtils {

	private FileUtils() {
	}

	@Nullable
	public static String streamToString(@NotNull final InputStream stream) {
		try (final BufferedReader reader = new BufferedReader(new InputStreamReader(stream))) {
			final StringBuilder builder = new StringBuilder();
			String line;
			while ((line = reader.readLine()) != null) {
				builder.append(line).append('\n');
			}
			return builder.toString();
		} catch (final IOException e) {
			Shattered.LOGGER.error(e);
			return null;
		}
	}
}
