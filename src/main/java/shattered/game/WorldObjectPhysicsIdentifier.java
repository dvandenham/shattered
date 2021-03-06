package shattered.game;

import java.util.Objects;
import org.jetbrains.annotations.NotNull;
import shattered.lib.ResourceLocation;

public final class WorldObjectPhysicsIdentifier {

	@NotNull
	public final ResourceLocation resource;

	@NotNull
	public final Object data;

	public WorldObjectPhysicsIdentifier(@NotNull final ResourceLocation resource, @NotNull final Object data) {
		this.resource = resource;
		this.data = data;
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.resource, this.data);
	}

	@Override
	public boolean equals(final Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || this.getClass() != o.getClass()) {
			return false;
		}
		final WorldObjectPhysicsIdentifier other = (WorldObjectPhysicsIdentifier) o;
		return this.resource.equals(other.resource) && this.data.equals(other.data);
	}

	@Override
	public String toString() {
		return "[" + this.resource + "]" + this.data;
	}
}