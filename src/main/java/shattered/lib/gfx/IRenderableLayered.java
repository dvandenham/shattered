package shattered.lib.gfx;

import org.jetbrains.annotations.NotNull;

public interface IRenderableLayered {

	void renderBackground(@NotNull Tessellator tessellator, @NotNull FontRenderer fontRenderer);

	void renderForeground(@NotNull Tessellator tessellator, @NotNull FontRenderer fontRenderer);
}