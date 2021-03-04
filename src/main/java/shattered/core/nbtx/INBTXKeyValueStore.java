package shattered.core.nbtx;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import shattered.lib.math.Dimension;
import shattered.lib.math.Point;
import shattered.lib.math.Rectangle;

@SuppressWarnings("unused")
interface INBTXKeyValueStore {

	void set(@NotNull String name, boolean value);

	void set(@NotNull String name, byte value);

	void set(@NotNull String name, short value);

	void set(@NotNull String name, int value);

	void set(@NotNull String name, long value);

	void set(@NotNull String name, float value);

	void set(@NotNull String name, double value);

	void set(@NotNull String name, char value);

	void set(@NotNull String name, @NotNull String value);

	void set(@NotNull String name, @NotNull Point value);

	void set(@NotNull String name, @NotNull Dimension value);

	void set(@NotNull String name, @NotNull Rectangle value);

	@NotNull
	NBTXTagArray newArray(@NotNull String name);

	@NotNull
	NBTXTagTable newTable(@NotNull String name);

	void setTag(@NotNull String name, @NotNull NBTXTag tag);

	boolean hasBoolean(@NotNull String name);

	boolean hasByte(@NotNull String name);

	boolean hasShort(@NotNull String name);

	boolean hasInteger(@NotNull String name);

	boolean hasLong(@NotNull String name);

	boolean hasFloat(@NotNull String name);

	boolean hasDouble(@NotNull String name);

	boolean hasCharacter(@NotNull String name);

	boolean hasString(@NotNull String name);

	boolean hasPoint(@NotNull String name);

	boolean hasDimension(@NotNull String name);

	boolean hasRectangle(@NotNull String name);

	boolean hasArray(@NotNull String name);

	boolean hasTable(@NotNull String name);

	boolean hasTag(@NotNull String name);

	boolean hasTag(@NotNull String name, @NotNull NBTXTypes type);

	boolean getBoolean(@NotNull String name);

	byte getByte(@NotNull String name);

	short getShort(@NotNull String name);

	int getInteger(@NotNull String name);

	long getLong(@NotNull String name);

	float getFloat(@NotNull String name);

	double getDouble(@NotNull String name);

	char getCharacter(@NotNull String name);

	@Nullable
	String getString(@NotNull String name);

	@Nullable
	Point getPoint(@NotNull String name);

	@Nullable
	Dimension getDimension(@NotNull String name);

	@Nullable
	Rectangle getRectangle(@NotNull String name);

	@Nullable
	NBTXTagArray getArray(@NotNull String name);

	@Nullable
	NBTXTagTable getTable(@NotNull String name);

	@Nullable
	NBTXTag getTag(@NotNull String name);

	void removeKey(@NotNull String name);

	@NotNull
	String[] getKeyNames();

	void clear();
}