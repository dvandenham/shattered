package shattered.lib.asset;

import java.util.HashMap;
import com.google.gson.annotations.SerializedName;
import shattered.lib.ResourceLocation;
import shattered.lib.json.Json;

@Json
public final class JsonTextureData {

	@SerializedName("type")
	@Json.TypeControllerEnum
	@Json.Required
	TextureType textureType;

	@SerializedName("variants")
	HashMap<String, ResourceLocation> variants;

	/* STITCHED */
	@SerializedName("usable_width")
	@Json.TypeValue(type = "stitched")
	Integer stitchedUsableWidth;

	@SerializedName("sprite_size")
	@Json.TypeValue(type = "stitched")
	@Json.Required(group = @Json.Required.OR(groupName = "sprite_size", groupIndex = "1"))
	Integer stitchedSpriteSize;

	@SerializedName("sprite_width")
	@Json.TypeValue(type = "stitched")
	@Json.Required(group = @Json.Required.OR(groupName = "sprite_size", groupIndex = "2"))
	Integer stitchedSpriteWidth;

	@SerializedName("sprite_height")
	@Json.TypeValue(type = "stitched")
	@Json.Required(group = @Json.Required.OR(groupName = "sprite_size", groupIndex = "2"))
	Integer stitchedSpriteHeight;

	@SerializedName("sprite_count")
	@Json.TypeValue(type = "stitched")
	@Json.Required
	Integer stitchedSpriteCount;

	/* MAPPED */
	@SerializedName("mapping")
	@Json.TypeValue(type = "mapped")
	@Json.Required
	HashMap<String, Integer[]> mappedMapping;

	/* ANIMATIONS */
	@SerializedName("fps")
	@Json.TypeValue(type = "animated")
	@Json.Required
	Double animationFps;


	@SerializedName("frame_mapping")
	@Json.TypeValue(type = "animated")
	Integer[] animationFrameMapping;
}