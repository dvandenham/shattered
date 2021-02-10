package shattered.lib.json;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class JDBArray extends JDBCollection {

	private final ObjectArrayList<Object> data = new ObjectArrayList<>();

	public void add(final boolean value) {
		this.data.add(value);
	}

	public void add(final byte value) {
		this.data.add((double) value);
	}

	public void add(final short value) {
		this.data.add((double) value);
	}

	public void add(final int value) {
		this.data.add((double) value);
	}

	public void add(final float value) {
		this.data.add((double) value);
	}

	public void add(final double value) {
		this.data.add(value);
	}

	public void add(final long value) {
		this.data.add((double) value);
	}

	public void add(final char value) {
		this.data.add(String.valueOf(value));
	}

	public void add(@NotNull final String value) {
		this.data.add(value);
	}

	public void add(@NotNull final JDBTable value) {
		this.data.add(value);
	}

	public void add(@NotNull final JDBArray value) {
		this.data.add(value);
	}

	public void add(@NotNull final JDBCollection value) {
		if (value instanceof JDBTable) {
			this.add((JDBTable) value);
		} else if (value instanceof JDBArray) {
			this.add((JDBArray) value);
		} else {
			//Shouldn't happen since JDBTable and JDBArray are the only classes that extend JDBCollection
			// and since the constructor of JDBCollection is package-private, no other class can extend it
			throw new IllegalArgumentException();
		}
	}

	public JDBTable addTable() {
		final JDBTable result = new JDBTable();
		this.add(result);
		return result;
	}

	public JDBArray addArray() {
		final JDBArray result = new JDBArray();
		this.add(result);
		return result;
	}

	public void set(final int index, final boolean value) {
		if (index < 0 || index >= this.data.size()) {
			throw new IndexOutOfBoundsException();
		}
		this.data.set(index, value);
	}

	public void set(final int index, final byte value) {
		if (index < 0 || index >= this.data.size()) {
			throw new IndexOutOfBoundsException();
		}
		this.data.set(index, (double) value);
	}

	public void set(final int index, final short value) {
		if (index < 0 || index >= this.data.size()) {
			throw new IndexOutOfBoundsException();
		}
		this.data.set(index, (double) value);
	}

	public void set(final int index, final int value) {
		if (index < 0 || index >= this.data.size()) {
			throw new IndexOutOfBoundsException();
		}
		this.data.set(index, (double) value);
	}

	public void set(final int index, final float value) {
		if (index < 0 || index >= this.data.size()) {
			throw new IndexOutOfBoundsException();
		}
		this.data.set(index, (double) value);
	}

	public void set(final int index, final double value) {
		if (index < 0 || index >= this.data.size()) {
			throw new IndexOutOfBoundsException();
		}
		this.data.set(index, value);
	}

	public void set(final int index, final long value) {
		if (index < 0 || index >= this.data.size()) {
			throw new IndexOutOfBoundsException();
		}
		this.data.set(index, (double) value);
	}

	public void set(final int index, final char value) {
		if (index < 0 || index >= this.data.size()) {
			throw new IndexOutOfBoundsException();
		}
		this.data.set(index, String.valueOf(value));
	}

	public void set(final int index, @NotNull final String value) {
		if (index < 0 || index >= this.data.size()) {
			throw new IndexOutOfBoundsException();
		}
		this.data.set(index, value);
	}

	public void set(final int index, @NotNull final JDBTable value) {
		if (index < 0 || index >= this.data.size()) {
			throw new IndexOutOfBoundsException();
		}
		this.data.set(index, value);
	}

	public void set(final int index, @NotNull final JDBArray value) {
		if (index < 0 || index >= this.data.size()) {
			throw new IndexOutOfBoundsException();
		}
		this.data.set(index, value);
	}

	public void set(final int index, @NotNull final JDBCollection value) {
		if (value instanceof JDBTable) {
			this.set(index, (JDBTable) value);
		} else if (value instanceof JDBArray) {
			this.set(index, (JDBArray) value);
		} else {
			//Shouldn't happen since JDBTable and JDBArray are the only classes that extend JDBCollection
			// and since the constructor of JDBCollection is package-private, no other class can extend it
			throw new IllegalArgumentException();
		}
	}

	public JDBTable newTable(final int index) {
		if (index < 0 || index >= this.data.size()) {
			throw new IndexOutOfBoundsException();
		}
		final JDBTable result = new JDBTable();
		this.set(index, result);
		return result;
	}

	public JDBArray newArray(final int index) {
		if (index < 0 || index >= this.data.size()) {
			throw new IndexOutOfBoundsException();
		}
		final JDBArray result = new JDBArray();
		this.set(index, result);
		return result;
	}

	public boolean hasBoolean(final int index) {
		return this.hasKey(index, JDBKeyTypes.BOOLEAN);
	}

	public boolean hasByte(final int index) {
		return this.hasKey(index, JDBKeyTypes.BYTE);
	}

	public boolean hasShort(final int index) {
		return this.hasKey(index, JDBKeyTypes.SHORT);
	}

	public boolean hasInteger(final int index) {
		return this.hasKey(index, JDBKeyTypes.INTEGER);
	}

	public boolean hasFloat(final int index) {
		return this.hasKey(index, JDBKeyTypes.FLOAT);
	}

	public boolean hasDouble(final int index) {
		return this.hasKey(index, JDBKeyTypes.DOUBLE);
	}

	public boolean hasLong(final int index) {
		return this.hasKey(index, JDBKeyTypes.LONG);
	}

	public boolean hasCharacter(final int index) {
		return this.hasKey(index, JDBKeyTypes.CHARACTER);
	}

	public boolean hasString(final int index) {
		return this.hasKey(index, JDBKeyTypes.STRING);
	}

	public boolean hasTable(final int index) {
		return this.hasKey(index, JDBKeyTypes.TABLE);
	}

	public boolean hasArray(final int index) {
		return this.hasKey(index, JDBKeyTypes.ARRAY);
	}

	public boolean hasCollection(final int index) {
		if (index < 0 || index >= this.data.size()) {
			throw new IndexOutOfBoundsException();
		}
		final Object result = this.data.get(index);
		return result instanceof JDBCollection;
	}

	public boolean hasKey(final int index, @NotNull final JDBKeyTypes type) {
		if (index < 0 || index >= this.data.size()) {
			throw new IndexOutOfBoundsException();
		}
		final Object result = this.data.get(index);
		return result != null && type.matchesObject(result);
	}

	public boolean getBoolean(final int index) {
		final Object result = this.getKey(index, JDBKeyTypes.BOOLEAN);
		return result != null && (boolean) result;
	}

	public byte getByte(final int index) {
		final Object result = this.getKey(index, JDBKeyTypes.BYTE);
		return result != null ? ((Double) result).byteValue() : 0;
	}

	public short getShort(final int index) {
		final Object result = this.getKey(index, JDBKeyTypes.SHORT);
		return result != null ? ((Double) result).shortValue() : 0;
	}

	public int getInteger(final int index) {
		final Object result = this.getKey(index, JDBKeyTypes.INTEGER);
		return result != null ? ((Double) result).intValue() : 0;
	}

	public float getFloat(final int index) {
		final Object result = this.getKey(index, JDBKeyTypes.FLOAT);
		return result != null ? ((Double) result).floatValue() : 0;
	}

	public double getDouble(final int index) {
		final Object result = this.getKey(index, JDBKeyTypes.DOUBLE);
		return result != null ? (double) result : 0;
	}

	public long getLong(final int index) {
		final Object result = this.getKey(index, JDBKeyTypes.LONG);
		return result != null ? ((Double) result).longValue() : 0;
	}

	public char getCharacter(final int index) {
		final Object result = this.getKey(index, JDBKeyTypes.CHARACTER);
		return result != null ? ((String) result).charAt(0) : (char) 0;
	}

	@Nullable
	public String getString(final int index) {
		return (String) this.getKey(index, JDBKeyTypes.STRING);
	}

	@Nullable
	public JDBTable getTable(final int index) {
		return (JDBTable) this.getKey(index, JDBKeyTypes.TABLE);
	}

	@Nullable
	public JDBArray getArray(final int index) {
		return (JDBArray) this.getKey(index, JDBKeyTypes.ARRAY);
	}

	@Nullable
	public JDBCollection getCollection(final int index) {
		if (index < 0 || index >= this.data.size()) {
			throw new IndexOutOfBoundsException();
		}
		final Object result = this.data.get(index);
		return result instanceof JDBCollection ? (JDBCollection) result : null;
	}

	@Nullable
	public Object getKey(final int index, @NotNull final JDBKeyTypes type) {
		final Object result = this.getKey(index);
		return result != null && type.matchesObject(result) ? result : null;
	}

	@Nullable
	public Object getKey(final int index) {
		if (index < 0 || index >= this.data.size()) {
			throw new IndexOutOfBoundsException();
		}
		return this.data.get(index);
	}

	public int getSize() {
		return this.data.size();
	}

	public void clear() {
		this.data.clear();
	}
}