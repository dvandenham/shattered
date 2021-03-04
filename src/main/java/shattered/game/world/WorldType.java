package shattered.game.world;

import com.google.gson.annotations.SerializedName;
import shattered.lib.ResourceLocation;
import shattered.lib.json.Json;
import shattered.lib.math.Dimension;

@Json
public final class WorldType {

	@SerializedName("world_size")
	@Json.Required
	public Dimension worldSize;

	@SerializedName("wallpaper")
	@Json.Required
	public ResourceLocation wallpaperTexture;

	private WorldType() {
	}
}