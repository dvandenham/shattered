package shattered.core.nbtx;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Objects;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import shattered.Shattered;
import shattered.lib.math.Dimension;
import shattered.lib.math.Point;
import shattered.lib.math.Rectangle;

@SuppressWarnings("unused")
public final class NBTX implements INBTXKeyValueStore {

	private static final int VERSION = 1;
	private final NBTXTagTable data = new NBTXTagTable();

	@Override
	public void set(@NotNull final String name, final boolean value) {
		this.data.set(name, value);
	}

	@Override
	public void set(@NotNull final String name, final byte value) {
		this.data.set(name, value);
	}

	@Override
	public void set(@NotNull final String name, final short value) {
		this.data.set(name, value);
	}

	@Override
	public void set(@NotNull final String name, final int value) {
		this.data.set(name, value);
	}

	@Override
	public void set(@NotNull final String name, final long value) {
		this.data.set(name, value);
	}

	@Override
	public void set(@NotNull final String name, final float value) {
		this.data.set(name, value);
	}

	@Override
	public void set(@NotNull final String name, final double value) {
		this.data.set(name, value);
	}

	@Override
	public void set(@NotNull final String name, final char value) {
		this.data.set(name, value);
	}

	@Override
	public void set(@NotNull final String name, @NotNull final String value) {
		this.data.set(name, value);
	}

	@Override
	public void set(@NotNull final String name, @NotNull final Point value) {
		this.data.set(name, value);
	}

	@Override
	public void set(@NotNull final String name, @NotNull final Dimension value) {
		this.data.set(name, value);
	}

	@Override
	public void set(@NotNull final String name, @NotNull final Rectangle value) {
		this.data.set(name, value);
	}

	@Override
	@NotNull
	public NBTXTagArray newArray(@NotNull final String name) {
		return this.data.newArray(name);
	}

	@Override
	@NotNull
	public NBTXTagTable newTable(@NotNull final String name) {
		return this.data.newTable(name);
	}

	@Override
	public void setTag(@NotNull final String name, @NotNull final NBTXTag tag) {
		this.data.setTag(name, tag);
	}

	@Override
	public boolean hasBoolean(@NotNull final String name) {
		return this.data.hasBoolean(name);
	}

	@Override
	public boolean hasByte(@NotNull final String name) {
		return this.data.hasByte(name);
	}

	@Override
	public boolean hasShort(@NotNull final String name) {
		return this.data.hasShort(name);
	}

	@Override
	public boolean hasInteger(@NotNull final String name) {
		return this.data.hasInteger(name);
	}

	@Override
	public boolean hasLong(@NotNull final String name) {
		return this.data.hasLong(name);
	}

	@Override
	public boolean hasFloat(@NotNull final String name) {
		return this.data.hasFloat(name);
	}

	@Override
	public boolean hasDouble(@NotNull final String name) {
		return this.data.hasDouble(name);
	}

	@Override
	public boolean hasCharacter(@NotNull final String name) {
		return this.data.hasCharacter(name);
	}

	@Override
	public boolean hasString(@NotNull final String name) {
		return this.data.hasString(name);
	}

	@Override
	public boolean hasPoint(@NotNull final String name) {
		return this.data.hasPoint(name);
	}

	@Override
	public boolean hasDimension(@NotNull final String name) {
		return this.data.hasDimension(name);
	}

	@Override
	public boolean hasRectangle(@NotNull final String name) {
		return this.data.hasRectangle(name);
	}

	@Override
	public boolean hasArray(@NotNull final String name) {
		return this.data.hasArray(name);
	}

	@Override
	public boolean hasTable(@NotNull final String name) {
		return this.data.hasTable(name);
	}

	@Override
	public boolean hasTag(@NotNull final String name) {
		return this.data.hasTag(name);
	}

	@Override
	public boolean hasTag(@NotNull final String name, @NotNull final NBTXTypes type) {
		return this.data.hasTag(name, type);
	}

	@Override
	public boolean getBoolean(@NotNull final String name) {
		return this.data.getBoolean(name);
	}

	@Override
	public byte getByte(@NotNull final String name) {
		return this.data.getByte(name);
	}

	@Override
	public short getShort(@NotNull final String name) {
		return this.data.getShort(name);
	}

	@Override
	public int getInteger(@NotNull final String name) {
		return this.data.getInteger(name);
	}

	@Override
	public long getLong(@NotNull final String name) {
		return this.data.getLong(name);
	}

	@Override
	public float getFloat(@NotNull final String name) {
		return this.data.getFloat(name);
	}

	@Override
	public double getDouble(@NotNull final String name) {
		return this.data.getDouble(name);
	}

	@Override
	public char getCharacter(@NotNull final String name) {
		return this.data.getCharacter(name);
	}

	@Override
	@Nullable
	public String getString(@NotNull final String name) {
		return this.data.getString(name);
	}

	@Override
	public @Nullable Point getPoint(@NotNull final String name) {
		return this.data.getPoint(name);
	}

	@Override
	public @Nullable Dimension getDimension(@NotNull final String name) {
		return this.data.getDimension(name);
	}

	@Override
	public @Nullable Rectangle getRectangle(@NotNull final String name) {
		return this.data.getRectangle(name);
	}

	@Override
	@Nullable
	public NBTXTagArray getArray(@NotNull final String name) {
		return this.data.getArray(name);
	}

	@Override
	@Nullable
	public NBTXTagTable getTable(@NotNull final String name) {
		return this.data.getTable(name);
	}

	@Override
	@Nullable
	public NBTXTag getTag(@NotNull final String name) {
		return this.data.getTag(name);
	}

	@Override
	public void removeKey(@NotNull final String name) {
		this.data.removeKey(name);
	}

	@Override
	@NotNull
	public String[] getKeyNames() {
		return this.data.getKeyNames();
	}

	@Override
	public int getKeyCount() {
		return this.data.getKeyCount();
	}

	@Override
	public void clear() {
		this.data.clear();
	}

	private void writeData(@NotNull final DataOutput output) throws IOException {
		output.writeInt(NBTX.VERSION);
		output.writeByte(NBTXTypes.TABLE.getId());
		this.data.serialize(output);
	}

	public void serialize(@NotNull final OutputStream output) throws IOException {
		try (final DataOutputStream Out = new DataOutputStream(new BufferedOutputStream(new GZIPOutputStream(output)))) {
			this.writeData(Out);
		}
	}

	public void serialize(@NotNull final File file) throws IOException {
		this.serialize(new FileOutputStream(file));
	}

	public void serializeUncompressed(@NotNull final OutputStream output) throws IOException {
		try (final DataOutputStream Out = new DataOutputStream(new BufferedOutputStream(output))) {
			this.writeData(Out);
		}
	}

	private void readData(@NotNull final DataInput input) throws IOException {
		final int version = input.readInt();
		if (version > NBTX.VERSION) {
			Shattered.LOGGER.warn("Trying to read {} with a version higher as the current version!", this.getClass().getSimpleName());
			Shattered.LOGGER.warn("\tNOT reading since this can cause unknown side effects!");
			return;
		}
		if (input.readByte() != NBTXTypes.TABLE.getId()) {
			Shattered.LOGGER.warn("Trying to read {} with a wrong header type!", this.getClass().getSimpleName());
			Shattered.LOGGER.warn("\tNOT reading since this can cause unknown side effects!");
			Shattered.LOGGER.warn("\tAre you sure you are reading the correct input?");
			return;
		}
		this.data.clear();
		this.data.read(input);
	}

	public void deserialize(@NotNull final InputStream input) throws IOException {
		try (final DataInputStream in = new DataInputStream(new BufferedInputStream(new GZIPInputStream(input)))) {
			this.readData(in);
		}
	}

	public void deserialize(@NotNull final File file) throws IOException {
		this.deserialize(new FileInputStream(file));
	}

	public void deserializeUncompressed(@NotNull final InputStream input) throws IOException {
		try (final DataInputStream in = new DataInputStream(new BufferedInputStream(input))) {
			this.readData(in);
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
		final NBTX other = (NBTX) o;
		return Objects.equals(this.data, other.data);
	}

	@NotNull
	public static NBTX deserializeNBTX(@NotNull final InputStream input) throws IOException {
		final NBTX result = new NBTX();
		result.deserialize(input);
		return result;
	}

	@NotNull
	public static NBTX deserializeNBTX(@NotNull final File file) throws IOException {
		final NBTX result = new NBTX();
		result.deserialize(file);
		return result;
	}

	@NotNull
	public static NBTX deserializeNBTXUncompressed(@NotNull final InputStream input) throws IOException {
		final NBTX result = new NBTX();
		result.deserializeUncompressed(input);
		return result;
	}
}