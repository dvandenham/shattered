package shattered.lib.json;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

interface IKeyValueStore {

	void set(@NotNull String name, boolean value);

	void set(@NotNull String name, byte value);

	void set(@NotNull String name, short value);

	void set(@NotNull String name, int value);

	void set(@NotNull String name, float value);

	void set(@NotNull String name, double value);

	void set(@NotNull String name, long value);

	void set(@NotNull String name, char value);

	void set(@NotNull String name, @NotNull String value);

	void set(@NotNull String name, @NotNull JDBTable value);

	void set(@NotNull String name, @NotNull JDBArray value);

	void set(@NotNull String name, @NotNull JDBCollection value);

	JDBTable newTable(@NotNull String name);

	JDBArray newArray(@NotNull String name);

	boolean hasBoolean(@NotNull String name);

	boolean hasByte(@NotNull String name);

	boolean hasShort(@NotNull String name);

	boolean hasInteger(@NotNull String name);

	boolean hasFloat(@NotNull String name);

	boolean hasDouble(@NotNull String name);

	boolean hasLong(@NotNull String name);

	boolean hasCharacter(@NotNull String name);

	boolean hasString(@NotNull String name);

	boolean hasTable(@NotNull String name);

	boolean hasArray(@NotNull String name);

	boolean hasCollection(@NotNull String name);

	boolean hasKey(@NotNull final String name, @NotNull JDBKeyTypes type);

	boolean hasKey(@NotNull final String name);

	boolean getBoolean(@NotNull String name);

	byte getByte(@NotNull String name);

	short getShort(@NotNull String name);

	int getInteger(@NotNull String name);

	float getFloat(@NotNull String name);

	double getDouble(@NotNull String name);

	long getLong(@NotNull String name);

	char getCharacter(@NotNull String name);

	@Nullable
	String getString(@NotNull String name);

	@Nullable
	JDBTable getTable(@NotNull String name);

	@Nullable
	JDBArray getArray(@NotNull String name);

	@Nullable
	JDBCollection getCollection(@NotNull String name);

	@Nullable
	Object getKey(@NotNull String name, @NotNull JDBKeyTypes type);

	@Nullable
	Object getKey(@NotNull String name);

	void remove(@NotNull String name);

	String[] getKeyNames();

	void clear();
}