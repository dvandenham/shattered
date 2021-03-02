package shattered.lib.event;

import java.util.function.Supplier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MessageEvent extends Event<String> {

	private final Object[] data;
	private Supplier<?> response = null;

	public MessageEvent(@NotNull final String message, final Object... data) {
		super(message);
		this.data = data;
	}

	public void setResponse(@NotNull final Supplier<?> response) {
		this.response = response;
	}

	@Nullable
	public Supplier<?> getResponse() {
		return this.response;
	}

	@NotNull
	public Object[] getData() {
		return this.data;
	}
}