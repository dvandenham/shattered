package shattered.core.nbtx;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Objects;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import shattered.lib.math.Dimension;
import shattered.lib.math.Point;
import shattered.lib.math.Rectangle;

@SuppressWarnings("unused")
public final class NBTXTagArray extends NBTXTag {

	private final ObjectArrayList<NBTXTag> data = new ObjectArrayList<>();

	public NBTXTagArray() {
		super(NBTXTypes.ARRAY);
	}

	public void add(final boolean value) {
		this.addTag(Objects.requireNonNull(NBTXTypes.createWithData(value)));
	}

	public void add(final byte value) {
		this.addTag(Objects.requireNonNull(NBTXTypes.createWithData(value)));
	}

	public void add(final short value) {
		this.addTag(Objects.requireNonNull(NBTXTypes.createWithData(value)));
	}

	public void add(final int value) {
		this.addTag(Objects.requireNonNull(NBTXTypes.createWithData(value)));
	}

	public void add(final long value) {
		this.addTag(Objects.requireNonNull(NBTXTypes.createWithData(value)));
	}

	public void add(final float value) {
		this.addTag(Objects.requireNonNull(NBTXTypes.createWithData(value)));
	}

	public void add(final double value) {
		this.addTag(Objects.requireNonNull(NBTXTypes.createWithData(value)));
	}

	public void add(final char value) {
		this.addTag(Objects.requireNonNull(NBTXTypes.createWithData(value)));
	}

	public void add(@NotNull final String value) {
		this.addTag(Objects.requireNonNull(NBTXTypes.createWithData(value)));
	}

	public void add(@NotNull final Point value) {
		this.addTag(Objects.requireNonNull(NBTXTypes.createWithData(value)));
	}

	public void add(@NotNull final Dimension value) {
		this.addTag(Objects.requireNonNull(NBTXTypes.createWithData(value)));
	}

	public void add(@NotNull final Rectangle value) {
		this.addTag(Objects.requireNonNull(NBTXTypes.createWithData(value)));
	}

	public void addTag(@NotNull final NBTXTag tag) {
		this.data.add(tag);
	}

	@NotNull
	public NBTXTagArray addArray() {
		final NBTXTagArray result = new NBTXTagArray();
		this.data.add(result);
		return result;
	}

	@NotNull
	public NBTXTagTable addTable() {
		final NBTXTagTable result = new NBTXTagTable();
		this.data.add(result);
		return result;
	}

	public boolean hasBoolean(final int index) {
		return this.hasTag(index, NBTXTypes.BOOLEAN);
	}

	public boolean hasByte(final int index) {
		return this.hasTag(index, NBTXTypes.BYTE);
	}

	public boolean hasShort(final int index) {
		return this.hasTag(index, NBTXTypes.SHORT);
	}

	public boolean hasInteger(final int index) {
		return this.hasTag(index, NBTXTypes.INTEGER);
	}

	public boolean hasLong(final int index) {
		return this.hasTag(index, NBTXTypes.LONG);
	}

	public boolean hasFloat(final int index) {
		return this.hasTag(index, NBTXTypes.FLOAT);
	}

	public boolean hasDouble(final int index) {
		return this.hasTag(index, NBTXTypes.DOUBLE);
	}

	public boolean hasCharacter(final int index) {
		return this.hasTag(index, NBTXTypes.CHARACTER);
	}

	public boolean hasString(final int index) {
		return this.hasTag(index, NBTXTypes.STRING);
	}

	public boolean hasPoint(final int index) {
		return this.hasTag(index, NBTXTypes.POINT);
	}

	public boolean hasDimension(final int index) {
		return this.hasTag(index, NBTXTypes.DIMENSION);
	}

	public boolean hasRectangle(final int index) {
		return this.hasTag(index, NBTXTypes.RECTANGLE);
	}

	public boolean hasArray(final int index) {
		return this.hasTag(index, NBTXTypes.ARRAY);
	}

	public boolean hasTable(final int index) {
		return this.hasTag(index, NBTXTypes.TABLE);
	}

	public boolean hasTag(final int index, @NotNull final NBTXTypes type) {
		return index >= 0 && index < this.data.size() && this.data.get(index).type == type;
	}

	public boolean getBoolean(final int index) {
		return this.hasBoolean(index) && ((NBTXTagPrimitive.TagBoolean) this.data.get(index)).value;
	}

	public byte getByte(final int index) {
		return this.hasByte(index) ? ((NBTXTagPrimitive.TagByte) this.data.get(index)).value : 0;
	}

	public short getShort(final int index) {
		return this.hasShort(index) ? ((NBTXTagPrimitive.TagShort) this.data.get(index)).value : 0;
	}

	public int getInteger(final int index) {
		return this.hasInteger(index) ? ((NBTXTagPrimitive.TagInteger) this.data.get(index)).value : 0;
	}

	public long getLong(final int index) {
		return this.hasLong(index) ? ((NBTXTagPrimitive.TagLong) this.data.get(index)).value : 0;
	}

	public float getFloat(final int index) {
		return this.hasFloat(index) ? ((NBTXTagPrimitive.TagFloat) this.data.get(index)).value : 0;
	}

	public double getDouble(final int index) {
		return this.hasDouble(index) ? ((NBTXTagPrimitive.TagDouble) this.data.get(index)).value : 0;
	}

	public char getCharacter(final int index) {
		return this.hasCharacter(index) ? ((NBTXTagPrimitive.TagCharacter) this.data.get(index)).value : 0;
	}

	@Nullable
	public String getString(final int index) {
		return this.hasString(index) ? ((NBTXTagPrimitive.TagString) this.data.get(index)).value : null;
	}

	@Nullable
	public Point getPoint(final int index) {
		return this.hasPoint(index) ? ((NBTXTagWrappers.TagPoint) this.data.get(index)).value : null;
	}

	@Nullable
	public Dimension getDimension(final int index) {
		return this.hasDimension(index) ? ((NBTXTagWrappers.TagDimension) this.data.get(index)).value : null;
	}

	@Nullable
	public Rectangle getRectangle(final int index) {
		return this.hasRectangle(index) ? ((NBTXTagWrappers.TagRectangle) this.data.get(index)).value : null;
	}

	@Nullable
	public NBTXTagArray getArray(final int index) {
		return this.hasArray(index) ? (NBTXTagArray) this.data.get(index) : null;
	}

	@Nullable
	public NBTXTagTable getTable(final int index) {
		return this.hasTable(index) ? (NBTXTagTable) this.data.get(index) : null;
	}

	@Nullable
	public NBTXTag getTag(final int index) {
		return index >= 0 && index < this.data.size() ? this.data.get(index) : null;
	}

	public void remove(final int index) {
		if (index >= 0 && index < this.data.size()) {
			this.data.remove(index);
		}
	}

	public int getSize() {
		return this.data.size();
	}

	public void clear() {
		this.data.clear();
	}

	@Override
	void serialize(@NotNull final DataOutput output) throws IOException {
		output.writeInt(this.data.size());
		for (final NBTXTag tag : this.data) {
			output.writeByte(tag.type.getId());
			tag.serialize(output);
		}
	}

	@Override
	void read(@NotNull final DataInput input) throws IOException {
		this.data.clear();
		final int size = input.readInt();
		for (int i = 0; i < size; ++i) {
			final byte typeId = input.readByte();
			final NBTXTypes type = NBTXTypes.getById(typeId);
			if (type == null) {
				throw new IOException(String.format("There is no tag with type %s", typeId));
			}
			final NBTXTag tag = type.createEmpty();
			tag.read(input);
			this.data.add(tag);
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
		final NBTXTagArray other = (NBTXTagArray) o;
		return Objects.equals(this.data, other.data);
	}
}