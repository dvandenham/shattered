package shattered.lib.audio;

import java.util.Objects;
import java.util.concurrent.ConcurrentLinkedQueue;
import shattered.lib.asset.Audio;
import org.jetbrains.annotations.NotNull;

final class AudioThreadController extends IAudioThread {

	static final ConcurrentLinkedQueue<QueueRequest> QUEUE = new ConcurrentLinkedQueue<>();
	static final byte REQUEST_PLAY = 0;
	static final byte REQUEST_PAUSE = 1;
	static final byte REQUEST_STOP = 2;

	AudioThreadController() {
		super("Controller");
	}

	@Override
	public void run() {
		while (this.running.get()) {
			QueueRequest request;
			while ((request = AudioThreadController.QUEUE.poll()) != null) {
				if (Objects.requireNonNull(request.audio).getSourcePointer() != -1) {
					switch (request.request) {
						case AudioThreadController.REQUEST_PLAY:
							this.play(request.audio, request.player);
							break;
						case AudioThreadController.REQUEST_PAUSE:
							this.pause(request.audio, request.player);
							break;
						case AudioThreadController.REQUEST_STOP:
							this.stop(request.audio, request.player);
							break;
					}
				}
			}
			this.trySleep(0);
		}
	}

	private void play(final @NotNull Audio audio, @NotNull final AudioPlayer player) {
		AudioThreadPlayer.QUEUE.offer(new Object[]{audio, AudioThreadController.REQUEST_PLAY});
		SoundSystem.THREAD_PLAYER.wake();
		if (player.getVolume(audio) > player.getMasterVolume()) {
			player.setVolume(audio, player.getMasterVolume());
		}
		player.nowPlaying.add(audio);
	}

	private void pause(@NotNull final Audio audio, @NotNull final AudioPlayer player) {
		AudioThreadPlayer.QUEUE.offer(new Object[]{audio, AudioThreadController.REQUEST_PAUSE});
		SoundSystem.THREAD_PLAYER.wake();
		player.nowPlaying.remove(audio);
	}

	private void stop(@NotNull final Audio audio, @NotNull final AudioPlayer player) {
		AudioThreadPlayer.QUEUE.offer(new Object[]{audio, AudioThreadController.REQUEST_STOP});
		SoundSystem.THREAD_PLAYER.wake();
		player.nowPlaying.remove(audio);
	}
}