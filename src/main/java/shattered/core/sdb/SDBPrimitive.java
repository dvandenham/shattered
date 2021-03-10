package shattered.core.sdb;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Objects;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public abstract class SDBPrimitive<T> extends SDBTag {

	protected T value;

	private SDBPrimitive(@NotNull final SDBTypes type) {
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
		final SDBPrimitive<?> other = (SDBPrimitive<?>) o;
		return Objects.equals(this.value, other.value);
	}

	static final class TagBoolean extends SDBPrimitive<Boolean> {

		TagBoolean() {
			super(SDBTypes.BOOLEAN);
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

	static final class TagByte extends SDBPrimitive<Byte> {

		TagByte() {
			super(SDBTypes.BYTE);
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

	static final class TagShort extends SDBPrimitive<Short> {

		TagShort() {
			super(SDBTypes.SHORT);
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

	static final class TagInteger extends SDBPrimitive<Integer> {

		TagInteger() {
			super(SDBTypes.INTEGER);
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

	static final class TagLong extends SDBPrimitive<Long> {

		TagLong() {
			super(SDBTypes.LONG);
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

	static final class TagFloat extends SDBPrimitive<Float> {

		TagFloat() {
			super(SDBTypes.FLOAT);
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

	static final class TagDouble extends SDBPrimitive<Double> {

		TagDouble() {
			super(SDBTypes.DOUBLE);
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

	static final class TagCharacter extends SDBPrimitive<Character> {

		TagCharacter() {
			super(SDBTypes.CHARACTER);
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

	static final class TagString extends SDBPrimitive<String> {

		TagString() {
			super(SDBTypes.STRING);
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