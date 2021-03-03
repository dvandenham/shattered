package shattered.lib.asset;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.openal.AL10;
import shattered.lib.ResourceLocation;

public final class Audio extends IAsset {

	private final int[] data;
	private final int length;

	Audio(@NotNull final ResourceLocation resource, final int[] data, final int length) {
		super(resource);
		this.data = data;
		this.length = length;
	}

	public int getChannels() {
		return this.data[AudioLoader.INDEX_CHANNELS];
	}

	public int getSampleRate() {
		return this.data[AudioLoader.INDEX_SAMPLE_RATE];
	}

	public int getFormat() {
		return this.data[AudioLoader.INDEX_FORMAT];
	}

	public int getBufferPointer() {
		return this.data[AudioLoader.INDEX_POINTER_BUFFER];
	}

	public int getSourcePointer() {
		return this.data[AudioLoader.INDEX_POINTER_SOURCE];
	}

	public int getLengthMillis() {
		return this.length;
	}

	@Override
	void recreate(@Nullable final IAsset newAsset) {
		AL10.alDeleteBuffers(this.getBufferPointer());
		AL10.alDeleteSources(this.getSourcePointer());
		super.recreate(newAsset);
	}
}