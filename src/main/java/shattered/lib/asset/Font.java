package shattered.lib.asset;

import java.util.concurrent.ConcurrentHashMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import shattered.lib.math.Rectangle;

public final class Font {

	final ConcurrentHashMap<Character, Integer> charMap = new ConcurrentHashMap<>();
	final ConcurrentHashMap<Integer, TextureAtlasDefault> charTextures = new ConcurrentHashMap<>();
	private final int size;
	final AtlasStitcher atlas;

	Font(@NotNull final AtlasStitcher atlas, final int size) {
		this.atlas = atlas;
		this.size = size;
	}

	@Nullable
	public Rectangle getUv(final char character) {
		return this.charMap.containsKey(character) ? this.atlas.getRealUv(this.charMap.get(character)) : Rectangle.EMPTY;
	}

	@Nullable
	public Texture getCharTexture(final char character) {
		return this.charMap.containsKey(character) ? this.charTextures.get(this.charMap.get(character)) : null;
	}

	public int getSize() {
		return this.size;
	}
}