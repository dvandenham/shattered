package shattered.lib.asset;

import org.jetbrains.annotations.NotNull;
import shattered.lib.ResourceLocation;

public abstract class IAsset {

	private final ResourceLocation resource;

	IAsset(@NotNull final ResourceLocation resource) {
		this.resource = resource;
	}

	@NotNull
	public final ResourceLocation getResource() {
		return this.resource;
	}

	void onDestroy() {
	}
}