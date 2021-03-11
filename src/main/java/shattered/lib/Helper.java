package shattered.lib;

import java.util.function.Predicate;

public final class Helper {

	private Helper() {
	}

	public static <T> Predicate<T> testTrue() {
		return ignored -> true;
	}
}