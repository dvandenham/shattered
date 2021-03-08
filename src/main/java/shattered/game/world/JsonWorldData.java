package shattered.game.world;

import java.util.HashMap;
import shattered.lib.ResourceLocation;
import shattered.lib.json.Json;
import shattered.lib.math.Point;
import com.google.gson.annotations.SerializedName;

@Json
public final class JsonWorldData {

	@SerializedName("display_name")
	@Json.Required
	public String displayName;

	@SerializedName("wallpaper")
	@Json.Required
	public ResourceLocation wallpaperTexture;

	@SerializedName("player_position")
	@Json.Required
	public Point playerPos;

	@SerializedName("tiles")
	@Json.Required
	public HashMap<String, ResourceLocation> tiles;

	@SerializedName("structure")
	@Json.Required
	public String[][] structure;

	private JsonWorldData() {
	}
}