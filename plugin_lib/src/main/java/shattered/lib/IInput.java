package shattered.lib;

import org.jetbrains.annotations.Nullable;
import shattered.lib.math.Rectangle;

public interface IInput {

	void restrictMouseInput(@Nullable final Rectangle bounds);

	void releaseMouseRestriction();

	boolean isMouseAllowed(final int x, final int y);

	int getMouseX();

	int getMouseY();

	boolean isMouseLeftPressed();

	boolean isMouseLeftClicked();

	boolean isMouseRightPressed();

	boolean isMouseRightClicked();

	boolean isMouseDragging();

	int getDraggedDX();

	int getDraggedDY();

	boolean isKeyDown(final int keyCode);

	boolean containsMouse(final int x, final int y, final int width, final int height);

	boolean containsMouse(@Nullable final Rectangle bounds);

	void setMouseBlocked(final boolean blocked);
}