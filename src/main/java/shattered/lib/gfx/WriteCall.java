package shattered.lib.gfx;

import org.jetbrains.annotations.NotNull;

final class WriteCall {

	@NotNull
	public final StringData data;
	public final int x;
	public final int y;

	public WriteCall(final int x, final int y, @NotNull final StringData data) {
		this.data = data;
		this.x = x;
		this.y = y;
	}
}