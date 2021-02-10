package shattered.lib.asset;

import java.util.Locale;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

enum AssetTypes {

	LANGUAGE("lang"),
	TEXTURE("textures"),
	FONT("fonts"),
	AUDIO_OGG("audio"),
	AUDIO_WAV("audio"),
	LUA("scripts"),
	BINARY(null);

	private final String name = super.toString().toLowerCase(Locale.ROOT);
	private final String root;

	AssetTypes(@Nullable final String root) {
		this.root = root != null ? root + "/" : "";
	}

	@NotNull
	public String getRoot() {
		return this.root;
	}

	@Override
	public String toString() {
		return this.name;
	}
}