package shattered.lib.json;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import shattered.lib.FastNamedObjectMap;

public final class JDBTable extends JDBCollection implements IKeyValueStore {

	private final FastNamedObjectMap<Object> data = new FastNamedObjectMap<>();

	@Override
	public void set(@NotNull final String name, final boolean value) {
		this.data.put(name, value);
	}

	@Override
	public void set(@NotNull final String name, final byte value) {
		this.data.put(name, (double) value);
	}

	@Override
	public void set(@NotNull final String name, final short value) {
		this.data.put(name, (double) value);
	}

	@Override
	public void set(@NotNull final String name, final int value) {
		this.data.put(name, (double) value);
	}

	@Override
	public void set(@NotNull final String name, final float value) {
		this.data.put(name, (double) value);
	}

	@Override
	public void set(@NotNull final String name, final double value) {
		this.data.put(name, value);
	}

	@Override
	public void set(@NotNull final String name, final long value) {
		this.data.put(name, (double) value);
	}

	@Override
	public void set(@NotNull final String name, final char value) {
		this.data.put(name, String.valueOf(value));
	}

	@Override
	public void set(@NotNull final String name, @NotNull final String value) {
		this.data.put(name, value);
	}

	@Override
	public void set(@NotNull final String name, @NotNull final JDBTable value) {
		this.data.put(name, value);
	}

	@Override
	public void set(@NotNull final String name, @NotNull final JDBArray value) {
		this.data.put(name, value);
	}

	@Override
	public void set(@NotNull final String name, @NotNull final JDBCollection value) {
		if (value instanceof JDBTable) {
			this.set(name, (JDBTable) value);
		} else if (value instanceof JDBArray) {
			this.set(name, (JDBArray) value);
		} else {
			//Shouldn't happen since JDBTable and JDBArray are the only classes that extend JDBCollection
			// and since the constructor of JDBCollection is package-private, no other class can extend it
			throw new IllegalArgumentException();
		}
	}

	@Override
	public JDBTable newTable(@NotNull final String name) {
		final JDBTable result = new JDBTable();
		this.set(name, result);
		return result;
	}

	@Override
	public JDBArray newArray(@NotNull final String name) {
		final JDBArray result = new JDBArray();
		this.set(name, result);
		return result;
	}

	@Override
	public boolean hasBoolean(@NotNull final String name) {
		return this.hasKey(name, JDBKeyTypes.BOOLEAN);
	}

	@Override
	public boolean hasByte(@NotNull final String name) {
		return this.hasKey(name, JDBKeyTypes.BYTE);
	}

	@Override
	public boolean hasShort(@NotNull final String name) {
		return this.hasKey(name, JDBKeyTypes.SHORT);
	}

	@Override
	public boolean hasInteger(@NotNull final String name) {
		return this.hasKey(name, JDBKeyTypes.INTEGER);
	}

	@Override
	public boolean hasFloat(@NotNull final String name) {
		return this.hasKey(name, JDBKeyTypes.FLOAT);
	}

	@Override
	public boolean hasDouble(@NotNull final String name) {
		return this.hasKey(name, JDBKeyTypes.DOUBLE);
	}

	@Override
	public boolean hasLong(@NotNull final String name) {
		return this.hasKey(name, JDBKeyTypes.LONG);
	}

	@Override
	public boolean hasCharacter(@NotNull final String name) {
		return this.hasKey(name, JDBKeyTypes.CHARACTER);
	}

	@Override
	public boolean hasString(@NotNull final String name) {
		return this.hasKey(name, JDBKeyTypes.STRING);
	}

	@Override
	public boolean hasTable(@NotNull final String name) {
		return this.hasKey(name, JDBKeyTypes.TABLE);
	}

	@Override
	public boolean hasArray(@NotNull final String name) {
		return this.hasKey(name, JDBKeyTypes.ARRAY);
	}

	@Override
	public boolean hasCollection(@NotNull final String name) {
		return this.data.get(name) instanceof JDBCollection;
	}

	@Override
	public boolean hasKey(@NotNull final String name) {
		return this.data.get(name) != null;
	}

	@Override
	public boolean hasKey(@NotNull final String name, @NotNull final JDBKeyTypes type) {
		final Object result = this.data.get(name);
		return result != null && type.matchesObject(result);
	}

	@Override
	public boolean getBoolean(@NotNull final String name) {
		final Object result = this.getKey(name, JDBKeyTypes.BOOLEAN);
		return result != null && (boolean) result;
	}

	@Override
	public byte getByte(@NotNull final String name) {
		final Object result = this.getKey(name, JDBKeyTypes.BYTE);
		return result != null ? ((Double) result).byteValue() : 0;
	}

	@Override
	public short getShort(@NotNull final String name) {
		final Object result = this.getKey(name, JDBKeyTypes.SHORT);
		return result != null ? ((Double) result).shortValue() : 0;
	}

	@Override
	public int getInteger(@NotNull final String name) {
		final Object result = this.getKey(name, JDBKeyTypes.INTEGER);
		return result != null ? ((Double) result).intValue() : 0;
	}

	@Override
	public float getFloat(@NotNull final String name) {
		final Object result = this.getKey(name, JDBKeyTypes.FLOAT);
		return result != null ? ((Double) result).floatValue() : 0;
	}

	@Override
	public double getDouble(@NotNull final String name) {
		final Object result = this.getKey(name, JDBKeyTypes.DOUBLE);
		return result != null ? (double) result : 0;
	}

	@Override
	public long getLong(@NotNull final String name) {
		final Object result = this.getKey(name, JDBKeyTypes.LONG);
		return result != null ? ((Double) result).longValue() : 0;
	}

	@Override
	public char getCharacter(@NotNull final String name) {
		final Object result = this.getKey(name, JDBKeyTypes.CHARACTER);
		return result != null ? ((String) result).charAt(0) : (char) 0;
	}

	@Override
	@Nullable
	public String getString(@NotNull final String name) {
		return (String) this.getKey(name, JDBKeyTypes.STRING);
	}

	@Override
	@Nullable
	public JDBTable getTable(@NotNull final String name) {
		return (JDBTable) this.getKey(name, JDBKeyTypes.TABLE);
	}

	@Override
	@Nullable
	public JDBArray getArray(@NotNull final String name) {
		return (JDBArray) this.getKey(name, JDBKeyTypes.ARRAY);
	}

	@Override
	@Nullable
	public JDBCollection getCollection(@NotNull final String name) {
		final Object result = this.getKey(name);
		return result instanceof JDBCollection ? (JDBCollection) result : null;
	}

	@Override
	@Nullable
	public Object getKey(@NotNull final String name, @NotNull final JDBKeyTypes type) {
		final Object result = this.getKey(name);
		return result != null && type.matchesObject(result) ? result : null;
	}

	@Override
	@Nullable
	public Object getKey(@NotNull final String name) {
		return this.data.get(name);
	}

	@Override
	public void remove(@NotNull final String name) {
		this.data.remove(name);
	}

	@Override
	public String[] getKeyNames() {
		return this.data.keySet().toArray(new String[0]);
	}

	@Override
	public void clear() {
		this.data.clear();
	}
}