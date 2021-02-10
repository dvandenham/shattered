package shattered.lib;

import java.util.Arrays;
import org.jetbrains.annotations.Nullable;

public final class StringUtils {

	private static final int[] RANGE_LOWERCASE = {'a', 'z'};
	private static final int[] RANGE_NUMERIC = {'0', '9'};
	private static final int[] CHARS_SEPARATOR = {'.', '-', '_', '/'};

	public static boolean isAlphaString(@Nullable final String str) {
		final int length;
		if (str == null || (length = str.length()) == 0) {
			return false;
		}
		for (int i = 0; i < length; ++i) {
			final char character = str.charAt(i);
			if (character >= StringUtils.RANGE_LOWERCASE[0] && character <= StringUtils.RANGE_LOWERCASE[1]) {
				continue;
			}
			return false;
		}
		return true;
	}

	public static boolean isResourceString(@Nullable final String str) {
		final int length;
		if (str == null || (length = str.length()) == 0) {
			return false;
		}
		for (int i = 0; i < length; ++i) {
			final char character = str.charAt(i);
			if (character >= StringUtils.RANGE_LOWERCASE[0] && character <= StringUtils.RANGE_LOWERCASE[1]) {
				continue;
			}
			if (character >= StringUtils.RANGE_NUMERIC[0] && character <= StringUtils.RANGE_NUMERIC[1]) {
				continue;
			}
			if (Arrays.stream(StringUtils.CHARS_SEPARATOR).noneMatch(ch -> ch == character)) {
				return false;
			}
		}
		return true;
	}

	private StringUtils() {
	}
}