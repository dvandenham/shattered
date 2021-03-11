package shattered.lib.audio;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import shattered.Shattered;
import shattered.core.event.EventBusSubscriber;
import shattered.core.event.MessageEvent;
import shattered.core.event.MessageListener;
import shattered.lib.asset.Audio;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.ALC;
import static org.lwjgl.openal.AL10.alSourcePause;
import static org.lwjgl.openal.AL10.alSourcePlay;
import static org.lwjgl.openal.AL10.alSourceStop;
import static org.lwjgl.openal.ALC10.ALC_DEFAULT_DEVICE_SPECIFIER;
import static org.lwjgl.openal.ALC10.alcCloseDevice;
import static org.lwjgl.openal.ALC10.alcCreateContext;
import static org.lwjgl.openal.ALC10.alcDestroyContext;
import static org.lwjgl.openal.ALC10.alcGetString;
import static org.lwjgl.openal.ALC10.alcMakeContextCurrent;
import static org.lwjgl.openal.ALC10.alcOpenDevice;

public final class SoundSystem extends Thread {

	private static final AtomicBoolean RUNNING = new AtomicBoolean(false);
	private static final ObjectArrayList<AudioPlayer> PLAYERS = new ObjectArrayList<>();
	private static final ConcurrentLinkedQueue<QueueRequest> QUEUE = new ConcurrentLinkedQueue<>();
	private static long deviceID, context;
	static SoundSystem INSTANCE;

	SoundSystem() {
		super("SoundSystem");
	}

	@NotNull
	public static AudioPlayer createPlayer() {
		final AudioPlayer player = new AudioPlayer();
		SoundSystem.PLAYERS.add(player);
		return player;
	}

	@Override
	public void run() {
		while (SoundSystem.RUNNING.get()) {
			QueueRequest request;
			long sleepMillis = 0;
			while ((request = SoundSystem.QUEUE.peek()) != null) {
				final Audio audio = request.audio;
				final AudioPlayer player = request.player;
				switch (request.request) {
					case QueueRequest.REQUEST_PLAY:
						if (player.getVolume(audio) > player.getMasterVolume()) {
							player.setVolume(audio, player.getMasterVolume());
						}
						alSourcePlay(audio.getSourcePointer());
						SoundSystem.QUEUE.remove();
						if (audio.getLengthMillis() > sleepMillis) {
							sleepMillis = audio.getLengthMillis();
						}
						break;
					case QueueRequest.REQUEST_PAUSE:
						alSourcePause(audio.getSourcePointer());
						break;
					case QueueRequest.REQUEST_STOP:
						alSourceStop(audio.getSourcePointer());
						break;
				}
			}
			this.trySleep(sleepMillis);
		}
	}

	private synchronized void trySleep(final long millis) {
		try {
			Thread.sleep(millis);
		} catch (final InterruptedException ignored) {
		}
	}

	private synchronized void wake() {
		synchronized (this) {
			this.interrupt();
		}
	}

	static void queue(@NotNull final QueueRequest request) {
		SoundSystem.QUEUE.offer(request);
		SoundSystem.INSTANCE.wake();
	}

	public static void clearSystem() {
		SoundSystem.QUEUE.clear();
		SoundSystem.PLAYERS.clear();
	}

	@EventBusSubscriber(Shattered.SYSTEM_BUS_NAME)
	private static class EventHandler {

		@MessageListener("init_sound_system")
		private static void onInitSoundSystem(final MessageEvent ignored) {
			SoundSystem.INSTANCE = new SoundSystem();

			final String specifier = alcGetString(0, ALC_DEFAULT_DEVICE_SPECIFIER);
			SoundSystem.deviceID = alcOpenDevice(specifier);
			SoundSystem.context = alcCreateContext(SoundSystem.deviceID, new int[]{0});

			alcMakeContextCurrent(SoundSystem.context);
			AL.createCapabilities(ALC.createCapabilities(SoundSystem.deviceID));

			SoundSystem.RUNNING.set(true);
			SoundSystem.INSTANCE.start();

			Shattered.LOGGER.debug("SoundSystem has been initialized");
		}

		@MessageListener("shutdown_sound_system")
		private static void onShutdown(final MessageEvent ignored) {
			Shattered.LOGGER.debug("SoundSystem is shutting down!");

			SoundSystem.RUNNING.set(false);
			SoundSystem.INSTANCE.wake();

			SoundSystem.QUEUE.clear();

			alcDestroyContext(SoundSystem.context);
			alcCloseDevice(SoundSystem.deviceID);
		}
	}
}