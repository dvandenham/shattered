package shattered.lib.gfx;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import shattered.lib.Color;
import shattered.lib.math.Point;

public interface PolygonBuilder {

	void start(@Nullable Color mainColor);

	void add(int x, int y);

	void add(int x, int y, @Nullable Color pointColor);

	void add(@NotNull Point position);

	void add(@NotNull Point position, @Nullable Color pointColor);

	void draw();

	boolean isBuilding();
}