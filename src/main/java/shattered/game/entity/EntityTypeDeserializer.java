package shattered.game.entity;

import java.util.HashMap;
import java.util.Map;
import com.google.gson.JsonSyntaxException;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.jetbrains.annotations.NotNull;
import shattered.BootMessageQueue;
import shattered.Shattered;
import shattered.lib.ResourceLocation;
import shattered.lib.registry.RegistryParser;

@RegistryParser.RegistryParserMetadata(EntityType.class)
public final class EntityTypeDeserializer extends RegistryParser<EntityType> {

	@Override
	@NotNull
	protected Class<?> getWrapperClass() {
		return JsonEntityData.class;
	}

	@Override
	@NotNull
	protected Map<ResourceLocation, EntityType> parse(@NotNull final ResourceLocation resource, @NotNull final Object data) {
		if (!(data instanceof JsonEntityData)) {
			throw new ClassCastException(data.getClass().getName() + " cannot be cast to " + JsonEntityData.class.getName());
		}


		final JsonEntityData jsonData = (JsonEntityData) data;
		if (!jsonData.variants.contains(ResourceLocation.DEFAULT_VARIANT)) {
			throw new JsonSyntaxException(String.format("Entity %s is missing the \"%s\" variant!", resource, ResourceLocation.DEFAULT_VARIANT));
		}

		EntityTypeDeserializer.checkMapForVariant(resource, jsonData.textures, ResourceLocation.DEFAULT_VARIANT, "Texture");
		EntityTypeDeserializer.checkMapForVariant(resource, jsonData.entitySizes, ResourceLocation.DEFAULT_VARIANT, "Entity-size");
		EntityTypeDeserializer.checkMapForVariant(resource, jsonData.updateScripts, ResourceLocation.DEFAULT_VARIANT, "Update-script");
		EntityTypeDeserializer.checkMapForVariant(resource, jsonData.renderScripts, ResourceLocation.DEFAULT_VARIANT, "Render-script");
		EntityTypeDeserializer.checkMapForVariant(resource, jsonData.attributes, ResourceLocation.DEFAULT_VARIANT, "Render-script");

		final HashMap<ResourceLocation, EntityType> result = new HashMap<>();
		for (final String variant : jsonData.variants) {
			final ResourceLocation variantResource = resource.toVariant(variant);

			if (!jsonData.textures.containsKey(variant)) {
				Shattered.MESSAGES.addMessage(
						"entity_type_json_deserializer",
						"missing_textures_for_variant_" + variant,
						BootMessageQueue.BootMessage.Severity.INFO,
						"Entity \"" + resource + "\" is missing texture for variant \"" + variant + "\". Using \"" + ResourceLocation.DEFAULT_VARIANT + "\" variant texture."
				);
			}
			if (!jsonData.entitySizes.containsKey(variant)) {
				Shattered.MESSAGES.addMessage(
						"entity_type_json_deserializer",
						"missing_entity_size_for_variant_" + variant,
						BootMessageQueue.BootMessage.Severity.INFO,
						"Entity \"" + resource + "\" is missing size mapping for variant \"" + variant + "\". Using \"" + ResourceLocation.DEFAULT_VARIANT + "\" variant entity size."
				);
			}
			if (!jsonData.updateScripts.containsKey(variant)) {
				Shattered.MESSAGES.addMessage(
						"entity_type_json_deserializer",
						"missing_update_script_for_variant_" + variant,
						BootMessageQueue.BootMessage.Severity.INFO,
						"Entity \"" + resource + "\" is missing update script mapping for variant \"" + variant + "\". Using \"" + ResourceLocation.DEFAULT_VARIANT + "\" variant update script."
				);
			}
			if (!jsonData.renderScripts.containsKey(variant)) {
				Shattered.MESSAGES.addMessage(
						"entity_type_json_deserializer",
						"missing_render_script_for_variant_" + variant,
						BootMessageQueue.BootMessage.Severity.INFO,
						"Entity \"" + resource + "\" is missing render script mapping for variant \"" + variant + "\". Using \"" + ResourceLocation.DEFAULT_VARIANT + "\" variant render script."
				);
			}
			if (!jsonData.attributes.containsKey(variant)) {
				Shattered.MESSAGES.addMessage(
						"entity_type_json_deserializer",
						"missing_attributes_for_variant_" + variant,
						BootMessageQueue.BootMessage.Severity.WARNING,
						"Entity \"" + resource + "\" is missing attribute mapping for variant \"" + variant + "\". Using \"" + ResourceLocation.DEFAULT_VARIANT + "\" variant attributes."
				);
			}

			final EntityAttributeContainer attributes = EntityAttributes.parseMap(
					jsonData.attributes.getOrDefault(variant, jsonData.attributes.get(ResourceLocation.DEFAULT_VARIANT))
			);
			if (!attributes.getRegisteredAttributes().containsAll(EntityAttributes.REQUIRED_ATTRIBUTES)) {
				final ObjectArrayList<EntityAttributes> missing = new ObjectArrayList<>(EntityAttributes.REQUIRED_ATTRIBUTES);
				missing.removeAll(attributes.getRegisteredAttributes());
				throw new JsonSyntaxException(String.format(
						"Attribute mapping of entity %s (variant: %s) is missing required attributes: %s",
						resource,
						variant,
						missing
				));
			}

			final EntityType entityType = new EntityType(
					variantResource,
					jsonData.textures.getOrDefault(variant, jsonData.textures.get(ResourceLocation.DEFAULT_VARIANT)),
					jsonData.entitySizes.getOrDefault(variant, jsonData.entitySizes.get(ResourceLocation.DEFAULT_VARIANT)),
					jsonData.updateScripts.getOrDefault(variant, jsonData.updateScripts.get(ResourceLocation.DEFAULT_VARIANT)),
					jsonData.renderScripts.getOrDefault(variant, jsonData.renderScripts.get(ResourceLocation.DEFAULT_VARIANT)),
					attributes
			);
			result.put(variantResource, entityType);
		}
		return result;
	}

	private static void checkMapForVariant(@NotNull final ResourceLocation resource,
	                                       @NotNull final Map<String, ?> map,
	                                       @NotNull final String variant,
	                                       @NotNull final String errorString) {
		if (!map.containsKey(variant)) {
			throw new JsonSyntaxException(String.format(
					"%s mapping of entity %s is missing the \"%s\" variant!", errorString, resource, ResourceLocation.DEFAULT_VARIANT
			));
		}
	}
}