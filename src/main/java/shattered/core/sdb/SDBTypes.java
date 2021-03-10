package shattered.core.sdb;

import java.util.function.Supplier;
import shattered.lib.math.Dimension;
import shattered.lib.math.Point;
import shattered.lib.math.Rectangle;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public enum SDBTypes {

	BOOLEAN(SDBPrimitive.TagBoolean::new, Boolean.class),
	BYTE(SDBPrimitive.TagByte::new, Byte.class),
	SHORT(SDBPrimitive.TagShort::new, Short.class),
	INTEGER(SDBPrimitive.TagInteger::new, Integer.class),
	LONG(SDBPrimitive.TagLong::new, Long.class),
	FLOAT(SDBPrimitive.TagFloat::new, Float.class),
	DOUBLE(SDBPrimitive.TagDouble::new, Double.class),
	CHARACTER(SDBPrimitive.TagCharacter::new, Character.class),
	STRING(SDBPrimitive.TagString::new, String.class),
	ARRAY(SDBArray::new, null),
	TABLE(SDBTable::new, null),
	POINT(SDBWrappers.TagPoint::new, Point.class),
	DIMENSION(SDBWrappers.TagDimension::new, Dimension.class),
	RECTANGLE(SDBWrappers.TagRectangle::new, Rectangle.class);

	private static final SDBTypes[] VALUES = SDBTypes.values();
	private final Supplier<SDBTag> factory;
	private final byte id;
	private final Class<?> typeClazz;

	SDBTypes(final Supplier<SDBTag> factory, final Class<?> typeClazz) {
		this.factory = factory;
		this.id = (byte) this.ordinal();
		this.typeClazz = typeClazz;
	}

	@NotNull
	SDBTag createEmpty() {
		return this.factory.get();
	}

	public byte getId() {
		return this.id;
	}

	@Nullable
	public static SDBTypes getById(final byte id) {
		return id >= 0 && id < SDBTypes.VALUES.length ? SDBTypes.VALUES[id] : null;
	}

	@Nullable
	public static SDBTypes getByDataType(@NotNull final Object data) {
		final Class<?> typeClazz = data.getClass();
		for (final SDBTypes type : SDBTypes.VALUES) {
			if (type.typeClazz == typeClazz) {
				return type;
			}
		}
		return null;
	}

	@Nullable
	@SuppressWarnings("unchecked")
	public static SDBTag createWithData(@NotNull final Object data) {
		final SDBTypes type = SDBTypes.getByDataType(data);
		if (type == null) {
			return null;
		}
		final SDBTag result = type.createEmpty();
		if (result instanceof SDBPrimitive) {
			((SDBPrimitive<Object>) result).value = data;
		}
		if (result instanceof SDBWrappers) {
			((SDBWrappers<Object>) result).value = data;
		}
		return result;
	}
}