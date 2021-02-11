package shattered.lib.gui;

import org.jetbrains.annotations.NotNull;

public abstract class Layout {

	public abstract void setInverted();

	public abstract void startRow();

	public abstract void startRow(int maxSize);

	public abstract void stopRow();

	public abstract void add(@NotNull IGuiComponent... components);

	public abstract void addEmptyRow();

	public abstract int getComponentHeight();

	public abstract int getComponentSpacing();

	@NotNull
	public abstract Layout recreate(int componentHeight, int componentSpacing, int x, int y, int width, int height);

	@NotNull
	public abstract Layout recreate(int componentHeight, int componentSpacing);

	@NotNull
	public abstract Layout recreate();
}