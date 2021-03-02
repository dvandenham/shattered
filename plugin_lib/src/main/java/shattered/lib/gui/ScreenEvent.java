package shattered.lib.gui;

import org.jetbrains.annotations.NotNull;
import shattered.lib.event.Event;

public abstract class ScreenEvent extends Event<IGuiScreen> {

	private ScreenEvent(@NotNull final IGuiScreen screen) {
		super(screen);
	}

	public static class Opened extends ScreenEvent {

		Opened(@NotNull final IGuiScreen screen) {
			super(screen);
		}
	}

	public static class Closed extends ScreenEvent {

		Closed(@NotNull final IGuiScreen screen) {
			super(screen);
		}
	}

	public static class Opening extends ScreenEvent {

		Opening(@NotNull final IGuiScreen screen) {
			super(screen);
		}

		@Override
		public boolean isCancellable() {
			return true;
		}
	}

	public static class Closing extends ScreenEvent {

		Closing(@NotNull final IGuiScreen screen) {
			super(screen);
		}

		@Override
		public boolean isCancellable() {
			return true;
		}
	}
}