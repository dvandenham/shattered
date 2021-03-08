package shattered.game.tile;

import java.util.HashMap;
import java.util.Map;
import com.google.gson.JsonSyntaxException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import shattered.BootMessageQueue;
import shattered.Shattered;
import shattered.game.entity.JsonEntityData;
import shattered.lib.ResourceLocation;
import shattered.lib.asset.AssetRegistry;
import shattered.lib.asset.InvalidResourceException;
import shattered.lib.asset.ScriptAsset;
import shattered.lib.registry.RegistryParser;

@RegistryParser.RegistryParserMetadata(Tile.class)
public final class TileDeserializer extends RegistryParser<Tile> {

	@Override
	@NotNull
	protected Class<?> getWrapperClass() {
		return JsonTileData.class;
	}

	@Override
	@NotNull
	protected Map<ResourceLocation, Tile> parse(@NotNull final ResourceLocation resource, @NotNull final Object data) {
		if (!(data instanceof JsonTileData)) {
			throw new ClassCastException(data.getClass().getName() + " cannot be cast to " + JsonEntityData.class.getName());
		}

		final JsonTileData jsonData = (JsonTileData) data;
		if (!jsonData.variants.contains(ResourceLocation.DEFAULT_VARIANT)) {
			throw new JsonSyntaxException(String.format("Tile %s is missing the \"%s\" variant!", resource, ResourceLocation.DEFAULT_VARIANT));
		}

		TileDeserializer.checkMapForVariant(resource, jsonData.textures, ResourceLocation.DEFAULT_VARIANT, "Texture");
		TileDeserializer.checkMapForVariant(resource, jsonData.updateScripts, ResourceLocation.DEFAULT_VARIANT, "Update-script");
		TileDeserializer.checkMapForVariant(resource, jsonData.renderScripts, ResourceLocation.DEFAULT_VARIANT, "Render-script");

		final HashMap<ResourceLocation, Tile> result = new HashMap<>();
		for (final String variant : jsonData.variants) {
			final ResourceLocation variantResource = resource.toVariant(variant);

			if (!jsonData.textures.containsKey(variant)) {
				Shattered.MESSAGES.addMessage(
						"tile_json_deserializer",
						"missing_textures_for_variant_" + variant,
						BootMessageQueue.BootMessage.Severity.INFO,
						"Tile \"" + resource + "\" is missing texture for variant \"" + variant + "\". Using \"" + ResourceLocation.DEFAULT_VARIANT + "\" variant texture."
				);
			}

			final Tile tile = new Tile(
					variantResource,
					jsonData.textures.getOrDefault(variant, jsonData.textures.get(ResourceLocation.DEFAULT_VARIANT)),
					jsonData.updateScripts != null ? jsonData.updateScripts.get(variant) : null,
					jsonData.renderScripts != null ? jsonData.renderScripts.get(variant) : null
			);
			if (tile.getUpdateScript() != null && !(AssetRegistry.getAsset(tile.getUpdateScript()) instanceof ScriptAsset)) {
				throw new InvalidResourceException(variantResource, "update script", tile.getUpdateScript());
			}
			if (tile.getRenderScript() != null && !(AssetRegistry.getAsset(tile.getRenderScript()) instanceof ScriptAsset)) {
				throw new InvalidResourceException(variantResource, "render script", tile.getResource());
			}
			result.put(variantResource, tile);
		}
		return result;
	}

	private static void checkMapForVariant(@NotNull final ResourceLocation resource,
	                                       @Nullable final Map<String, ?> map,
	                                       @NotNull final String variant,
	                                       @NotNull final String errorString) {
		if (map != null && !map.containsKey(variant)) {
			throw new JsonSyntaxException(String.format(
					"%s mapping of tile %s is missing the \"%s\" variant!", errorString, resource, ResourceLocation.DEFAULT_VARIANT
			));
		}
	}
}