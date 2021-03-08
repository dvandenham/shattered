package shattered.game.entity;

import shattered.lib.ResourceLocation;
import shattered.lib.math.Dimension;
import org.jetbrains.annotations.NotNull;

public final class EntityType {

	@NotNull
	private final ResourceLocation resource;
	@NotNull
	private final ResourceLocation texture;
	@NotNull
	private final Dimension entitySize;
	@NotNull
	private final ResourceLocation updateScript;
	@NotNull
	private final ResourceLocation renderScript;
	@NotNull
	private final EntityAttributeContainer attributes;

	EntityType(@NotNull final ResourceLocation resource,
	           @NotNull final ResourceLocation texture,
	           @NotNull final Dimension entitySize,
	           @NotNull final ResourceLocation updateScript,
	           @NotNull final ResourceLocation renderScript,
	           @NotNull final EntityAttributeContainer attributes
	) {
		this.resource = resource;
		this.texture = texture;
		this.entitySize = entitySize;
		this.updateScript = updateScript;
		this.renderScript = renderScript;
		this.attributes = attributes;
	}

	@NotNull
	public ResourceLocation getResource() {
		return this.resource;
	}

	@NotNull
	public ResourceLocation getTexture() {
		return this.texture;
	}

	@NotNull
	public Dimension getEntitySize() {
		return this.entitySize;
	}

	@NotNull
	public ResourceLocation getUpdateScript() {
		return this.updateScript;
	}

	@NotNull
	public ResourceLocation getRenderScript() {
		return this.renderScript;
	}

	@NotNull
	public EntityAttributeContainer getAttributes() {
		return this.attributes.copy();
	}
}