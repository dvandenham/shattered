package shattered;

import shattered.lib.ITimerListener;

public class Timer {

	private final ITimerListener listener;
	private final boolean repeating;
	private final int tickRate;
	private final int maxTicks;
	private final long tickLengthMillis;
	private int triggeredCount = 0;
	private long lastTickTime = 0;

	Timer(final ITimerListener listener, final int tps, final int maxTicks, final boolean repeating) {
		this.listener = listener;
		this.tickRate = tps;
		this.maxTicks = maxTicks;
		this.repeating = repeating;
		this.tickLengthMillis = 1000L / this.tickRate;
	}

	void tick() {
		final long delta = Shattered.getSystemTime() - this.lastTickTime;
		if (delta >= this.tickLengthMillis) {
			++this.triggeredCount;
			this.listener.onTimerTriggered(this);
			this.lastTickTime = Shattered.getSystemTime();
		}
	}

	public void start() {
		this.lastTickTime = Shattered.getSystemTime();
	}

	public boolean isRepeating() {
		return this.repeating;
	}

	public boolean isDone() {
		return !this.repeating && this.triggeredCount >= this.maxTicks;
	}

	public int getTickRate() {
		return this.tickRate;
	}

	public int getTriggeredCount() {
		return this.triggeredCount;
	}

	public double getScaledProgress(final double scale) {
		return scale / this.maxTicks * this.triggeredCount;
	}
}