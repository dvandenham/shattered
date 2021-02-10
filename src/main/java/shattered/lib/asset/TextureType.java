package shattered.lib.asset;

import java.util.Locale;

public enum TextureType {

	DEFAULT,
	STITCHED,
	MAPPED,
	ANIMATED;

	private final String name = super.toString().toLowerCase(Locale.ROOT);

	@Override
	public String toString() {
		return this.name;
	}
}