package shattered.lib.audio;

import java.util.concurrent.atomic.AtomicBoolean;

abstract class IAudioThread extends Thread {

	AtomicBoolean running = new AtomicBoolean(false);

	IAudioThread(final String name) {
		super("SoundSystem (" + name + ')');
	}

	@Override
	public abstract void run();

	@Override
	public synchronized void start() {
		this.running.set(true);
		super.start();
	}

	void trySleep(final long millis) {
		try {
			Thread.sleep(millis);
		} catch (final InterruptedException ignored) {
		}
	}

	synchronized void wake() {
		synchronized (this) {
			this.interrupt();
		}
	}

	public void close() {
		this.running.set(false);
		this.wake();
	}
}