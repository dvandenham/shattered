package shattered.lib.audio;

import org.jetbrains.annotations.NotNull;
import shattered.lib.ResourceLocation;
import shattered.lib.asset.AssetRegistry;
import shattered.lib.asset.Audio;

final class QueueRequest {

	final AudioPlayer player;
	final Audio audio;
	final byte request;

	private QueueRequest(@NotNull final AudioPlayer player, @NotNull final ResourceLocation resource, final byte request) {
		this.player = player;
		this.audio = (Audio) AssetRegistry.getAsset(resource);
		this.request = request;
	}

	@NotNull
	static QueueRequest play(@NotNull final AudioPlayer player, @NotNull final ResourceLocation resource) {
		return new QueueRequest(player, resource, (byte) 0);
	}

	@NotNull
	static QueueRequest pause(@NotNull final AudioPlayer player, @NotNull final ResourceLocation resource) {
		return new QueueRequest(player, resource, (byte) 1);
	}

	@NotNull
	static QueueRequest stop(@NotNull final AudioPlayer player, @NotNull final ResourceLocation resource) {
		return new QueueRequest(player, resource, (byte) 2);
	}
}