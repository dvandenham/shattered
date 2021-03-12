package shattered.lib.gui.component;

import shattered.lib.gui.GuiPanel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class GuiTab extends GuiPanel {

	@NotNull
	private String title;
	private boolean localizeTitle = true;
	GuiTabPanel panel;

	public GuiTab(@NotNull final String title) {
		this.title = title;
	}

	@NotNull
	public final GuiTab setTitle(@NotNull final String title) {
		this.title = title;
		return this;
	}

	@NotNull
	public final GuiTab setLocalizeTitle(final boolean localizeTitle) {
		this.localizeTitle = localizeTitle;
		return this;
	}

	@NotNull
	public final String getTitle() {
		return this.title;
	}

	public final boolean localizeTitle() {
		return this.localizeTitle;
	}

	@Nullable
	protected final GuiTabPanel getTabPanel() {
		return this.panel;
	}
}