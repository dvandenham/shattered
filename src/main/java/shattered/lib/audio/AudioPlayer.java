package shattered.lib.audio;

import java.util.concurrent.CopyOnWriteArrayList;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.openal.AL10;
import shattered.lib.ResourceLocation;
import shattered.lib.asset.Audio;

@SuppressWarnings("unused")
public final class AudioPlayer {

	final CopyOnWriteArrayList<Audio> nowPlaying = new CopyOnWriteArrayList<>();
	private float volume = 1.0F;

	public void play(@NotNull final ResourceLocation audio) {
		AudioThreadController.QUEUE.offer(QueueRequest.play(this, audio));
		SoundSystem.CONTROLLER.wake();
	}

	public void pause(@NotNull final ResourceLocation audio) {
		AudioThreadController.QUEUE.offer(QueueRequest.pause(this, audio));
		SoundSystem.CONTROLLER.wake();
	}

	public void resume(@NotNull final ResourceLocation audio) {
		this.play(audio);
	}

	public void stop(@NotNull final ResourceLocation audio) {
		AudioThreadController.QUEUE.offer(QueueRequest.stop(this, audio));
		SoundSystem.CONTROLLER.wake();
	}

	public boolean isPlaying(@NotNull final Audio audio) {
		final int pointer = audio.getSourcePointer();
		return pointer != -1 && AL10.alGetSourcei(pointer, AL10.AL_SOURCE_STATE) == AL10.AL_PLAYING;
	}

	public boolean isPaused(@NotNull final Audio audio) {
		final int pointer = audio.getSourcePointer();
		return pointer != -1 && AL10.alGetSourcei(pointer, AL10.AL_SOURCE_STATE) == AL10.AL_PAUSED;
	}

	public void setVolume(@NotNull final Audio audio, final float volume) {
		final int pointer = audio.getSourcePointer();
		if (pointer != -1) {
			AL10.alSourcef(pointer, AL10.AL_GAIN, volume);
		}
	}

	public float getVolume(@NotNull final Audio audio) {
		final int pointer = audio.getSourcePointer();
		return pointer != -1 ? AL10.alGetSourcef(pointer, AL10.AL_GAIN) : 0F;
	}

	public void setMasterVolume(final float volume) {
		this.volume = volume;
		for (final Audio audio : this.nowPlaying) {
			if (this.getVolume(audio) > this.volume) {
				this.setVolume(audio, this.volume);
			}
		}
	}

	public float getMasterVolume() {
		return this.volume;
	}

	public void destroy() {
		this.nowPlaying.clear();
	}
}