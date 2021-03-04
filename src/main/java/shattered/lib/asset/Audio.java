package shattered.lib.asset;

import org.jetbrains.annotations.NotNull;
import org.lwjgl.openal.AL10;
import shattered.core.event.MessageEvent;
import shattered.core.event.MessageListener;
import shattered.Shattered;
import shattered.lib.ResourceLocation;

public final class Audio extends IAsset {

	private final JsonAudioData data;
	private int[] audioData;
	private int length;

	Audio(@NotNull final ResourceLocation resource, @NotNull final JsonAudioData data) {
		super(resource);
		this.data = data;
		Shattered.SYSTEM_BUS.register(this);
	}

	@MessageListener("load_audio")
	private void onLoadAudio(final MessageEvent ignored) {
		this.load();
		Shattered.SYSTEM_BUS.unregister(this);
	}

	public int getChannels() {
		return this.audioData[AudioLoader.INDEX_CHANNELS];
	}

	public int getSampleRate() {
		return this.audioData[AudioLoader.INDEX_SAMPLE_RATE];
	}

	public int getFormat() {
		return this.audioData[AudioLoader.INDEX_FORMAT];
	}

	public int getBufferPointer() {
		return this.audioData[AudioLoader.INDEX_POINTER_BUFFER];
	}

	public int getSourcePointer() {
		return this.audioData[AudioLoader.INDEX_POINTER_SOURCE];
	}

	public int getLengthMillis() {
		return this.length;
	}

	@Override
	void onDestroy() {
		super.onDestroy();
		AL10.alDeleteBuffers(this.getBufferPointer());
		AL10.alDeleteSources(this.getSourcePointer());
	}

	private void load() {
		final int[] audioData = AudioLoader.load(this.getResource(), this.data);
		if (audioData.length == 0) {
			AssetRegistry.LOGGER.error("Could not load audio data: {}", this.getResource());
			return;
		}
		AssetRegistry.LOGGER.debug("Registered audio: {} ({})", this.getResource(), this.data.audioType);
		this.audioData = audioData;
		this.length = this.audioData[AudioLoader.INDEX_AUDIO_LENGTH];
	}
}