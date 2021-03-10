package shattered.core.sdb;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Objects;
import shattered.lib.math.Dimension;
import shattered.lib.math.Point;
import shattered.lib.math.Rectangle;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("unused")
public final class SDBArray extends SDBTag {

	private final ObjectArrayList<SDBTag> data = new ObjectArrayList<>();

	public SDBArray() {
		super(SDBTypes.ARRAY);
	}

	public void add(final boolean value) {
		this.addTag(Objects.requireNonNull(SDBTypes.createWithData(value)));
	}

	public void add(final byte value) {
		this.addTag(Objects.requireNonNull(SDBTypes.createWithData(value)));
	}

	public void add(final short value) {
		this.addTag(Objects.requireNonNull(SDBTypes.createWithData(value)));
	}

	public void add(final int value) {
		this.addTag(Objects.requireNonNull(SDBTypes.createWithData(value)));
	}

	public void add(final long value) {
		this.addTag(Objects.requireNonNull(SDBTypes.createWithData(value)));
	}

	public void add(final float value) {
		this.addTag(Objects.requireNonNull(SDBTypes.createWithData(value)));
	}

	public void add(final double value) {
		this.addTag(Objects.requireNonNull(SDBTypes.createWithData(value)));
	}

	public void add(final char value) {
		this.addTag(Objects.requireNonNull(SDBTypes.createWithData(value)));
	}

	public void add(@NotNull final String value) {
		this.addTag(Objects.requireNonNull(SDBTypes.createWithData(value)));
	}

	public void add(@NotNull final Point value) {
		this.addTag(Objects.requireNonNull(SDBTypes.createWithData(value)));
	}

	public void add(@NotNull final Dimension value) {
		this.addTag(Objects.requireNonNull(SDBTypes.createWithData(value)));
	}

	public void add(@NotNull final Rectangle value) {
		this.addTag(Objects.requireNonNull(SDBTypes.createWithData(value)));
	}

	public void addTag(@NotNull final SDBTag tag) {
		this.data.add(tag);
	}

	@NotNull
	public SDBArray addArray() {
		final SDBArray result = new SDBArray();
		this.data.add(result);
		return result;
	}

	@NotNull
	public SDBTable addTable() {
		final SDBTable result = new SDBTable();
		this.data.add(result);
		return result;
	}

	public boolean hasBoolean(final int index) {
		return this.hasTag(index, SDBTypes.BOOLEAN);
	}

	public boolean hasByte(final int index) {
		return this.hasTag(index, SDBTypes.BYTE);
	}

	public boolean hasShort(final int index) {
		return this.hasTag(index, SDBTypes.SHORT);
	}

	public boolean hasInteger(final int index) {
		return this.hasTag(index, SDBTypes.INTEGER);
	}

	public boolean hasLong(final int index) {
		return this.hasTag(index, SDBTypes.LONG);
	}

	public boolean hasFloat(final int index) {
		return this.hasTag(index, SDBTypes.FLOAT);
	}

	public boolean hasDouble(final int index) {
		return this.hasTag(index, SDBTypes.DOUBLE);
	}

	public boolean hasCharacter(final int index) {
		return this.hasTag(index, SDBTypes.CHARACTER);
	}

	public boolean hasString(final int index) {
		return this.hasTag(index, SDBTypes.STRING);
	}

	public boolean hasPoint(final int index) {
		return this.hasTag(index, SDBTypes.POINT);
	}

	public boolean hasDimension(final int index) {
		return this.hasTag(index, SDBTypes.DIMENSION);
	}

	public boolean hasRectangle(final int index) {
		return this.hasTag(index, SDBTypes.RECTANGLE);
	}

	public boolean hasArray(final int index) {
		return this.hasTag(index, SDBTypes.ARRAY);
	}

	public boolean hasTable(final int index) {
		return this.hasTag(index, SDBTypes.TABLE);
	}

	public boolean hasTag(final int index, @NotNull final SDBTypes type) {
		return index >= 0 && index < this.data.size() && this.data.get(index).type == type;
	}

	public boolean getBoolean(final int index) {
		return this.hasBoolean(index) && ((SDBPrimitive.TagBoolean) this.data.get(index)).value;
	}

	public byte getByte(final int index) {
		return this.hasByte(index) ? ((SDBPrimitive.TagByte) this.data.get(index)).value : 0;
	}

	public short getShort(final int index) {
		return this.hasShort(index) ? ((SDBPrimitive.TagShort) this.data.get(index)).value : 0;
	}

	public int getInteger(final int index) {
		return this.hasInteger(index) ? ((SDBPrimitive.TagInteger) this.data.get(index)).value : 0;
	}

	public long getLong(final int index) {
		return this.hasLong(index) ? ((SDBPrimitive.TagLong) this.data.get(index)).value : 0;
	}

	public float getFloat(final int index) {
		return this.hasFloat(index) ? ((SDBPrimitive.TagFloat) this.data.get(index)).value : 0;
	}

	public double getDouble(final int index) {
		return this.hasDouble(index) ? ((SDBPrimitive.TagDouble) this.data.get(index)).value : 0;
	}

	public char getCharacter(final int index) {
		return this.hasCharacter(index) ? ((SDBPrimitive.TagCharacter) this.data.get(index)).value : 0;
	}

	@Nullable
	public String getString(final int index) {
		return this.hasString(index) ? ((SDBPrimitive.TagString) this.data.get(index)).value : null;
	}

	@Nullable
	public Point getPoint(final int index) {
		return this.hasPoint(index) ? ((SDBWrappers.TagPoint) this.data.get(index)).value : null;
	}

	@Nullable
	public Dimension getDimension(final int index) {
		return this.hasDimension(index) ? ((SDBWrappers.TagDimension) this.data.get(index)).value : null;
	}

	@Nullable
	public Rectangle getRectangle(final int index) {
		return this.hasRectangle(index) ? ((SDBWrappers.TagRectangle) this.data.get(index)).value : null;
	}

	@Nullable
	public SDBArray getArray(final int index) {
		return this.hasArray(index) ? (SDBArray) this.data.get(index) : null;
	}

	@Nullable
	public SDBTable getTable(final int index) {
		return this.hasTable(index) ? (SDBTable) this.data.get(index) : null;
	}

	@Nullable
	public SDBTag getTag(final int index) {
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
		for (final SDBTag tag : this.data) {
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
			final SDBTypes type = SDBTypes.getById(typeId);
			if (type == null) {
				throw new IOException(String.format("There is no tag with type %s", typeId));
			}
			final SDBTag tag = type.createEmpty();
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
		final SDBArray other = (SDBArray) o;
		return Objects.equals(this.data, other.data);
	}
}