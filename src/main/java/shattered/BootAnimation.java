package shattered;

import org.jetbrains.annotations.NotNull;
import shattered.lib.Color;
import shattered.lib.gfx.Display;
import shattered.lib.gfx.Tessellator;

final class BootAnimation {

	private static final int TPS = 75;
	private static final int TICKS = 255;
	private final Timer timer;

	BootAnimation() {
		this.timer = Shattered.addTimer(BootAnimation.TPS, BootAnimation.TICKS, timer -> {
		});
	}

	public void start() {
		this.timer.start();
	}

	public void render(@NotNull final Tessellator tessellator) {
		tessellator.start();
		if (this.timer.getScaledProgress(3) < 2) {
			tessellator.set(Display.getBounds(), Color.BLACK);
			tessellator.next();
			tessellator.set(0, 0, Assets.TEXTURE_LOGO);
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