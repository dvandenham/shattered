package shattered.core.nbtx;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Objects;
import org.jetbrains.annotations.NotNull;
import shattered.lib.math.Dimension;
import shattered.lib.math.Point;
import shattered.lib.math.Rectangle;

@SuppressWarnings("unused")
public abstract class NBTXTagWrappers<T> extends NBTXTag {

	protected T value;

	private NBTXTagWrappers(@NotNull final NBTXTypes type) {
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
		final NBTXTagWrappers<?> other = (NBTXTagWrappers<?>) o;
		return Objects.equals(this.value, other.value);
	}

	static final class TagPoint extends NBTXTagWrappers<Point> {

		TagPoint() {
			super(NBTXTypes.POINT);
		}

		@Override
		void serialize(@NotNull final DataOutput output) throws IOException {
			output.writeBoolean(this.value.isMutable());
			output.writeInt(this.value.getX());
			output.writeInt(this.value.getY());
		}

		@Override
		void read(@NotNull final DataInput input) throws IOException {
			this.value = input.readBoolean() ? Point.createMutable(input.readInt(), input.readInt()) : Point.create(input.readInt(), input.readInt());
		}
	}

	static final class TagDimension extends NBTXTagWrappers<Dimension> {

		TagDimension() {
			super(NBTXTypes.DIMENSION);
		}

		@Override
		void serialize(@NotNull final DataOutput output) throws IOException {
			output.writeBoolean(this.value.isMutable());
			output.writeInt(this.value.getWidth());
			output.writeInt(this.value.getHeight());
		}

		@Override
		void read(@NotNull final DataInput input) throws IOException {
			this.value = input.readBoolean() ? Dimension.createMutable(input.readInt(), input.readInt()) : Dimension.create(input.readInt(), input.readInt());
		}
	}

	static final class TagRectangle extends NBTXTagWrappers<Rectangle> {

		TagRectangle() {
			super(NBTXTypes.RECTANGLE);
		}

		@Override
		void serialize(@NotNull final DataOutput output) throws IOException {
			output.writeBoolean(this.value.isMutable());
			output.writeInt(this.value.getX());
			output.writeInt(this.value.getY());
			output.writeInt(this.value.getWidth());
			output.writeInt(this.value.getHeight());
		}

		@Override
		void read(@NotNull final DataInput input) throws IOException {
			final boolean Mutable = input.readBoolean();
			final int x = input.readInt();
			final int y = input.readInt();
			final int width = input.readInt();
			final int height = input.readInt();
			this.value = Mutable ? Rectangle.createMutable(x, y, width, height) : Rectangle.create(x, y, width, height);
		}
	}
}