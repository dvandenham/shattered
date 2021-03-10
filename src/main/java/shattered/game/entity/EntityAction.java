package shattered.game.entity;

import java.util.Locale;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public enum EntityAction {

	FALLING,
	JUMPING;

	private static final EntityAction[] VALUES = EntityAction.values();
	private final String identifier;

	EntityAction() {
		this.identifier = super.toString().toLowerCase(Locale.ROOT);
	}

	@Override
	public String toString() {
		return this.identifier;
	}

	@Nullable
	public static EntityAction getByIdentifier(@NotNull final String identifier) {
		for (final EntityAction action : EntityAction.VALUES) {
			if (action.toString().equals(identifier)) {
				return action;
			}
		}
		return null;
	}
}