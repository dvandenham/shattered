package shattered.lib.json;

import java.io.IOException;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import shattered.lib.ResourceLocation;

final class ResourceLocationTypeAdapter extends TypeAdapter<ResourceLocation> {

	@Override
	public void write(final JsonWriter out, final ResourceLocation value) throws IOException {
		out.value(value.toString());
	}

	@Override
	public ResourceLocation read(final JsonReader in) throws IOException {
		return new ResourceLocation(in.nextString());
	}
}
