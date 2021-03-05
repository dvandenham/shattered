package shattered.lib.asset;

import it.unimi.dsi.fastutil.chars.Char2IntArrayMap;
import it.unimi.dsi.fastutil.ints.Int2IntArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import shattered.lib.ResourceLocation;
import shattered.lib.math.Rectangle;

public final class Font extends IAsset {

	private static final Int2IntArrayMap SIZE_CACHE = new Int2IntArrayMap();
	public static final int[] DEFAULT_SIZES = {16, 24, 32, 48, 64, 128};
	public static final int DEFAULT_SIZE_INDEX = 3;

	static {
		Font.SIZE_CACHE.defaultReturnValue(-1);
		for (int i = 0; i < Font.DEFAULT_SIZES.length; ++i) {
			Font.SIZE_CACHE.put(Font.DEFAULT_SIZES[i], i);
		}
	}

	final Char2IntArrayMap[] charMaps = new Char2IntArrayMap[Font.DEFAULT_SIZES.length];
	final Int2ObjectArrayMap<Texture> charTextures = new Int2ObjectArrayMap<>();

	final AtlasStitcher atlas;

	Font(@NotNull final ResourceLocation resource, @NotNull final AtlasStitcher atlas) {
		super(resource);
		this.atlas = atlas;
		for (int i = 0; i < this.charMaps.length; ++i) {
			this.charMaps[i] = new Char2IntArrayMap();
		}
	}

	@Nullable
	public Rectangle getUv(final int fontSize, final char character) {
		final Char2IntArrayMap map = this.getCorrectCharMap(fontSize);
		return map.containsKey(character) ? this.atlas.getRealUv(map.get(character)) : Rectangle.EMPTY;
	}

	@Nullable
	public Texture getCharTexture(final int fontSize, final char character) {
		final Char2IntArrayMap map = this.getCorrectCharMap(fontSize);
		return map.containsKey(character) ? this.charTextures.get(map.get(character)) : null;
	}

	@NotNull
	private Char2IntArrayMap getCorrectCharMap(final int fontSize) {
		final int sizeIndex = Font.getSizeIndexForSize(fontSize);
		return this.charMaps[sizeIndex];
	}

	private static int getSizeIndexForSize(final int fontSize) {
		final int result = Font.SIZE_CACHE.get(fontSize);
		if (result != -1) {
			return result;
		} else {
			for (int i = 0; i < Font.DEFAULT_SIZES.length - 1; ++i) {
				final int size = Font.DEFAULT_SIZES[i];
				if (fontSize <= size) {
					Font.SIZE_CACHE.put(fontSize, i);
					return i;
				}
			}
			//Return largest font if there was no match before
			Font.SIZE_CACHE.put(fontSize, Font.DEFAULT_SIZES.length - 1);
			return Font.DEFAULT_SIZES.length - 1;
		}
	}

	public static int getPhysicalSizeForLogicalSize(final int logicalSize) {
		final int index = Font.getSizeIndexForSize(logicalSize);
		return Font.DEFAULT_SIZES[index];
	}
}