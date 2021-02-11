package shattered.lib.gfx;

import org.jetbrains.annotations.NotNull;
import org.joml.Vector2f;
import shattered.lib.Color;
import shattered.lib.ResourceLocation;
import shattered.lib.math.Dimension;
import shattered.lib.math.Point;
import shattered.lib.math.Rectangle;

public interface Tessellator {
	
	default void drawQuick(@NotNull final Point position, @NotNull final ResourceLocation resource) {
		this.start();
		this.set(position, resource);
		this.draw();
	}

	default void drawQuick(final int x, final int y, @NotNull final ResourceLocation resource) {
		this.start();
		this.set(x, y, resource);
		this.draw();
	}

	default void drawQuick(@NotNull final Rectangle bounds, @NotNull final ResourceLocation resource) {
		this.start();
		this.set(bounds, resource);
		this.draw();
	}

	default void drawQuick(@NotNull final Point position, @NotNull final Dimension size, @NotNull final ResourceLocation resource) {
		this.start();
		this.set(position, size, resource);
		this.draw();
	}

	default void drawQuick(@NotNull final Point position, final int width, final int height, @NotNull final ResourceLocation resource) {
		this.start();
		this.set(position, width, height, resource);
		this.draw();
	}

	default void drawQuick(final int x, final int y, @NotNull final Dimension size, @NotNull final ResourceLocation resource) {
		this.start();
		this.set(x, y, size, resource);
		this.draw();
	}

	default void drawQuick(final int x, final int y, final int width, final int height, @NotNull final ResourceLocation resource) {
		this.start();
		this.set(x, y, width, height, resource);
		this.draw();
	}

	default void drawQuick(@NotNull final Rectangle bounds, @NotNull final Color color) {
		this.start();
		this.set(bounds, color);
		this.draw();
	}

	default void drawQuick(@NotNull final Point position, @NotNull final Dimension size, @NotNull final Color color) {
		this.start();
		this.set(position, size, color);
		this.draw();
	}

	default void drawQuick(@NotNull final Point position, final int width, final int height, @NotNull final Color color) {
		this.start();
		this.set(position, width, height, color);
		this.draw();
	}

	default void drawQuick(final int x, final int y, @NotNull final Dimension size, @NotNull final Color color) {
		this.start();
		this.set(x, y, size, color);
		this.draw();
	}

	default void drawQuick(final int x, final int y, final int width, final int height, @NotNull final Color color) {
		this.start();
		this.set(x, y, width, height, color);
		this.draw();
	}

	void start();

	void set(@NotNull Point position, @NotNull ResourceLocation resource);

	void set(int x, int y, @NotNull ResourceLocation resource);

	void set(@NotNull Rectangle bounds, @NotNull ResourceLocation resource);

	void set(@NotNull Point position, @NotNull Dimension size, @NotNull ResourceLocation resource);

	void set(@NotNull Point position, int width, int height, @NotNull ResourceLocation resource);

	void set(int x, int y, @NotNull Dimension size, @NotNull ResourceLocation resource);

	void set(int x, int y, int width, int height, @NotNull ResourceLocation resource);

	void set(@NotNull Rectangle bounds, @NotNull Color color);

	void set(@NotNull Point position, @NotNull Dimension size, @NotNull Color color);

	void set(@NotNull Point position, int width, int height, @NotNull Color color);

	void set(int x, int y, @NotNull Dimension size, @NotNull Color color);

	void set(int x, int y, int width, int height, @NotNull Color color);

	void setLocation(@NotNull Point position);

	void setLocation(int x, int y);

	void move(@NotNull Dimension amount);

	void move(int amountX, int amountY);

	void scale(@NotNull Vector2f scale);

	void scale(float scaleX, float scaleY);

	void centerX(int width);

	void centerY(int height);

	void center(@NotNull Dimension size);

	void center(int width, int height);

	void uMin(int uMin);

	void vMin(int vMin);

	void uMax(int uMax);

	void vMax(int vMax);

	void uv(@NotNull Rectangle uv);

	void translate(@NotNull Point amount);

	void translate(int x, int y);

	void translateToCenter(@NotNull Dimension size);

	void translateToCenter(int width, int height);

	void mirrorX();

	void mirrorY();

	void mirror();

	void rotate(float degrees);

	void color(@NotNull Color color);

	void colorTop(@NotNull Color color);

	void colorBottom(@NotNull Color color);

	void colorLeft(@NotNull Color color);

	void colorRight(@NotNull Color color);

	void colorTopLeft(@NotNull Color color);

	void colorTopRight(@NotNull Color color);

	void colorBottomLeft(@NotNull Color color);

	void colorBottomRight(@NotNull Color color);

	void pushMatrix();

	void popMatrix();

	void enableSmoothing();

	void disableSmoothing();

	void next();

	void draw();

	boolean isDrawing();
}
