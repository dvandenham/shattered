package shattered.game.world;

import org.jetbrains.annotations.NotNull;
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
	protected WorldType parse(@NotNull final Object data) {
		if (!(data instanceof WorldType)) {
			throw new ClassCastException(data.getClass().getName() + " cannot be cast to " + WorldType.class.getName());
		}
		return (WorldType) data;
	}
}
