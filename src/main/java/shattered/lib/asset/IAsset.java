package shattered.lib.asset;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import shattered.lib.ResourceLocation;

public abstract class IAsset {

	private final ResourceLocation resource;

	IAsset(@NotNull final ResourceLocation resource) {
		this.resource = resource;
	}

	@NotNull
	public final ResourceLocation getResource() {
		return this.resource;
	}

	void recreate(@Nullable final IAsset newAsset) {
		try {
			final Field modifiers = Field.class.getDeclaredField("modifiers");
			modifiers.setAccessible(true);

			Class<?> clazz = this.getClass();
			while (clazz != Object.class) {
				for (final Field field : clazz.getDeclaredFields()) {
					//Open field for changes
					@SuppressWarnings("deprecation") final boolean accessible = field.isAccessible();
					field.setAccessible(true);
					final boolean isFinal = Modifier.isFinal(field.getModifiers());
					if (isFinal) {
						modifiers.setInt(field, field.getModifiers() & ~Modifier.FINAL);
					}
					//Get new value
					final Object value = field.get(newAsset);
					//Replace old value
					field.set(this, value);
					//Close field
					if (isFinal) {
						modifiers.setInt(field, field.getModifiers() & Modifier.FINAL);
					}
					field.setAccessible(accessible);
				}
				clazz = clazz.getSuperclass();
			}

			modifiers.setAccessible(false);
		} catch (final Throwable e) {
			e.printStackTrace();
			Runtime.getRuntime().halt(-1);
		}
	}
}