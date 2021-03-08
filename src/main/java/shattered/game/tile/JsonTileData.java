package shattered.game.tile;

import java.util.HashMap;
import java.util.List;
import com.google.gson.annotations.SerializedName;
import shattered.lib.ResourceLocation;
import shattered.lib.json.Json;

@Json
public final class JsonTileData {

	@SerializedName("variants")
	@Json.Required
	public List<String> variants;

	@SerializedName("textures")
	@Json.Required
	public HashMap<String, ResourceLocation> textures;

	@SerializedName("update_scripts")
	public HashMap<String, ResourceLocation> updateScripts;

	@SerializedName("render_scripts")
	public HashMap<String, ResourceLocation> renderScripts;

	private JsonTileData() {
	}
}