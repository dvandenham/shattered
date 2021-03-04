package shattered.core.nbtx;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import shattered.lib.FastNamedObjectMap;
import shattered.lib.math.Dimension;
import shattered.lib.math.Point;
import shattered.lib.math.Rectangle;

@SuppressWarnings("ConstantConditions")
public class NBTXTagTable extends NBTXTag implements INBTXKeyValueStore {

	private final FastNamedObjectMap<NBTXTag> data = new FastNamedObjectMap<>();

	public NBTXTagTable() {
		super(NBTXTypes.TABLE);
	}

	@Override
	public void set(@NotNull final String name, final boolean value) {
		this.setTag(name, Objects.requireNonNull(NBTXTypes.createWithData(value)));
	}

	@Override
	public void set(@NotNull final String name, final byte value) {
		this.setTag(name, Objects.requireNonNull(NBTXTypes.createWithData(value)));
	}

	@Override
	public void set(@NotNull final String name, final short value) {
		this.setTag(name, Objects.requireNonNull(NBTXTypes.createWithData(value)));
	}

	@Override
	public void set(@NotNull final String name, final int value) {
		this.setTag(name, Objects.requireNonNull(NBTXTypes.createWithData(value)));
	}

	@Override
	public void set(@NotNull final String name, final long value) {
		this.setTag(name, Objects.requireNonNull(NBTXTypes.createWithData(value)));
	}

	@Override
	public void set(@NotNull final String name, final float value) {
		this.setTag(name, Objects.requireNonNull(NBTXTypes.createWithData(value)));
	}

	@Override
	public void set(@NotNull final String name, final double value) {
		this.setTag(name, Objects.requireNonNull(NBTXTypes.createWithData(value)));
	}

	@Override
	public void set(@NotNull final String name, final char value) {
		this.setTag(name, Objects.requireNonNull(NBTXTypes.createWithData(value)));
	}

	@Override
	public void set(@NotNull final String name, @NotNull final String value) {
		this.setTag(name, Objects.requireNonNull(NBTXTypes.createWithData(value)));
	}

	@Override
	public void set(@NotNull final String name, @NotNull final Point value) {
		this.setTag(name, Objects.requireNonNull(NBTXTypes.createWithData(value)));
	}

	@Override
	public void set(@NotNull final String name, @NotNull final Dimension value) {
		this.setTag(name, Objects.requireNonNull(NBTXTypes.createWithData(value)));
	}

	@Override
	public void set(@NotNull final String name, @NotNull final Rectangle value) {
		this.setTag(name, Objects.requireNonNull(NBTXTypes.createWithData(value)));
	}

	@NotNull
	@Override
	public NBTXTagArray newArray(@NotNull final String name) {
		final NBTXTagArray Result = new NBTXTagArray();
		this.setTag(name, Result);
		return Result;
	}

	@NotNull
	@Override
	public NBTXTagTable newTable(@NotNull final String name) {
		final NBTXTagTable Result = new NBTXTagTable();
		this.setTag(name, Result);
		return Result;
	}

	@Override
	public void setTag(@NotNull final String name, @NotNull final NBTXTag tag) {
		this.data.put(name, tag);
	}

	@Override
	public boolean hasBoolean(@NotNull final String name) {
		return this.hasTag(name, NBTXTypes.BOOLEAN);
	}

	@Override
	public boolean hasByte(@NotNull final String name) {
		return this.hasTag(name, NBTXTypes.BYTE);
	}

	@Override
	public boolean hasShort(@NotNull final String name) {
		return this.hasTag(name, NBTXTypes.SHORT);
	}

	@Override
	public boolean hasInteger(@NotNull final String name) {
		return this.hasTag(name, NBTXTypes.INTEGER);
	}

	@Override
	public boolean hasLong(@NotNull final String name) {
		return this.hasTag(name, NBTXTypes.LONG);
	}

	@Override
	public boolean hasFloat(@NotNull final String name) {
		return this.hasTag(name, NBTXTypes.FLOAT);
	}

	@Override
	public boolean hasDouble(@NotNull final String name) {
		return this.hasTag(name, NBTXTypes.DOUBLE);
	}

	@Override
	public boolean hasCharacter(@NotNull final String name) {
		return this.hasTag(name, NBTXTypes.CHARACTER);
	}

	@Override
	public boolean hasString(@NotNull final String name) {
		return this.hasTag(name, NBTXTypes.STRING);
	}

	@Override
	public boolean hasPoint(@NotNull final String name) {
		return this.hasTag(name, NBTXTypes.POINT);
	}

	@Override
	public boolean hasDimension(@NotNull final String name) {
		return this.hasTag(name, NBTXTypes.DIMENSION);
	}

	@Override
	public boolean hasRectangle(@NotNull final String name) {
		return this.hasTag(name, NBTXTypes.RECTANGLE);
	}

	@Override
	public boolean hasArray(@NotNull final String name) {
		return this.hasTag(name, NBTXTypes.ARRAY);
	}

	@Override
	public boolean hasTable(@NotNull final String name) {
		return this.hasTag(name, NBTXTypes.TABLE);
	}

	@Override
	public boolean hasTag(@NotNull final String name) {
		return this.data.get(name) != null;
	}

	@Override
	public boolean hasTag(@NotNull final String name, @NotNull final NBTXTypes type) {
		final NBTXTag tag = this.data.get(name);
		return tag != null && tag.type == type;
	}

	@Override
	public boolean getBoolean(@NotNull final String name) {
		return this.hasBoolean(name) && ((NBTXTagPrimitive.TagBoolean) this.getTag(name)).value;
	}

	@Override
	public byte getByte(@NotNull final String name) {
		return this.hasByte(name) ? ((NBTXTagPrimitive.TagByte) this.getTag(name)).value : 0;
	}

	@Override
	public short getShort(@NotNull final String name) {
		return this.hasShort(name) ? ((NBTXTagPrimitive.TagShort) this.getTag(name)).value : 0;
	}

	@Override
	public int getInteger(@NotNull final String name) {
		return this.hasInteger(name) ? ((NBTXTagPrimitive.TagInteger) this.getTag(name)).value : 0;
	}

	@Override
	public long getLong(@NotNull final String name) {
		return this.hasLong(name) ? ((NBTXTagPrimitive.TagLong) this.getTag(name)).value : 0;
	}

	@Override
	public float getFloat(@NotNull final String name) {
		return this.hasFloat(name) ? ((NBTXTagPrimitive.TagFloat) this.getTag(name)).value : 0;
	}

	@Override
	public double getDouble(@NotNull final String name) {
		return this.hasDouble(name) ? ((NBTXTagPrimitive.TagDouble) this.getTag(name)).value : 0;
	}

	@Override
	public char getCharacter(@NotNull final String name) {
		return this.hasCharacter(name) ? ((NBTXTagPrimitive.TagCharacter) this.getTag(name)).value : 0;
	}

	@Override
	@Nullable
	public String getString(@NotNull final String name) {
		return this.hasString(name) ? ((NBTXTagPrimitive.TagString) this.getTag(name)).value : null;
	}

	@Override
	@Nullable
	public Point getPoint(@NotNull final String name) {
		return this.hasPoint(name) ? ((NBTXTagWrappers.TagPoint) this.getTag(name)).value : null;
	}

	@Override
	@Nullable
	public Dimension getDimension(@NotNull final String name) {
		return this.hasDimension(name) ? ((NBTXTagWrappers.TagDimension) this.getTag(name)).value : null;
	}

	@Override
	@Nullable
	public Rectangle getRectangle(@NotNull final String name) {
		return this.hasRectangle(name) ? ((NBTXTagWrappers.TagRectangle) this.getTag(name)).value : null;
	}

	@Override
	@Nullable
	public NBTXTagArray getArray(@NotNull final String name) {
		return this.hasArray(name) ? (NBTXTagArray) this.getTag(name) : null;
	}

	@Override
	@Nullable
	public NBTXTagTable getTable(@NotNull final String name) {
		return this.hasTable(name) ? (NBTXTagTable) this.getTag(name) : null;
	}

	@Override
	@Nullable
	public NBTXTag getTag(@NotNull final String name) {
		return this.data.get(name);
	}

	@Override
	public void removeKey(@NotNull final String name) {
		this.data.remove(name);
	}

	@Override
	@NotNull
	public String[] getKeyNames() {
		return this.data.keySet().toArray(new String[0]);
	}

	@Override
	public void clear() {
		this.data.clear();
	}

	@Override
	void serialize(@NotNull final DataOutput output) throws IOException {
		output.writeInt(this.data.size());
		for (final Map.Entry<String, NBTXTag> entry : this.data.entrySet()) {
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
			final NBTXTypes type = NBTXTypes.getById(typeId);
			if (type == null) {
				throw new IOException(String.format("There is no tag with type %s", typeId));
			}
			final NBTXTag tag = type.createEmpty();
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
		final NBTXTagTable other = (NBTXTagTable) o;
		return Objects.equals(this.data, other.data);
	}
}