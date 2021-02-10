package shattered.lib.asset;

import it.unimi.dsi.fastutil.ints.Int2IntArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.IntIterator;
import org.jetbrains.annotations.NotNull;
import shattered.lib.ResourceLocation;

public final class FontGroup extends IAsset {

	public static final int[]                    DEFAULT_SIZES      = {16, 24, 32, 48, 64, 128};
	public static final int                      DEFAULT_SIZE_INDEX = 3;
	private final       Int2IntArrayMap          cache              = new Int2IntArrayMap();
	final               java.awt.Font            baseFont;
	final               Int2ObjectArrayMap<Font> fonts              = new Int2ObjectArrayMap<>();
	private             int                      largestFontSize    = 0;

	FontGroup(@NotNull final ResourceLocation Resource, @NotNull final java.awt.Font baseFont) {
		super(Resource);
		this.cache.defaultReturnValue(-1);
		this.baseFont = baseFont;
	}

	void destroy() {
		this.fonts.values().forEach(font -> font.atlas.reset());
	}

	public void addSize(final int size) {
		if (size <= 0 || this.fonts.containsKey(size)) {
			return;
		}
		final Font result = FontLoader.create(this.baseFont, size);
		this.fonts.put(size, result);
		this.cache.put(size, size);
		if (size > this.largestFontSize) {
			this.largestFontSize = size;
		}
	}

	@NotNull
	public Font getFont(final int size) {
		final int sizeCached = this.cache.get(size);
		if (sizeCached != -1) {
			return this.fonts.get(sizeCached);
		}
		int               sizeChosen = -1;
		final IntIterator iterator   = this.cache.keySet().iterator();
		int               distance   = -1;
		while (iterator.hasNext()) {
			final int sizeCurrent = iterator.nextInt();
			if (sizeCurrent < size) {
				continue;
			}
			final int newDistance = Math.abs(sizeCurrent - size);
			if (newDistance < distance) {
				distance = newDistance;
				sizeChosen = sizeCurrent;
			}
		}
		if (sizeChosen == -1) {
			sizeChosen = this.largestFontSize;
		}
		this.cache.put(size, sizeChosen);
		return this.fonts.get(sizeChosen);
	}
}