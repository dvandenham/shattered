package shattered;

import shattered.lib.ReflectionHelper;
import shattered.lib.ResourceLocation;
import shattered.lib.asset.AssetRegistry;
import shattered.lib.asset.Texture;
import shattered.lib.gfx.Shader;

public final class StaticAssets {

	public static Shader SHADER;

	private StaticAssets() {
	}

	static void loadAssets() {
		if (StaticAssets.SHADER != null) {
			StaticAssets.SHADER.destroy();
		}
		StaticAssets.SHADER = new Shader(Assets.SHADER_VERTEX, Assets.SHADER_FRAGMENT);
		ReflectionHelper.invokeMethod(
				AssetRegistry.class,
				null,
				Texture.class,
				ResourceLocation.class,
				Assets.TEXTURE_ARGON
		);
		ReflectionHelper.invokeMethod(
				AssetRegistry.class,
				null,
				Texture.class,
				ResourceLocation.class,
				Assets.TEXTURE_LOADING
		);
	}
}