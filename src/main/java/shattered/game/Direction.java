package shattered.game;

import java.util.Locale;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public enum Direction {

	UP,
	RIGHT,
	DOWN,
	LEFT;

	private static final Direction[] VALUES = Direction.values();
	private final String identifier;

	Direction() {
		this.identifier = super.toString().toLowerCase(Locale.ROOT);
	}

	@Override
	public String toString() {
		return this.identifier;
	}

	@Nullable
	public static Direction getByIdentifier(@NotNull final String identifier) {
		for (final Direction action : Direction.VALUES) {
			if (action.toString().equals(identifier)) {
				return action;
			}
		}
		return null;
	}
}