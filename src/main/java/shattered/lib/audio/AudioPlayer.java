package shattered.lib.audio;

import shattered.lib.ResourceLocation;
import shattered.lib.asset.Audio;
import org.jetbrains.annotations.NotNull;
import static org.lwjgl.openal.AL10.AL_GAIN;
import static org.lwjgl.openal.AL10.AL_PAUSED;
import static org.lwjgl.openal.AL10.AL_PLAYING;
import static org.lwjgl.openal.AL10.AL_SOURCE_STATE;
import static org.lwjgl.openal.AL10.alGetSourcef;
import static org.lwjgl.openal.AL10.alGetSourcei;
import static org.lwjgl.openal.AL10.alSourcef;

@SuppressWarnings("unused")
public final class AudioPlayer {

	private float volume = 1.0F;

	public void play(@NotNull final ResourceLocation audio) {
		SoundSystem.queue(QueueRequest.play(this, audio));
	}

	public void pause(@NotNull final ResourceLocation audio) {
		SoundSystem.queue(QueueRequest.pause(this, audio));
	}

	public void resume(@NotNull final ResourceLocation audio) {
		this.play(audio);
	}

	public void stop(@NotNull final ResourceLocation audio) {
		SoundSystem.queue(QueueRequest.stop(this, audio));
	}

	public boolean isPlaying(@NotNull final Audio audio) {
		final int pointer = audio.getSourcePointer();
		return pointer != -1 && alGetSourcei(pointer, AL_SOURCE_STATE) == AL_PLAYING;
	}

	public boolean isPaused(@NotNull final Audio audio) {
		final int pointer = audio.getSourcePointer();
		return pointer != -1 && alGetSourcei(pointer, AL_SOURCE_STATE) == AL_PAUSED;
	}

	public void setVolume(@NotNull final Audio audio, final float volume) {
		final int pointer = audio.getSourcePointer();
		if (pointer != -1) {
			alSourcef(pointer, AL_GAIN, volume);
		}
	}

	public float getVolume(@NotNull final Audio audio) {
		final int pointer = audio.getSourcePointer();
		return pointer != -1 ? alGetSourcef(pointer, AL_GAIN) : 0F;
	}

	public void setMasterVolume(final float volume) {
		this.volume = volume;
	}

	public float getMasterVolume() {
		return this.volume;
	}
}