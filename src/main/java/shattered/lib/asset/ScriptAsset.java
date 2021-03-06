package shattered.lib.asset;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import shattered.Shattered;
import shattered.lib.FileUtils;
import shattered.lib.ResourceLocation;

public final class ScriptAsset extends IAsset {

	private final String path;
	private String cachedScript;

	ScriptAsset(@NotNull final ResourceLocation resource, @NotNull final String path) {
		super(resource);
		this.path = path;
	}

	@Nullable
	public String getScript() {
		if (this.cachedScript == null) {
			this.cachedScript = FileUtils.streamToString(Shattered.class.getResourceAsStream(this.path));
		}
		return this.cachedScript;
	}

	@Override
	void onDestroy() {
		super.onDestroy();
		this.cachedScript = null;
	}
}