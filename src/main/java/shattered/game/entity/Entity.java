package shattered.game.entity;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jetbrains.annotations.NotNull;
import shattered.game.WorldObjectPhysicsIdentifier;
import shattered.lib.ResourceLocation;
import shattered.lib.StringUtils;
import shattered.lib.gfx.Display;
import shattered.lib.gfx.FontRenderer;
import shattered.lib.gfx.Tessellator;
import shattered.lib.math.Dimension;
import shattered.lib.math.Rectangle;

public final class Entity {

	@NotNull
	private final ResourceLocation resource;
	@NotNull
	private final EntityType type;
	@NotNull
	private final Body physicsBody;
	@NotNull
	private String currentVariant;

	private Entity(@NotNull final ResourceLocation resource, @NotNull final EntityType type, @NotNull final Body physicsBody) {
		this.resource = resource;
		this.type = type;
		this.physicsBody = physicsBody;
		this.currentVariant = this.resource.getVariant();
	}

	public void tick() {

	}

	public void render(@NotNull final Tessellator tessellator, @NotNull final FontRenderer fontRenderer) {
		final Dimension s = this.type.getEntitySize();
		final Vec2 pp = this.physicsBody.getPosition();
		final Rectangle render = Rectangle.create((int) pp.x, Display.getHeight() - (int) pp.y, s.getWidth() * 32, s.getHeight() * 32);
		System.out.println(render);
		tessellator.drawQuick(render, this.type.getTexture());
	}

	public void setVariant(@NotNull final String variant) {
		if (!StringUtils.isAlphaString(variant)) {
			throw new IllegalArgumentException("Variant can only contain characters from range [a-z]");
		}
		this.currentVariant = variant;
	}

	@NotNull
	public ResourceLocation getResource() {
		return this.resource.toVariant(this.currentVariant);
	}

	@NotNull
	public ResourceLocation getBaseResource() {
		return this.resource;
	}

	@NotNull
	public EntityType getType() {
		return this.type;
	}

	@NotNull
	public Body getPhysicsBody() {
		return this.physicsBody;
	}

	@NotNull
	public WorldObjectPhysicsIdentifier getPhysicsIdentifier() {
		return (WorldObjectPhysicsIdentifier) this.physicsBody.m_userData;
	}
}