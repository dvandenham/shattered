package shattered.game.world;

import com.google.gson.annotations.SerializedName;
import shattered.lib.ResourceLocation;
import shattered.lib.json.Json;
import shattered.lib.math.Dimension;
import shattered.lib.math.Point;

@Json
public final class WorldType {

	@SerializedName("world_size")
	@Json.Required
	public Dimension worldSize;

	@SerializedName("wallpaper")
	@Json.Required
	public ResourceLocation wallpaperTexture;

	@SerializedName("player_position")
	@Json.Required
	public Point playerPos;

	private WorldType() {
	}
}