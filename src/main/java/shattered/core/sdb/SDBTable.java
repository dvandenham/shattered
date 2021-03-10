package shattered.core.sdb;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import shattered.lib.FastNamedObjectMap;
import shattered.lib.math.Dimension;
import shattered.lib.math.Point;
import shattered.lib.math.Rectangle;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("ConstantConditions")
public class SDBTable extends SDBTag {

	private final FastNamedObjectMap<SDBTag> data = new FastNamedObjectMap<>();

	public SDBTable() {
		super(SDBTypes.TABLE);
	}

	public void set(@NotNull final String name, final boolean value) {
		this.setTag(name, Objects.requireNonNull(SDBTypes.createWithData(value)));
	}

	public void set(@NotNull final String name, final byte value) {
		this.setTag(name, Objects.requireNonNull(SDBTypes.createWithData(value)));
	}

	public void set(@NotNull final String name, final short value) {
		this.setTag(name, Objects.requireNonNull(SDBTypes.createWithData(value)));
	}

	public void set(@NotNull final String name, final int value) {
		this.setTag(name, Objects.requireNonNull(SDBTypes.createWithData(value)));
	}

	public void set(@NotNull final String name, final long value) {
		this.setTag(name, Objects.requireNonNull(SDBTypes.createWithData(value)));
	}

	public void set(@NotNull final String name, final float value) {
		this.setTag(name, Objects.requireNonNull(SDBTypes.createWithData(value)));
	}

	public void set(@NotNull final String name, final double value) {
		this.setTag(name, Objects.requireNonNull(SDBTypes.createWithData(value)));
	}

	public void set(@NotNull final String name, final char value) {
		this.setTag(name, Objects.requireNonNull(SDBTypes.createWithData(value)));
	}

	public void set(@NotNull final String name, @NotNull final String value) {
		this.setTag(name, Objects.requireNonNull(SDBTypes.createWithData(value)));
	}

	public void set(@NotNull final String name, @NotNull final Point value) {
		this.setTag(name, Objects.requireNonNull(SDBTypes.createWithData(value)));
	}

	public void set(@NotNull final String name, @NotNull final Dimension value) {
		this.setTag(name, Objects.requireNonNull(SDBTypes.createWithData(value)));
	}

	public void set(@NotNull final String name, @NotNull final Rectangle value) {
		this.setTag(name, Objects.requireNonNull(SDBTypes.createWithData(value)));
	}

	@NotNull
	public SDBArray newArray(@NotNull final String name) {
		final SDBArray Result = new SDBArray();
		this.setTag(name, Result);
		return Result;
	}

	@NotNull
	public SDBTable newTable(@NotNull final String name) {
		final SDBTable Result = new SDBTable();
		this.setTag(name, Result);
		return Result;
	}

	public void setTag(@NotNull final String name, @NotNull final SDBTag tag) {
		this.data.put(name, tag);
	}

	public boolean hasBoolean(@NotNull final String name) {
		return this.hasTag(name, SDBTypes.BOOLEAN);
	}

	public boolean hasByte(@NotNull final String name) {
		return this.hasTag(name, SDBTypes.BYTE);
	}

	public boolean hasShort(@NotNull final String name) {
		return this.hasTag(name, SDBTypes.SHORT);
	}

	public boolean hasInteger(@NotNull final String name) {
		return this.hasTag(name, SDBTypes.INTEGER);
	}

	public boolean hasLong(@NotNull final String name) {
		return this.hasTag(name, SDBTypes.LONG);
	}

	public boolean hasFloat(@NotNull final String name) {
		return this.hasTag(name, SDBTypes.FLOAT);
	}

	public boolean hasDouble(@NotNull final String name) {
		return this.hasTag(name, SDBTypes.DOUBLE);
	}

	public boolean hasCharacter(@NotNull final String name) {
		return this.hasTag(name, SDBTypes.CHARACTER);
	}

	public boolean hasString(@NotNull final String name) {
		return this.hasTag(name, SDBTypes.STRING);
	}

	public boolean hasPoint(@NotNull final String name) {
		return this.hasTag(name, SDBTypes.POINT);
	}

	public boolean hasDimension(@NotNull final String name) {
		return this.hasTag(name, SDBTypes.DIMENSION);
	}

	public boolean hasRectangle(@NotNull final String name) {
		return this.hasTag(name, SDBTypes.RECTANGLE);
	}

	public boolean hasArray(@NotNull final String name) {
		return this.hasTag(name, SDBTypes.ARRAY);
	}

	public boolean hasTable(@NotNull final String name) {
		return this.hasTag(name, SDBTypes.TABLE);
	}

	public boolean hasTag(@NotNull final String name) {
		return this.data.get(name) != null;
	}

	public boolean hasTag(@NotNull final String name, @NotNull final SDBTypes type) {
		final SDBTag tag = this.data.get(name);
		return tag != null && tag.type == type;
	}

	public boolean getBoolean(@NotNull final String name) {
		return this.hasBoolean(name) && ((SDBPrimitive.TagBoolean) this.getTag(name)).value;
	}

	public byte getByte(@NotNull final String name) {
		return this.hasByte(name) ? ((SDBPrimitive.TagByte) this.getTag(name)).value : 0;
	}

	public short getShort(@NotNull final String name) {
		return this.hasShort(name) ? ((SDBPrimitive.TagShort) this.getTag(name)).value : 0;
	}

	public int getInteger(@NotNull final String name) {
		return this.hasInteger(name) ? ((SDBPrimitive.TagInteger) this.getTag(name)).value : 0;
	}

	public long getLong(@NotNull final String name) {
		return this.hasLong(name) ? ((SDBPrimitive.TagLong) this.getTag(name)).value : 0;
	}

	public float getFloat(@NotNull final String name) {
		return this.hasFloat(name) ? ((SDBPrimitive.TagFloat) this.getTag(name)).value : 0;
	}

	public double getDouble(@NotNull final String name) {
		return this.hasDouble(name) ? ((SDBPrimitive.TagDouble) this.getTag(name)).value : 0;
	}

	public char getCharacter(@NotNull final String name) {
		return this.hasCharacter(name) ? ((SDBPrimitive.TagCharacter) this.getTag(name)).value : 0;
	}

	@Nullable
	public String getString(@NotNull final String name) {
		return this.hasString(name) ? ((SDBPrimitive.TagString) this.getTag(name)).value : null;
	}

	@Nullable
	public Point getPoint(@NotNull final String name) {
		return this.hasPoint(name) ? ((SDBWrappers.TagPoint) this.getTag(name)).value : null;
	}

	@Nullable
	public Dimension getDimension(@NotNull final String name) {
		return this.hasDimension(name) ? ((SDBWrappers.TagDimension) this.getTag(name)).value : null;
	}

	@Nullable
	public Rectangle getRectangle(@NotNull final String name) {
		return this.hasRectangle(name) ? ((SDBWrappers.TagRectangle) this.getTag(name)).value : null;
	}

	@Nullable
	public SDBArray getArray(@NotNull final String name) {
		return this.hasArray(name) ? (SDBArray) this.getTag(name) : null;
	}

	@Nullable
	public SDBTable getTable(@NotNull final String name) {
		return this.hasTable(name) ? (SDBTable) this.getTag(name) : null;
	}

	@Nullable
	public SDBTag getTag(@NotNull final String name) {
		return this.data.get(name);
	}

	public void removeKey(@NotNull final String name) {
		this.data.remove(name);
	}

	@NotNull
	public String[] getKeyNames() {
		return this.data.keySet().toArray(new String[0]);
	}

	public int getKeyCount() {
		return this.data.size();
	}

	public void clear() {
		this.data.clear();
	}

	@Override
	void serialize(@NotNull final DataOutput output) throws IOException {
		output.writeInt(this.data.size());
		for (final Map.Entry<String, SDBTag> entry : this.data.entrySet()) {
			output.writeUTF(entry.getKey());
			output.writeByte(entry.getValue().type.getId());
			entry.getValue().serialize(output);
		}
	}

	@Override
	void read(@NotNull final DataInput input) throws IOException {
		this.data.clear();
		final int size = input.readInt();
		for (int i = 0; i < size; ++i) {
			final String keyName = input.readUTF();
			final byte typeId = input.readByte();
			final SDBTypes type = SDBTypes.getById(typeId);
			if (type == null) {
				throw new IOException(String.format("There is no tag with type %s", typeId));
			}
			final SDBTag tag = type.createEmpty();
			tag.read(input);
			this.data.put(keyName, tag);
		}
	}

	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(), this.data);
	}

	@Override
	public boolean equals(final Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || this.getClass() != o.getClass()) {
			return false;
		}
		if (!super.equals(o)) {
			return false;
		}
		final SDBTable other = (SDBTable) o;
		return Objects.equals(this.data, other.data);
	}
}