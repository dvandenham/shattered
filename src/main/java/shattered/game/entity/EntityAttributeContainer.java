package shattered.game.entity;

import java.util.List;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class EntityAttributeContainer {

	private final Object2ObjectArrayMap<EntityAttributes, Object> defaults = new Object2ObjectArrayMap<>();
	private final Object2ObjectArrayMap<EntityAttributes, Object> values = new Object2ObjectArrayMap<>();

	void add(@NotNull final EntityAttributes attribute, final Object value) {
		if (!this.defaults.containsKey(attribute)) {
			this.defaults.put(attribute, value);
		}
		this.values.put(attribute, value);
	}

	@Nullable
	public Object getDefault(@NotNull final EntityAttributes attribute) {
		final Object result = this.defaults.get(attribute);
		if (result != null) {
			return result;
		}
		return attribute.getDefaultValue();
	}

	@Nullable
	public Object get(@NotNull final EntityAttributes attribute) {
		final Object result = this.values.get(attribute);
		if (result != null) {
			return result;
		}
		return attribute.getDefaultValue();
	}

	@NotNull
	public EntityAttributeContainer copy() {
		final EntityAttributeContainer result = new EntityAttributeContainer();
		result.defaults.putAll(this.defaults.clone());
		result.values.putAll(this.values.clone());
		return result;
	}

	@NotNull
	public List<EntityAttributes> getRegisteredAttributes() {
		return new ObjectArrayList<>(this.values.keySet());
	}
}
