package shattered.core.nbtx;

import java.util.function.Supplier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import shattered.lib.math.Dimension;
import shattered.lib.math.Point;
import shattered.lib.math.Rectangle;

public enum NBTXTypes {

	BOOLEAN(NBTXTagPrimitive.TagBoolean::new, Boolean.class),
	BYTE(NBTXTagPrimitive.TagByte::new, Byte.class),
	SHORT(NBTXTagPrimitive.TagShort::new, Short.class),
	INTEGER(NBTXTagPrimitive.TagInteger::new, Integer.class),
	LONG(NBTXTagPrimitive.TagLong::new, Long.class),
	FLOAT(NBTXTagPrimitive.TagFloat::new, Float.class),
	DOUBLE(NBTXTagPrimitive.TagDouble::new, Double.class),
	CHARACTER(NBTXTagPrimitive.TagCharacter::new, Character.class),
	STRING(NBTXTagPrimitive.TagString::new, String.class),
	ARRAY(NBTXTagArray::new, null),
	TABLE(NBTXTagTable::new, null),
	POINT(NBTXTagWrappers.TagPoint::new, Point.class),
	DIMENSION(NBTXTagWrappers.TagDimension::new, Dimension.class),
	RECTANGLE(NBTXTagWrappers.TagRectangle::new, Rectangle.class);

	private static final NBTXTypes[] VALUES = NBTXTypes.values();
	private final Supplier<NBTXTag> factory;
	private final byte id;
	private final Class<?> typeClazz;

	NBTXTypes(final Supplier<NBTXTag> factory, final Class<?> typeClazz) {
		this.factory = factory;
		this.id = (byte) this.ordinal();
		this.typeClazz = typeClazz;
	}

	@NotNull
	NBTXTag createEmpty() {
		return this.factory.get();
	}

	public byte getId() {
		return this.id;
	}

	@Nullable
	public static NBTXTypes getById(final byte id) {
		return id >= 0 && id < NBTXTypes.VALUES.length ? NBTXTypes.VALUES[id] : null;
	}

	@Nullable
	public static NBTXTypes getByDataType(@NotNull final Object data) {
		final Class<?> typeClazz = data.getClass();
		for (final NBTXTypes type : NBTXTypes.VALUES) {
			if (type.typeClazz == typeClazz) {
				return type;
			}
		}
		return null;
	}

	@Nullable
	@SuppressWarnings("unchecked")
	public static NBTXTag createWithData(@NotNull final Object data) {
		final NBTXTypes type = NBTXTypes.getByDataType(data);
		if (type == null) {
			return null;
		}
		final NBTXTag result = type.createEmpty();
		if (result instanceof NBTXTagPrimitive) {
			((NBTXTagPrimitive<Object>) result).value = data;
		}
		if (result instanceof NBTXTagWrappers) {
			((NBTXTagWrappers<Object>) result).value = data;
		}
		return result;
	}
}