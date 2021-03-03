package shattered.lib;

import org.jetbrains.annotations.NotNull;
import shattered.Timer;

@FunctionalInterface
public interface ITimerListener {

	void onTimerTriggered(@NotNull Timer timer);
}