package shattered.core.nbtx;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Objects;
import org.jetbrains.annotations.NotNull;

public abstract class NBTXTag {

	final NBTXTypes type;

	NBTXTag(@NotNull final NBTXTypes type) {
		this.type = type;
	}

	abstract void serialize(@NotNull DataOutput output) throws IOException;

	abstract void read(@NotNull DataInput input) throws IOException;

	@Override
	public int hashCode() {
		return Objects.hash(this.type);
	}

	@Override
	public boolean equals(final Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || this.getClass() != o.getClass()) {
			return false;
		}
		final NBTXTag other = (NBTXTag) o;
		return this.type == other.type;
	}
}