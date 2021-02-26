package shattered.lib.json;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.regex.Pattern;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class JDB implements IKeyValueStore {

	static final int VERSION = 1;
	static final String HEADER = String.format("<JDB>%s</JDB>", JDB.VERSION);
	static final Pattern PATTERN = Pattern.compile("<JDB>([\\s\\S]*?)</JDB>");
	final JDBTable data;

	JDB(final JDBTable data) {
		this.data = data;
	}

	public JDB() {
		this(new JDBTable());
	}

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
	public void set(@NotNull final String name, final float value) {
		this.data.set(name, value);
	}

	@Override
	public void set(@NotNull final String name, final double value) {
		this.data.set(name, value);
	}

	@Override
	public void set(@NotNull final String name, final long value) {
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
	public void set(@NotNull final String name, @NotNull final JDBTable value) {
		this.data.set(name, value);
	}

	@Override
	public void set(@NotNull final String name, @NotNull final JDBArray value) {
		this.data.set(name, value);
	}

	@Override
	public void set(@NotNull final String name, @NotNull final JDBCollection value) {
		this.data.set(name, value);
	}

	@Override
	public JDBTable newTable(@NotNull final String name) {
		return this.data.newTable(name);
	}

	@Override
	public JDBArray newArray(@NotNull final String name) {
		return this.data.newArray(name);
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
	public boolean hasFloat(@NotNull final String name) {
		return this.data.hasFloat(name);
	}

	@Override
	public boolean hasDouble(@NotNull final String name) {
		return this.data.hasDouble(name);
	}

	@Override
	public boolean hasLong(@NotNull final String name) {
		return this.data.hasLong(name);
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
	public boolean hasTable(@NotNull final String name) {
		return this.data.hasTable(name);
	}

	@Override
	public boolean hasArray(@NotNull final String name) {
		return this.data.hasArray(name);
	}

	@Override
	public boolean hasCollection(@NotNull final String name) {
		return this.data.hasCollection(name);
	}

	@Override
	public boolean hasKey(@NotNull final String name, @NotNull final JDBKeyTypes type) {
		return this.data.hasKey(name, type);
	}

	@Override
	public boolean hasKey(@NotNull final String name) {
		return this.data.hasKey(name);
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
	public float getFloat(@NotNull final String name) {
		return this.data.getFloat(name);
	}

	@Override
	public double getDouble(@NotNull final String name) {
		return this.data.getDouble(name);
	}

	@Override
	public long getLong(@NotNull final String name) {
		return this.data.getLong(name);
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
	@Nullable
	public JDBTable getTable(@NotNull final String name) {
		return this.data.getTable(name);
	}

	@Override
	@Nullable
	public JDBArray getArray(@NotNull final String name) {
		return this.data.getArray(name);
	}

	@Override
	public @Nullable JDBCollection getCollection(@NotNull final String name) {
		return this.data.getCollection(name);
	}

	@Override
	@Nullable
	public Object getKey(@NotNull final String name, @NotNull final JDBKeyTypes type) {
		return this.data.getKey(name, type);
	}

	@Override
	@Nullable
	public Object getKey(@NotNull final String name) {
		return this.data.getKey(name);
	}

	@Override
	public void remove(@NotNull final String name) {
		this.data.remove(name);
	}

	@Override
	public String[] getKeyNames() {
		return this.data.getKeyNames();
	}

	@Override
	public void clear() {
		this.data.clear();
	}

	public void save(@NotNull final Writer writer) throws IOException {
		JDBSerializers.serialize(this, writer);
	}

	public void saveSilently(@NotNull final Writer writer) {
		try {
			this.save(writer);
		} catch (final IOException ignored) {
		}
	}

	public void save(@NotNull final OutputStream output) throws IOException {
		this.save(new OutputStreamWriter(output));
	}

	public void saveSilently(@NotNull final OutputStream output) {
		try {
			this.save(output);
		} catch (final IOException ignored) {
		}
	}

	public void save(@NotNull final File file) throws IOException {
		this.save(new FileOutputStream(file));
	}

	public void saveSilently(@NotNull final File file) {
		try {
			this.save(file);
		} catch (final IOException ignored) {
		}
	}

	public static JDB read(@NotNull final Reader reader) throws IOException {
		return JDBSerializers.deserialize(reader);
	}

	public static JDB readSilently(@NotNull final Reader reader) {
		try {
			return JDB.read(reader);
		} catch (final IOException ignored) {
			return new JDB();
		}
	}

	public static JDB read(@NotNull final InputStream input) throws IOException {
		return JDB.read(new InputStreamReader(input));
	}

	public static JDB readSilently(@NotNull final InputStream input) {
		try {
			return JDB.read(input);
		} catch (final IOException ignored) {
			return new JDB();
		}
	}

	public static JDB read(@NotNull final File file) throws IOException {
		return JDB.read(new FileInputStream(file));
	}

	public static JDB readSilently(@NotNull final File file) {
		try {
			return JDB.read(file);
		} catch (final IOException ignored) {
			return new JDB();
		}
	}
}