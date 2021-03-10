package shattered.core.sdb;

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
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import shattered.Shattered;
import org.jetbrains.annotations.NotNull;

public final class SDBHelper {

	private static final int VERSION = 1;

	private SDBHelper() {
	}

	private static void writeData(@NotNull final SDBTable data, @NotNull final DataOutput output) throws IOException {
		output.writeInt(SDBHelper.VERSION);
		output.writeByte(SDBTypes.TABLE.getId());
		data.serialize(output);
	}

	public static void serialize(@NotNull final SDBTable data, @NotNull final OutputStream output) throws IOException {
		try (final DataOutputStream out = new DataOutputStream(new BufferedOutputStream(new GZIPOutputStream(output)))) {
			SDBHelper.writeData(data, out);
		}
	}

	public static void serialize(@NotNull final SDBTable data, @NotNull final File file) throws IOException {
		SDBHelper.serialize(data, new FileOutputStream(file));
	}

	public static void serializeUncompressed(@NotNull final SDBTable data, @NotNull final OutputStream output) throws IOException {
		try (final DataOutputStream out = new DataOutputStream(new BufferedOutputStream(output))) {
			SDBHelper.writeData(data, out);
		}
	}

	private static void readData(@NotNull final SDBTable data, @NotNull final DataInput input) throws IOException {
		final int version = input.readInt();
		if (version > SDBHelper.VERSION) {
			Shattered.LOGGER.warn("Trying to read SDB with a version higher as the current version!");
			Shattered.LOGGER.warn("\tNOT reading since this can cause unknown side effects!");
			return;
		}
		if (input.readByte() != SDBTypes.TABLE.getId()) {
			Shattered.LOGGER.warn("Trying to read SDB with a wrong header type!");
			Shattered.LOGGER.warn("\tNOT reading since this can cause unknown side effects!");
			Shattered.LOGGER.warn("\tAre you sure you are reading the correct input?");
			return;
		}
		data.clear();
		data.read(input);
	}

	@NotNull
	public static SDBTable deserialize(@NotNull final InputStream input) throws IOException {
		try (final DataInputStream in = new DataInputStream(new BufferedInputStream(new GZIPInputStream(input)))) {
			final SDBTable data = new SDBTable();
			SDBHelper.readData(data, in);
			return data;
		}
	}

	@NotNull
	public static SDBTable deserialize(@NotNull final File file) throws IOException {
		return SDBHelper.deserialize(new FileInputStream(file));
	}

	@NotNull
	public static SDBTable deserializeUncompressed(@NotNull final InputStream input) throws IOException {
		try (final DataInputStream in = new DataInputStream(new BufferedInputStream(input))) {
			final SDBTable data = new SDBTable();
			SDBHelper.readData(data, in);
			return data;
		}
	}
}