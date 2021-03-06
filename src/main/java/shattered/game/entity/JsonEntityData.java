package shattered.game.entity;

import java.util.HashMap;
import java.util.List;
import com.google.gson.annotations.SerializedName;
import shattered.lib.ResourceLocation;
import shattered.lib.json.Json;
import shattered.lib.math.Dimension;

@Json
public final class JsonEntityData {

	@SerializedName("variants")
	@Json.Required
	public List<String> variants;

	@SerializedName("textures")
	@Json.Required
	public HashMap<String, ResourceLocation> textures;

	@SerializedName("entity_sizes")
	@Json.Required
	public HashMap<String, Dimension> entitySizes;

	@SerializedName("update_scripts")
	@Json.Required
	public HashMap<String, ResourceLocation> updateScripts;

	@SerializedName("render_scripts")
	@Json.Required
	public HashMap<String, ResourceLocation> renderScripts;

	@SerializedName("attributes")
	@Json.Required
	public HashMap<String, HashMap<String, Object>> attributes;

	private JsonEntityData() {
	}
}