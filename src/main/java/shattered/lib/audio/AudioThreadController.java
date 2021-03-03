package shattered.lib.audio;

import java.util.Objects;
import java.util.concurrent.ConcurrentLinkedQueue;
import org.jetbrains.annotations.NotNull;
import shattered.lib.asset.Audio;

final class AudioThreadController extends IAudioThread {

	static final ConcurrentLinkedQueue<QueueRequest> QUEUE = new ConcurrentLinkedQueue<>();

	AudioThreadController() {
		super("Controller");
	}

	@Override
	public void run() {
		while (!this.shouldClose) {
			this.trySleep(300000);
			QueueRequest request;
			while ((request = AudioThreadController.QUEUE.poll()) != null) {
				if (Objects.requireNonNull(request.audio).getSourcePointer() != -1) {
					switch (request.request) {
						case 0:
							this.play(request.audio, request.player);
							break;
						case 1:
							this.pause(request.audio, request.player);
							break;
						case 2:
							this.stop(request.audio, request.player);
							break;
					}
				}
			}
		}
	}

	private void play(final @NotNull Audio audio, @NotNull final AudioPlayer player) {
		AudioThreadPlayer.QUEUE.offer(new Object[]{audio, 0});
		SoundSystem.PLAYERS.wake();
		if (player.getVolume(audio) > player.getMasterVolume()) {
			player.setVolume(audio, player.getMasterVolume());
		}
		player.nowPlaying.add(audio);
	}

	private void pause(@NotNull final Audio audio, @NotNull final AudioPlayer player) {
		AudioThreadPlayer.QUEUE.offer(new Object[]{audio, 1});
		SoundSystem.PLAYERS.wake();
		player.nowPlaying.remove(audio);
	}

	private void stop(@NotNull final Audio audio, @NotNull final AudioPlayer player) {
		AudioThreadPlayer.QUEUE.offer(new Object[]{audio, 2});
		SoundSystem.PLAYERS.wake();
		player.nowPlaying.remove(audio);
	}
}