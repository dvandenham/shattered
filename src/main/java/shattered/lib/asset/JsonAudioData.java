package shattered.lib.asset;

import com.google.gson.annotations.SerializedName;
import shattered.lib.json.Json;

@Json
final class JsonAudioData {

	@SerializedName("type")
	@Json.Required
	AudioLoader.AudioType audioType;
}