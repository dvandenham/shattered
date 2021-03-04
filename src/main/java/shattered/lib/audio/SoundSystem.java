package shattered.lib.audio;

import java.util.ArrayList;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.ALC;
import org.lwjgl.openal.ALC10;
import shattered.core.event.EventBusSubscriber;
import shattered.core.event.MessageEvent;
import shattered.core.event.MessageListener;
import shattered.Shattered;

@EventBusSubscriber(Shattered.SYSTEM_BUS_NAME)
public final class SoundSystem {

	public static final SoundSystem INSTANCE = new SoundSystem();
	static final AudioThreadController CONTROLLER = new AudioThreadController();
	static final AudioThreadPlayer PLAYERS = new AudioThreadPlayer();
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
		final String defaultName = ALC10.alcGetString(0, ALC10.ALC_DEFAULT_DEVICE_SPECIFIER);
		this.deviceID = ALC10.alcOpenDevice(defaultName);
		this.context = ALC10.alcCreateContext(this.deviceID, new int[]{0});
		ALC10.alcMakeContextCurrent(this.context);
		AL.createCapabilities(ALC.createCapabilities(this.deviceID));
		SoundSystem.CONTROLLER.start();
		SoundSystem.PLAYERS.start();
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
		SoundSystem.PLAYERS.close();
		SoundSystem.PLAYERS.wake();
		SoundSystem.CONTROLLER.close();
		SoundSystem.CONTROLLER.wake();
		for (final AudioPlayer player : this.players) {
			player.destroy();
		}
		this.players.clear();
		ALC10.alcDestroyContext(this.context);
		ALC10.alcCloseDevice(this.deviceID);
	}

	@MessageListener("init_sound_system")
	private static void onInitSoundSystem(final MessageEvent ignored) {
		SoundSystem.INSTANCE.initialize();
		Shattered.LOGGER.debug("SoundSystem has been initialized");
	}

	@MessageListener("shutdown")
	private static void onShutdown(final MessageEvent ignored) {
		Shattered.LOGGER.debug("SoundSystem is shutting down!");
		SoundSystem.INSTANCE.destroy();
	}
}