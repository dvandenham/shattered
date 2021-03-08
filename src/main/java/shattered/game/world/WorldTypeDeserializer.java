package shattered.game.world;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import shattered.BootMessageQueue;
import shattered.Shattered;
import shattered.game.GameRegistries;
import shattered.game.tile.Tile;
import shattered.lib.FastNamedObjectMap;
import shattered.lib.ResourceLocation;
import shattered.lib.math.Dimension;
import shattered.lib.registry.RegistryParser;
import com.google.gson.JsonSyntaxException;
import org.jetbrains.annotations.NotNull;

@RegistryParser.RegistryParserMetadata(WorldType.class)
public final class WorldTypeDeserializer extends RegistryParser<WorldType> {

	@Override
	@NotNull
	protected Class<?> getWrapperClass() {
		return JsonWorldData.class;
	}

	@Override
	@NotNull
	protected Map<ResourceLocation, WorldType> parse(@NotNull final ResourceLocation resource, @NotNull final Object data) {
		if (!(data instanceof JsonWorldData)) {
			throw new ClassCastException(data.getClass().getName() + " cannot be cast to " + WorldType.class.getName());
		}

		final JsonWorldData jsonData = (JsonWorldData) data;

		if (jsonData.displayName.trim().isEmpty()) {
			return new HashMap<>();
		}

		try {
			final HashMap<ResourceLocation, WorldType> result = new HashMap<>();
			result.put(resource, new WorldType(
					resource,
					jsonData.displayName,
					jsonData.wallpaperTexture,
					jsonData.playerPos,
					WorldTypeDeserializer.parseStructure(resource, jsonData)
			));
			return result;
		} catch (final JsonSyntaxException ignored) {
			return new HashMap<>();
		}
	}

	private static Structure parseStructure(@NotNull final ResourceLocation resource, @NotNull final JsonWorldData data) {
		final FastNamedObjectMap<Tile> references = new FastNamedObjectMap<>();
		for (final Map.Entry<String, ResourceLocation> entry : data.tiles.entrySet()) {
			if (entry.getKey().equals("0")) {
				Shattered.MESSAGES.addMessage(
						"world_type_json_deserializer_" + resource,
						"zero_key_definition",
						BootMessageQueue.BootMessage.Severity.WARNING,
						"World \"" + resource + "\" tile mapping contains invalid key: 0"
				);
				throw new JsonSyntaxException("");
			}
			final Tile tile = GameRegistries.TILE().get(entry.getValue());
			if (tile == null) {
				Shattered.MESSAGES.addMessage(
						"world_type_json_deserializer_" + resource,
						"unknown_key_definition",
						BootMessageQueue.BootMessage.Severity.WARNING,
						"World \"" + resource + "\" tile mapping contains unknown tile reference: " + entry.getValue()
				);
				throw new JsonSyntaxException("");
			}
			references.put(entry.getKey(), tile);
		}
		final Tile[][] structure = new Tile[data.structure.length][];
		for (int y = 0; y < structure.length; ++y) {
			structure[y] = new Tile[data.structure[y].length];
			for (int x = 0; x < structure[y].length; ++x) {
				final String key = data.structure[y][x];
				if (key == null || key.equals("0")) {
					continue;
				}
				final Tile tile = references.get(key);
				if (tile == null) {
					Shattered.MESSAGES.addMessage(
							"world_type_json_deserializer_" + resource,
							"unmapped_structure_key",
							BootMessageQueue.BootMessage.Severity.WARNING,
							"World \"" + resource + "\" structure contains unmapped key: " + key
					);
					throw new JsonSyntaxException("");
				}
				structure[y][x] = tile;
			}
		}
		final Dimension worldSize = Dimension.create(
				Arrays.stream(structure).mapToInt(tiles -> tiles.length).max().orElse(0),
				structure.length
		);
		return new Structure(structure, worldSize);
	}
}