package shattered.game;

import org.jetbrains.annotations.NotNull;

public final class InvalidSaveException extends Exception {

	private static final long serialVersionUID = 1253659096420229832L;

	public enum InvalidSaveReason {
		METADATA_MISSING,
		METADATA_CORRUPT,
		WORLD_MISSING,
		WORLD_CORRUPT,
	}

	private final InvalidSaveReason reason;

	InvalidSaveException(@NotNull final InvalidSaveReason reason) {
		this.reason = reason;
	}

	public InvalidSaveReason getReason() {
		return this.reason;
	}
}