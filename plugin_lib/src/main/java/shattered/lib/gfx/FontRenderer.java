package shattered.lib.gfx;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import shattered.lib.Color;
import shattered.lib.ResourceLocation;
import shattered.lib.math.Dimension;
import shattered.lib.math.Point;
import shattered.lib.math.Rectangle;

public interface FontRenderer {

	void setFont(@NotNull ResourceLocation font);

	void resetFont();

	void setFontSize(int size);

	void revertFontSize();

	default void writeQuick(@NotNull final Point position, @NotNull final StringData data) {
		this.start();
		this.add(position, data);
		this.write();
	}

	default void writeQuick(final int x, final int y, @NotNull final StringData data) {
		this.start();
		this.add(x, y, data);
		this.write();
	}

	default void writeQuick(@NotNull final Point position, @NotNull final String text, @NotNull final Color color) {
		this.start();
		this.add(position, text, color);
		this.write();
	}

	default void writeQuick(final int x, final int y, @NotNull final String text, @NotNull final Color color) {
		this.start();
		this.add(x, y, text, color);
		this.write();
	}

	default void writeQuick(@NotNull final Point position, @NotNull final String text) {
		this.start();
		this.add(position, text);
		this.write();
	}

	default void writeQuick(final int x, final int y, @NotNull final String text) {
		this.start();
		this.add(x, y, text);
		this.write();
	}

	default void writeQuickCentered(@NotNull final Rectangle bounds, @NotNull final StringData data) {
		this.start();
		this.addCentered(bounds, data);
		this.write();
	}

	default void writeQuickCentered(@NotNull final Point position, @NotNull final Dimension centerSize, @NotNull final StringData data) {
		this.start();
		this.addCentered(position, centerSize, data);
		this.write();
	}

	default void writeQuickCentered(final int x, final int y, @NotNull final Dimension centerSize, @NotNull final StringData data) {
		this.start();
		this.addCentered(x, y, centerSize, data);
		this.write();
	}

	default void writeQuickCentered(@NotNull final Point position, final int centerWidth, final int centerHeight, @NotNull final StringData data) {
		this.start();
		this.addCentered(position, centerWidth, centerHeight, data);
		this.write();
	}

	default void writeQuickCentered(final int x, final int y, final int centerWidth, final int centerHeight, @NotNull final StringData data) {
		this.start();
		this.addCentered(x, y, centerWidth, centerHeight, data);
		this.write();
	}

	default void writeQuickCentered(@NotNull final Rectangle bounds, @NotNull final String text, @NotNull final Color color) {
		this.start();
		this.addCentered(bounds, text, color);
		this.write();
	}

	default void writeQuickCentered(@NotNull final Point position, @NotNull final Dimension centerSize, @NotNull final String text, @NotNull final Color color) {
		this.start();
		this.addCentered(position, centerSize, text, color);
		this.write();
	}

	default void writeQuickCentered(final int x, final int y, @NotNull final Dimension centerSize, @NotNull final String text, @NotNull final Color color) {
		this.start();
		this.addCentered(x, y, centerSize, text, color);
		this.write();
	}

	default void writeQuickCentered(@NotNull final Point position, final int centerWidth, final int centerHeight, @NotNull final String text, @NotNull final Color color) {
		this.start();
		this.addCentered(position, centerWidth, centerHeight, text, color);
		this.write();
	}

	default void writeQuickCentered(final int x, final int y, final int centerWidth, final int centerHeight, @NotNull final String text, @NotNull final Color color) {
		this.start();
		this.addCentered(x, y, centerWidth, centerHeight, text, color);
		this.write();
	}

	default void writeQuickCentered(@NotNull final Rectangle bounds, @NotNull final String text) {
		this.start();
		this.addCentered(bounds, text);
		this.write();
	}

	default void writeQuickCentered(@NotNull final Point position, @NotNull final Dimension centerSize, @NotNull final String text) {
		this.start();
		this.addCentered(position, centerSize, text);
		this.write();
	}

	default void writeQuickCentered(final int x, final int y, final Dimension centerSize, @NotNull final String text) {
		this.start();
		this.addCentered(x, y, centerSize, text);
		this.write();
	}

	default void writeQuickCentered(@NotNull final Point position, final int centerWidth, final int centerHeight, @NotNull final String text) {
		this.start();
		this.addCentered(position, centerWidth, centerHeight, text);
		this.write();
	}

	default void writeQuickCentered(final int x, final int y, final int centerWidth, final int centerHeight, @NotNull final String text) {
		this.start();
		this.addCentered(x, y, centerWidth, centerHeight, text);
		this.write();
	}

	void start();

	void add(@NotNull Point position, @NotNull StringData data);

	void add(int x, int y, @NotNull StringData data);

	void add(@NotNull Point position, @NotNull String text, @NotNull Color color);

	void add(int x, int y, @NotNull String text, @NotNull Color color);

	void add(@NotNull Point position, @NotNull String text);

	void add(int x, int y, @NotNull String text);

	void addCentered(@NotNull Rectangle bounds, @NotNull StringData data);

	void addCentered(@NotNull Point position, @NotNull Dimension centerSize, @NotNull StringData data);

	void addCentered(int x, int y, @NotNull Dimension centerSize, @NotNull StringData data);

	void addCentered(@NotNull Point position, int centerWidth, int centerHeight, @NotNull StringData data);

	void addCentered(int x, int y, int centerWidth, int centerHeight, @NotNull StringData data);

	void addCentered(@NotNull Rectangle Bounds, @NotNull String text, @NotNull Color color);

	void addCentered(@NotNull Point position, @NotNull Dimension centerSize, @NotNull String text, @NotNull Color color);

	void addCentered(int x, int y, @NotNull Dimension centerSize, @NotNull String text, @NotNull Color color);

	void addCentered(@NotNull Point position, int centerWidth, int centerHeight, @NotNull String text, @NotNull Color color);

	void addCentered(int x, int y, int centerWidth, int centerHeight, @NotNull String text, @NotNull Color color);

	void addCentered(@NotNull Rectangle bounds, @NotNull String text);

	void addCentered(@NotNull Point position, @NotNull Dimension centerSize, @NotNull String text);

	void addCentered(int x, int y, @NotNull Dimension centerSize, @NotNull String text);

	void addCentered(@NotNull Point position, int centerWidth, int centerHeight, @NotNull String text);

	void addCentered(int x, int y, int centerWidth, int centerHeight, @NotNull String text);

	void write();

	boolean isWriting();

	int getWidth(@NotNull StringData data);

	int getWidth(@NotNull String text);

	int getHeight(@NotNull StringData data);

	int getHeight();

	int getTextRows(@NotNull StringData data);

	@Nullable
	String getWrappedText(@NotNull StringData data);
}