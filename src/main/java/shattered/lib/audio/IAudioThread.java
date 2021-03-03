package shattered.lib.audio;

abstract class IAudioThread extends Thread {

	volatile boolean shouldClose = false;

	IAudioThread(final String name) {
		super("SoundSystem (" + name + ')');
	}

	@Override
	public abstract void run();

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
		this.shouldClose = true;
		this.wake();
	}
}