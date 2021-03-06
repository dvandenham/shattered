package shattered.game.entity;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import shattered.Shattered;

public enum EntityAttributes {

	HEALTH(Number.class, null),
	HAS_GRAVITY(Boolean.class, true);

	private static final EntityAttributes[] VALUES = EntityAttributes.values();
	public static final List<EntityAttributes> REQUIRED_ATTRIBUTES = Arrays.stream(EntityAttributes.VALUES)
			.filter(Attribute -> Attribute.getDefaultValue() == null)
			.collect(Collectors.toList());

	private final String identifier;
	private final Class<?> typeClazz;
	private final Object defaultValue;

	EntityAttributes(@NotNull final Class<?> typeClazz, @Nullable final Object defaultValue) {
		this.identifier = super.toString().toLowerCase(Locale.ROOT);
		this.typeClazz = typeClazz;
		this.defaultValue = defaultValue;
	}

	@Nullable
	public Object getDefaultValue() {
		return this.defaultValue;
	}

	@Override
	public String toString() {
		return this.identifier;
	}

	@Nullable
	public static EntityAttributes getByIdentifier(final String identifier) {
		for (final EntityAttributes attribute : EntityAttributes.VALUES) {
			if (attribute.identifier.equals(identifier)) {
				return attribute;
			}
		}
		return null;
	}

	@NotNull
	static EntityAttributeContainer parseMap(@NotNull final HashMap<String, Object> mapping) {
		final EntityAttributeContainer result = new EntityAttributeContainer();
		for (final Map.Entry<String, Object> entity : mapping.entrySet()) {
			final EntityAttributes attribute = EntityAttributes.getByIdentifier(entity.getKey());
			if (attribute == null) {
				Shattered.LOGGER.error("Could not find entity attribute with id: {}", entity.getKey());
				continue;
			}
			if (!attribute.typeClazz.isAssignableFrom(entity.getValue().getClass())) {
				Shattered.LOGGER.error(
						"Invalid value type for entity attribute: {}. Found {}, expected {}",
						entity.getKey(),
						entity.getValue().getClass().getSimpleName(),
						attribute.typeClazz.getSimpleName()
				);
				continue;
			}
			result.add(attribute, entity.getValue());
		}
		return result;
	}
}