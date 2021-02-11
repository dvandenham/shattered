package shattered.lib.gfx;

import java.util.Arrays;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import shattered.lib.Color;
import shattered.lib.asset.Texture;
import shattered.lib.math.Rectangle;

final class DrawCall {

	public enum CallType {
		MATRIX_PUSH,
		MATRIX_POP,
		TRANSLATE,
		ROTATE,
		SMOOTH
	}

	private static final int DATA_SIZE = 6;
	public final Color[] colors = new Color[]{Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE};
	public Rectangle bounds;
	public Texture texture = null;
	public boolean useTexture = false;
	public int uMin, vMin, uMax, vMax;
	public Object[][] data = new Object[DrawCall.DATA_SIZE][];

	public DrawCall(@NotNull final Rectangle bounds, @Nullable final Texture texture) {
		this.bounds = bounds;
		this.texture = texture;
		this.useTexture = true;
		final Rectangle uv = this.texture != null ? this.texture.getUv() : null;
		this.uMin = uv != null ? uv.getX() : -1;
		this.vMin = uv != null ? uv.getY() : -1;
		this.uMax = uv != null ? uv.getMaxX() : -1;
		this.vMax = uv != null ? uv.getMaxY() : -1;
	}

	public DrawCall(@NotNull final Rectangle bounds, @NotNull final Color color) {
		this.bounds = bounds;
		Arrays.fill(this.colors, color);
	}

	public void action(@NotNull final CallType type, @NotNull final Object... data) {
		if (this.data[this.data.length - 1] != null) {
			final Object[][] newArray = new Object[this.data.length + DrawCall.DATA_SIZE][];
			System.arraycopy(this.data, 0, newArray, 0, this.data.length);
			this.data = newArray;
		}
		for (int i = 0; i < this.data.length; ++i) {
			if (this.data[i] == null) {
				final Object[] entry = new Object[data.length + 1];
				entry[0] = type;
				System.arraycopy(data, 0, entry, 1, data.length);
				this.data[i] = entry;
				return;
			}
		}
	}
}