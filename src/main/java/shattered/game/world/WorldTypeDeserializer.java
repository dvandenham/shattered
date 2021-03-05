package shattered.game.world;

import java.util.HashMap;
import java.util.Map;
import org.jetbrains.annotations.NotNull;
import shattered.lib.ResourceLocation;
import shattered.lib.registry.RegistryParser;

@RegistryParser.RegistryParserMetadata(WorldType.class)
public final class WorldTypeDeserializer extends RegistryParser<WorldType> {

	@Override
	@NotNull
	protected Class<?> getWrapperClass() {
		return WorldType.class;
	}

	@Override
	@NotNull
	protected Map<ResourceLocation, WorldType> parse(@NotNull final ResourceLocation resource, @NotNull final Object data) {
		if (!(data instanceof WorldType)) {
			throw new ClassCastException(data.getClass().getName() + " cannot be cast to " + WorldType.class.getName());
		}
		final HashMap<ResourceLocation, WorldType> result = new HashMap<>();
		result.put(resource, (WorldType) data);
		return result;
	}
}