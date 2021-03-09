package shattered;

import shattered.lib.Color;
import shattered.lib.asset.AssetRegistry;
import shattered.lib.asset.Texture;
import shattered.lib.audio.AudioPlayer;
import shattered.lib.audio.SoundSystem;
import shattered.lib.gfx.Display;
import shattered.lib.gfx.Tessellator;
import org.jetbrains.annotations.NotNull;

final class BootAnimation {

	private static final int TPS = 75;
	private static final int TICKS = 255;
	private final Timer timer;
	private final AudioPlayer player;

	BootAnimation() {
		this.timer = Shattered.addTimer(BootAnimation.TPS, BootAnimation.TICKS, timer -> {
			if (timer.isDone()) {
				Shattered.removeTimer(timer);
			}
		});
		this.player = SoundSystem.createPlayer();
	}

	public void start() {
		this.timer.start();
		this.player.play(Assets.AUDIO_BOOT);
	}

	public void render(@NotNull final Tessellator tessellator) {
		tessellator.start();
		if (this.timer.getScaledProgress(3) < 2) {
			tessellator.set(Display.getBounds(), Color.BLACK);
			tessellator.next();
			final Texture texture = (Texture) AssetRegistry.getAsset(Assets.TEXTURE_LOGO);
			assert texture != null;
			final int displayMin = Math.min(Display.getWidth(), Display.getHeight());
			final float scale = (float) displayMin / Math.max(texture.getTextureSize().getWidth(), texture.getTextureSize().getHeight());
			tessellator.set(0, 0, Assets.TEXTURE_LOGO);
			tessellator.scale(scale, scale);
			tessellator.center(Display.getSize());
			tessellator.next();
			if (this.timer.getScaledProgress(3) < 1) {
				final int alpha = 255 - ((int) this.timer.getScaledProgress(255) * 3);
				tessellator.set(Display.getBounds(), Color.BLACK.withAlpha(alpha));
			} else {
				final int alpha = (int) (this.timer.getScaledProgress(255) - 255 / 3) * 3;
				tessellator.set(Display.getBounds(), Color.BLACK.withAlpha(alpha));
			}
		} else {
			final int alpha = (255 - (int) this.timer.getScaledProgress(255)) * 3;
			tessellator.set(Display.getBounds(), Color.BLACK.withAlpha(alpha));
		}
		tessellator.draw();
	}

	@SuppressWarnings("BooleanMethodIsAlwaysInverted")
	public boolean isFinished() {
		return this.timer.isDone();
	}
}