package shattered.lib.audio;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import org.lwjgl.openal.AL10;
import shattered.lib.asset.Audio;

final class AudioThreadPlayer extends IAudioThread {

	static final CopyOnWriteArrayList<Audio> PLAYING = new CopyOnWriteArrayList<>();
	static final ConcurrentLinkedQueue<Object[]> QUEUE = new ConcurrentLinkedQueue<>();

	AudioThreadPlayer() {
		super("Player");
	}

	@Override
	public void run() {
		while (!this.shouldClose) {
			if (AudioThreadPlayer.PLAYING.isEmpty()) {
				this.trySleep(300000);
			}
			Object[] requestData;
			while ((requestData = AudioThreadPlayer.QUEUE.poll()) != null) {
				final Audio audio = (Audio) requestData[0];
				switch ((int) requestData[1]) {
					case 0:
						AL10.alSourcePlay(audio.getSourcePointer());
						if (!AudioThreadPlayer.PLAYING.contains(audio)) {
							AudioThreadPlayer.PLAYING.add(audio);
						}
						break;
					case 1:
						AL10.alSourcePause(audio.getSourcePointer());
						break;
					case 2:
						AL10.alSourceStop(audio.getSourcePointer());
						AudioThreadPlayer.PLAYING.remove(audio);
						break;
				}
			}
			this.trySleep(20);
		}
	}
}