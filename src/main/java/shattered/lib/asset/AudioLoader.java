package shattered.lib.asset;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.util.Locale;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.BufferUtils;
import org.lwjgl.stb.STBVorbis;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.libc.LibCStdlib;
import shattered.lib.ResourceLocation;
import shattered.lib.json.JsonUtils;
import static org.lwjgl.openal.AL10.AL_BITS;
import static org.lwjgl.openal.AL10.AL_BUFFER;
import static org.lwjgl.openal.AL10.AL_CHANNELS;
import static org.lwjgl.openal.AL10.AL_FORMAT_MONO16;
import static org.lwjgl.openal.AL10.AL_FORMAT_MONO8;
import static org.lwjgl.openal.AL10.AL_FORMAT_STEREO16;
import static org.lwjgl.openal.AL10.AL_FORMAT_STEREO8;
import static org.lwjgl.openal.AL10.AL_FREQUENCY;
import static org.lwjgl.openal.AL10.AL_SIZE;
import static org.lwjgl.openal.AL10.alBufferData;
import static org.lwjgl.openal.AL10.alGenBuffers;
import static org.lwjgl.openal.AL10.alGenSources;
import static org.lwjgl.openal.AL10.alGetBufferi;
import static org.lwjgl.openal.AL10.alSourcei;

final class AudioLoader {

	private static int nextIndex = 0;
	static final int INDEX_CHANNELS = AudioLoader.nextIndex++;
	static final int INDEX_SAMPLE_RATE = AudioLoader.nextIndex++;
	static final int INDEX_FORMAT = AudioLoader.nextIndex++;
	static final int INDEX_POINTER_BUFFER = AudioLoader.nextIndex++;
	static final int INDEX_POINTER_SOURCE = AudioLoader.nextIndex++;
	static final int INDEX_AUDIO_LENGTH = AudioLoader.nextIndex++;
	private static final int ARRAY_SIZE = AudioLoader.nextIndex++;

	@Nullable
	public static JsonAudioData loadJsonData(@NotNull final ResourceLocation resource) {
		final String path = AssetRegistry.getResourcePath(resource, AssetTypes.AUDIO, "json");
		final URL location = AssetRegistry.getPathUrl(path);
		if (location == null) {
			AssetRegistry.LOGGER.error("Registered audio.json \"{}\" has no matching metadata file!", resource);
			AssetRegistry.LOGGER.error("\tExpected filepath: {}", path);
			AssetRegistry.LOGGER.error("\tAssuming it's an OGG audio resource");
			return null;
		}
		try (final InputStreamReader reader = new InputStreamReader(location.openStream())) {
			return JsonUtils.deserialize(reader, JsonAudioData.class);
		} catch (final IOException | JsonIOException | JsonSyntaxException e) {
			AssetRegistry.LOGGER.error("Could not read audio metadata from audio.json \"{}\"", resource);
			AssetRegistry.LOGGER.error(e);
			AssetRegistry.LOGGER.error("\tIgnoring the metadata and loading as a default off audio resource without variants");
			return null;
		}
	}

	static int[] load(@NotNull final ResourceLocation resource, @NotNull final JsonAudioData data) {
		final String path = AssetRegistry.getResourcePath(resource, AssetTypes.AUDIO, data.audioType.toString());
		try {
			final URL location = AssetRegistry.getPathUrl(path);
			final int[] result;
			switch (data.audioType) {
				case OGG:
					result = AudioLoader.loadOgg(location);
					break;
				case WAV:
					result = AudioLoader.loadWav(location);
					break;
				default:
					AssetRegistry.LOGGER.error("Could not read audio from location: {}. reason: unsupported audio format", path);
					return new int[0];
			}
			result[AudioLoader.INDEX_AUDIO_LENGTH] = AudioLoader.calculateLength(result[AudioLoader.INDEX_POINTER_BUFFER]);
			return result;
		} catch (final Throwable e) {
			AssetRegistry.LOGGER.error("Could not read audio from location: " + path, e);
			return new int[0];
		}
	}

	private static int[] loadOgg(@NotNull final URL location) throws IOException {
		final ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		final InputStream input = location.openStream();
		final byte[] data = new byte[1024];
		int bytesRead;
		while ((bytesRead = input.read(data)) != -1) {
			bytes.write(data, 0, bytesRead);
		}
		input.close();
		final byte[] byteArray = bytes.toByteArray();
		final ByteBuffer dataBuffer = BufferUtils.createByteBuffer(byteArray.length);
		dataBuffer.put(byteArray);
		dataBuffer.flip();
		//Decode buffer
		final int[] result = new int[AudioLoader.ARRAY_SIZE];
		try (final MemoryStack stack = MemoryStack.stackPush()) {
			final IntBuffer channelBuffer = stack.mallocInt(1);
			final IntBuffer sampleRateBuffer = stack.mallocInt(1);
			final ShortBuffer rawAudioBuffer = STBVorbis.stb_vorbis_decode_memory(dataBuffer, channelBuffer, sampleRateBuffer);
			result[AudioLoader.INDEX_CHANNELS] = channelBuffer.get();
			result[AudioLoader.INDEX_SAMPLE_RATE] = sampleRateBuffer.get();
			result[AudioLoader.INDEX_FORMAT] = result[AudioLoader.INDEX_CHANNELS] == 1 ? AL_FORMAT_MONO16 : result[AudioLoader.INDEX_CHANNELS] == 2 ? AL_FORMAT_STEREO16 : -1;
			result[AudioLoader.INDEX_POINTER_BUFFER] = alGenBuffers();
			assert rawAudioBuffer != null;
			alBufferData(result[AudioLoader.INDEX_POINTER_BUFFER], result[AudioLoader.INDEX_FORMAT], rawAudioBuffer, result[AudioLoader.INDEX_SAMPLE_RATE]);
			LibCStdlib.free(rawAudioBuffer);
			result[AudioLoader.INDEX_POINTER_SOURCE] = alGenSources();
			alSourcei(result[AudioLoader.INDEX_POINTER_SOURCE], AL_BUFFER, result[AudioLoader.INDEX_POINTER_BUFFER]);
		}
		return result;
	}

	private static int[] loadWav(@NotNull final URL location) throws IOException {
		try {
			final int[] result = new int[AudioLoader.ARRAY_SIZE];
			final AudioInputStream input = AudioSystem.getAudioInputStream(location.openStream());
			final AudioFormat format = input.getFormat();
			//Load audio.json data
			result[AudioLoader.INDEX_CHANNELS] = format.getChannels();
			result[AudioLoader.INDEX_SAMPLE_RATE] = (int) format.getSampleRate();
			int alFormat = -1;
			switch (format.getChannels()) {
				case 1:
					switch (format.getSampleSizeInBits()) {
						case 8:
							alFormat = AL_FORMAT_MONO8;
							break;
						case 16:
							alFormat = AL_FORMAT_MONO16;
							break;
					}
					break;
				case 2:
					switch (format.getSampleSizeInBits()) {
						case 8:
							alFormat = AL_FORMAT_STEREO8;
							break;
						case 16:
							alFormat = AL_FORMAT_STEREO16;
							break;
					}
					break;
			}
			result[AudioLoader.INDEX_FORMAT] = alFormat;
			//Load data into memory
			int available = input.available();
			if (available <= 0) {
				available = format.getChannels() * (int) input.getFrameLength() * format.getSampleSizeInBits() / 8;
			}
			final byte[] bufferArray = new byte[available];
			int bytesRead, total = 0;
			while ((bytesRead = input.read(bufferArray, total, bufferArray.length - total)) != -1 && total < bufferArray.length) {
				total += bytesRead;
			}
			final ByteBuffer buffer = ByteBuffer.allocateDirect(bufferArray.length);
			buffer.order(ByteOrder.nativeOrder());
			final ByteBuffer bufferArrayWrap = ByteBuffer.wrap(bufferArray);
			bufferArrayWrap.order(format.isBigEndian() ? ByteOrder.BIG_ENDIAN : ByteOrder.LITTLE_ENDIAN);
			if (format.getSampleSizeInBits() == 16) {
				final ShortBuffer bufferShort = buffer.asShortBuffer();
				final ShortBuffer bufferArrayWrapShort = bufferArrayWrap.asShortBuffer();
				while (bufferArrayWrapShort.hasRemaining()) {
					bufferShort.put(bufferArrayWrapShort.get());
				}
			} else {
				while (bufferArrayWrap.hasRemaining()) {
					buffer.put(bufferArrayWrap.get());
				}
			}
			buffer.rewind();
			//Create pointers
			result[AudioLoader.INDEX_POINTER_BUFFER] = alGenBuffers();
			alBufferData(result[AudioLoader.INDEX_POINTER_BUFFER], result[AudioLoader.INDEX_FORMAT], buffer, result[AudioLoader.INDEX_SAMPLE_RATE]);
			buffer.clear();
			result[AudioLoader.INDEX_POINTER_SOURCE] = alGenSources();
			alSourcei(result[AudioLoader.INDEX_POINTER_SOURCE], AL_BUFFER, result[AudioLoader.INDEX_POINTER_BUFFER]);
			return result;
		} catch (final UnsupportedAudioFileException e) {
			throw new IOException(e);
		}
	}

	private static int calculateLength(final int bufferPointer) {
		final int size = alGetBufferi(bufferPointer, AL_SIZE);
		final int channels = alGetBufferi(bufferPointer, AL_CHANNELS);
		final int bits = alGetBufferi(bufferPointer, AL_BITS);
		final int frequency = alGetBufferi(bufferPointer, AL_FREQUENCY);
		final float sampleLength = size * 8F / ((float) channels * (float) bits);
		return (int) Math.ceil((sampleLength / frequency) * 1000F);
	}

	enum AudioType {
		OGG,
		WAV;

		private final String name = super.toString().toLowerCase(Locale.ROOT);

		@Override
		public String toString() {
			return this.name;
		}
	}
}