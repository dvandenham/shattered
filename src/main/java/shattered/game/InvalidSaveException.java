package shattered.game;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class InvalidSaveException extends Exception {

	private static final long serialVersionUID = 1253659096420229832L;

	public enum InvalidSaveReason {
		METADATA_MISSING,
		METADATA_CORRUPT,
		WORLD_MISSING,
		WORLD_CORRUPT,
	}

	@NotNull
	private final InvalidSaveReason reason;
	@Nullable
	private final String detail;

	InvalidSaveException(@NotNull final InvalidSaveReason reason, @Nullable final String detail) {
		this.reason = reason;
		this.detail = detail;
	}

	@NotNull
	public InvalidSaveReason getReason() {
		return this.reason;
	}

	@Nullable
	public String getDetail() {
		return this.detail;
	}
}