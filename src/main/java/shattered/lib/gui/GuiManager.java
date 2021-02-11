package shattered.lib.gui;

import java.util.function.BiConsumer;
import org.jetbrains.annotations.NotNull;
import shattered.core.event.EventBusSubscriber;
import shattered.core.event.MessageEvent;
import shattered.core.event.MessageListener;
import shattered.Shattered;
import shattered.lib.gfx.FontRenderer;
import shattered.lib.gfx.Tessellator;

@EventBusSubscriber(Shattered.SYSTEM_BUS_NAME)
public final class GuiManager {

	public static final GuiManager INSTANCE = new GuiManager();

	private GuiManager() {
	}

	private void tick() {

	}

	private void render(@NotNull final Tessellator tessellator, @NotNull final FontRenderer fontRenderer) {

	}

	@MessageListener("init_gui")
	private static void onMessageReceived(final MessageEvent event) {
		event.setResponse(() -> new Object[]{
				(Runnable) GuiManager.INSTANCE::tick,
				(BiConsumer<Tessellator, FontRenderer>) GuiManager.INSTANCE::render
		});
	}
}