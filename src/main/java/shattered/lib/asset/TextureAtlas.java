package shattered.lib.asset;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import shattered.lib.math.Rectangle;

public interface TextureAtlas {
	@NotNull
	AtlasStitcher getAtlas();

	@Nullable
	Rectangle getRealUv();
}