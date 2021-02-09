package shattered.core.event;

import org.jetbrains.annotations.Nullable;

public abstract class Event<T> {

	private final T       data;
	private       boolean cancelled = false;

	public Event() {
		this.data = null;
	}

	public Event(@Nullable final T data) {
		this.data = data;
	}

	public final void cancel() {
		if (this.isCancellable()) {
			this.cancelled = true;
		}
	}

	public boolean isCancellable() {
		return false;
	}

	public final boolean isCancelled() {
		return this.cancelled;
	}

	@Nullable
	public final T get() {
		return this.data;
	}
}