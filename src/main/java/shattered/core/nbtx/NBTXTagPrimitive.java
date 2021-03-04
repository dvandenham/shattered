package shattered.core.nbtx;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Objects;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public abstract class NBTXTagPrimitive<T> extends NBTXTag {

	protected T value;

	private NBTXTagPrimitive(@NotNull final NBTXTypes type) {
		super(type);
	}

	public final T get() {
		return this.value;
	}

	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(), this.value);
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
		final NBTXTagPrimitive<?> other = (NBTXTagPrimitive<?>) o;
		return Objects.equals(this.value, other.value);
	}

	static final class TagBoolean extends NBTXTagPrimitive<Boolean> {

		TagBoolean() {
			super(NBTXTypes.BOOLEAN);
		}

		@Override
		void serialize(@NotNull final DataOutput output) throws IOException {
			output.writeBoolean(this.value);
		}

		@Override
		void read(@NotNull final DataInput input) throws IOException {
			this.value = input.readBoolean();
		}
	}

	static final class TagByte extends NBTXTagPrimitive<Byte> {

		TagByte() {
			super(NBTXTypes.BYTE);
		}

		@Override
		void serialize(@NotNull final DataOutput output) throws IOException {
			output.writeByte(this.value);
		}

		@Override
		void read(@NotNull final DataInput input) throws IOException {
			this.value = input.readByte();
		}
	}

	static final class TagShort extends NBTXTagPrimitive<Short> {

		TagShort() {
			super(NBTXTypes.SHORT);
		}

		@Override
		void serialize(@NotNull final DataOutput output) throws IOException {
			output.writeShort(this.value);
		}

		@Override
		void read(@NotNull final DataInput input) throws IOException {
			this.value = input.readShort();
		}
	}

	static final class TagInteger extends NBTXTagPrimitive<Integer> {

		TagInteger() {
			super(NBTXTypes.INTEGER);
		}

		@Override
		void serialize(@NotNull final DataOutput output) throws IOException {
			output.writeInt(this.value);
		}

		@Override
		void read(@NotNull final DataInput input) throws IOException {
			this.value = input.readInt();
		}
	}

	static final class TagLong extends NBTXTagPrimitive<Long> {

		TagLong() {
			super(NBTXTypes.LONG);
		}

		@Override
		void serialize(@NotNull final DataOutput output) throws IOException {
			output.writeLong(this.value);
		}

		@Override
		void read(@NotNull final DataInput input) throws IOException {
			this.value = input.readLong();
		}
	}

	static final class TagFloat extends NBTXTagPrimitive<Float> {

		TagFloat() {
			super(NBTXTypes.FLOAT);
		}

		@Override
		void serialize(@NotNull final DataOutput output) throws IOException {
			output.writeFloat(this.value);
		}

		@Override
		void read(@NotNull final DataInput input) throws IOException {
			this.value = input.readFloat();
		}
	}

	static final class TagDouble extends NBTXTagPrimitive<Double> {

		TagDouble() {
			super(NBTXTypes.DOUBLE);
		}

		@Override
		void serialize(@NotNull final DataOutput output) throws IOException {
			output.writeDouble(this.value);
		}

		@Override
		void read(@NotNull final DataInput input) throws IOException {
			this.value = input.readDouble();
		}
	}

	static final class TagCharacter extends NBTXTagPrimitive<Character> {

		TagCharacter() {
			super(NBTXTypes.CHARACTER);
		}

		@Override
		void serialize(@NotNull final DataOutput output) throws IOException {
			output.writeChar(this.value);
		}

		@Override
		void read(@NotNull final DataInput input) throws IOException {
			this.value = input.readChar();
		}
	}

	static final class TagString extends NBTXTagPrimitive<String> {

		TagString() {
			super(NBTXTypes.STRING);
		}

		@Override
		void serialize(@NotNull final DataOutput output) throws IOException {
			output.writeUTF(this.value);
		}

		@Override
		void read(@NotNull final DataInput input) throws IOException {
			this.value = input.readUTF();
		}
	}
}