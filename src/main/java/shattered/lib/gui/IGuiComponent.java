package shattered.lib.gui;

import org.jetbrains.annotations.NotNull;

public abstract class IGuiComponent extends IGuiElement {

	private boolean enabled = true;
	private boolean visible = true;
	private int maximumWidth = -1;

	@NotNull
	public IGuiComponent setEnabled(final boolean enabled) {
		this.enabled = enabled;
		return this;
	}

	@NotNull
	public final IGuiComponent setVisible(final boolean visible) {
		this.visible = visible;
		return this;
	}

	@NotNull
	public final IGuiComponent setMaximumWidth(final int maximumWidth) {
		this.maximumWidth = maximumWidth;
		return this;
	}

	public final boolean isEnabled() {
		return this.enabled;
	}

	public final boolean isVisible() {
		return this.visible;
	}

	public final int getMaximumWidth() {
		return this.maximumWidth;
	}
}