package shattered.lib.audio;

import java.util.ArrayList;
import shattered.Shattered;
import shattered.core.event.EventBusSubscriber;
import shattered.core.event.MessageEvent;
import shattered.core.event.MessageListener;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.ALC;
import org.lwjgl.openal.ALC10;

@EventBusSubscriber(Shattered.SYSTEM_BUS_NAME)
public final class SoundSystem {

	public static final SoundSystem INSTANCE = new SoundSystem();
	static AudioThreadController THREAD_CONTROLLER;
	static AudioThreadPlayer THREAD_PLAYER;
	private final ArrayList<AudioPlayer> players = new ArrayList<>();
	private long deviceID, context;

	private SoundSystem() {
	}

	@NotNull
	public static AudioPlayer createPlayer() {
		final AudioPlayer player = new AudioPlayer();
		SoundSystem.INSTANCE.players.add(player);
		return player;
	}

	private void initialize() {
		SoundSystem.THREAD_CONTROLLER = new AudioThreadController();
		SoundSystem.THREAD_PLAYER = new AudioThreadPlayer();

		final String defaultName = ALC10.alcGetString(0, ALC10.ALC_DEFAULT_DEVICE_SPECIFIER);
		this.deviceID = ALC10.alcOpenDevice(defaultName);
		this.context = ALC10.alcCreateContext(this.deviceID, new int[]{0});
		ALC10.alcMakeContextCurrent(this.context);
		AL.createCapabilities(ALC.createCapabilities(this.deviceID));
		SoundSystem.THREAD_CONTROLLER.start();
		SoundSystem.THREAD_PLAYER.start();
	}

	public long getContext() {
		return this.context;
	}

	public void clearSystem() {
		AudioThreadPlayer.QUEUE.clear();
		AudioThreadPlayer.PLAYING.clear();
		AudioThreadController.QUEUE.clear();
		for (final AudioPlayer player : this.players) {
			player.nowPlaying.clear();
		}
	}

	private void destroy() {
		if (SoundSystem.THREAD_PLAYER != null) {
			SoundSystem.THREAD_PLAYER.close();
			SoundSystem.THREAD_PLAYER = null;
		}
		if (SoundSystem.THREAD_CONTROLLER != null) {
			SoundSystem.THREAD_CONTROLLER.close();
			SoundSystem.THREAD_CONTROLLER = null;
		}

		for (final AudioPlayer player : this.players) {
			player.destroy();
		}

		ALC10.alcDestroyContext(this.context);
		ALC10.alcCloseDevice(this.deviceID);
	}

	@MessageListener("init_sound_system")
	private static void onInitSoundSystem(final MessageEvent ignored) {
		SoundSystem.INSTANCE.initialize();
		Shattered.LOGGER.debug("SoundSystem has been initialized");
	}

	@MessageListener("shutdown_sound_system")
	private static void onShutdown(final MessageEvent ignored) {
		Shattered.LOGGER.debug("SoundSystem is shutting down!");
		SoundSystem.INSTANCE.destroy();
	}
}