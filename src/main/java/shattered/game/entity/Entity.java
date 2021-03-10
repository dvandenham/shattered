package shattered.game.entity;

import shattered.Shattered;
import shattered.core.LuaMachine;
import shattered.core.LuaScript;
import shattered.core.lua.constant.LuaConstantStateContainer;
import shattered.core.lua.lib.LuaLibEntity;
import shattered.core.lua.lib.LuaLibWorld;
import shattered.core.nbtx.NBTXTagTable;
import shattered.game.Direction;
import shattered.game.world.World;
import shattered.lib.ResourceLocation;
import shattered.lib.StringUtils;
import shattered.lib.gfx.Display;
import shattered.lib.gfx.FontRenderer;
import shattered.lib.gfx.Tessellator;
import shattered.lib.math.Rectangle;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.luaj.vm2.LuaTable;

public final class Entity {

	//Jumping takes 1 second and jumps 2.25 tiles
	public static final int JUMP_TIMER = Shattered.TICK_RATE / 3;
	private static final double JUMP_STRENGTH = (World.TILE_SIZE * 2.25) / (double) Entity.JUMP_TIMER;
	private static final double MAX_MOVE_STRENGTH = 1.5;
	private static final double MOVEMENT_SPEED_MODIFIER = 1.15;

	@NotNull
	private final ResourceLocation resource;
	@NotNull
	private final EntityType type;
	@NotNull
	private final World world;
	@NotNull
	private final LuaScript updateScript;
	@NotNull
	private final LuaScript renderScript;
	@NotNull
	private final Rectangle bounds;
	@NotNull
	private final EntityAttributeContainer attributes;
	@NotNull
	private final LuaTable state = new LuaTable();
	@NotNull
	private String currentVariant;

	@Nullable
	private EntityAction currentAction;
	private int currentActionTimer;
	@Nullable
	private Direction moveDirection = null;
	private boolean didMoveLastTick = false;
	private double moveSpeed = 0;

	private Entity(@NotNull final EntityType type, @NotNull final World world, @NotNull final Rectangle bounds) {
		this.resource = type.getResource();
		this.type = type;
		this.world = world;
		this.updateScript = this.createScript(false, type.getUpdateScript(), this.state);
		this.renderScript = this.createScript(true, type.getRenderScript(), this.state);
		this.bounds = Rectangle.createMutable(bounds.getPosition(), bounds.getSize());
		this.attributes = type.getAttributes().copy();
		this.currentVariant = this.resource.getVariant();
	}

	@NotNull
	private LuaScript createScript(final boolean canUseRenderUtils, @NotNull final ResourceLocation resource, @NotNull final LuaTable state) {
		final LuaScript result = canUseRenderUtils ? LuaMachine.loadRenderScript(resource) : LuaMachine.loadScript(resource);
		assert result != null;
		result.register(new LuaConstantStateContainer(state));
		result.register(new LuaLibWorld(this.world));
		result.register(new LuaLibEntity(this));
		return result;
	}

	@SuppressWarnings("ConstantConditions")
	public void tick() {
		this.tryMove();
		if ((boolean) this.attributes.get(EntityAttributes.HAS_GRAVITY)) {
			if (this.isJumping()) {
				this.bounds.moveY(Entity.JUMP_STRENGTH);
			} else if (this.isFloating()) {
				if (this.isFalling()) {
					final int space = this.world.rayCast(this.bounds, Direction.DOWN, 6);
					System.out.println(space);
					this.bounds.moveY(-space);
				} else {
					this.execute(EntityAction.FALLING, -1, true);
				}
			} else {
				this.execute(null, 0);
			}
		}
		if (this.currentActionTimer > 0) {
			--this.currentActionTimer;
		}
		this.updateScript.executeScript();
	}

	private void tryMove() {
		if (this.moveDirection != null && this.canMove(this.moveDirection)) {
			if (this.didMoveLastTick) {
				if (this.moveSpeed < Entity.MAX_MOVE_STRENGTH) {
					if (this.moveSpeed == 0) {
						this.moveSpeed = 0.25;
					} else {
						this.moveSpeed = Math.min(this.moveSpeed * Entity.MOVEMENT_SPEED_MODIFIER, Entity.MAX_MOVE_STRENGTH);
					}
				}
			} else {
				this.moveSpeed = Math.max(this.moveSpeed / Entity.MOVEMENT_SPEED_MODIFIER, 0);
				if (this.moveSpeed <= 0.25) {
					this.moveSpeed = 0;
				}
			}
			switch (this.moveDirection) {
				case LEFT:
					this.bounds.moveX(-this.moveSpeed);
					break;
				case RIGHT:
					this.bounds.moveX(this.moveSpeed);
					break;
			}
		} else {
			this.moveSpeed = 0;
		}
		if (this.moveSpeed == 0) {
			this.moveDirection = null;
		}
		this.didMoveLastTick = false;
	}

	public void render(@NotNull final Tessellator tessellator, @NotNull final FontRenderer fontRenderer) {
		final int renderY = Display.getHeight() - this.bounds.getY() - this.bounds.getHeight();
		tessellator.drawQuick(this.bounds.getX(), renderY, this.bounds.getSize(), this.type.getTexture());
		this.renderScript.executeScript();
	}

	@NotNull
	public NBTXTagTable serialize(@NotNull NBTXTagTable table) {
		return table;
	}

	public void deserialize(@NotNull NBTXTagTable table) {
		
	}

	public void setVariant(@NotNull final String variant) {
		if (!StringUtils.isAlphaString(variant)) {
			throw new IllegalArgumentException("Variant can only contain characters from range [a-z]");
		}
		this.currentVariant = variant;
	}

	public void move(@NotNull final Direction direction) {
		if (this.moveSpeed == 0 || this.moveDirection == direction) {
			this.moveDirection = direction;
			this.didMoveLastTick = true;
		}
	}

	public void execute(@Nullable final EntityAction action, final int timer) {
		this.execute(action, timer, false);
	}

	public void execute(@Nullable final EntityAction action, final int timer, final boolean override) {
		if (override || this.currentAction == null || timer == 0) {
			this.currentAction = action;
			this.currentActionTimer = timer;
		}
	}

	public boolean isExecuting(@NotNull final EntityAction action) {
		return this.currentAction == action && (this.currentActionTimer == -1 || this.currentActionTimer > 0);
	}

	public boolean isJumping() {
		return this.isExecuting(EntityAction.JUMPING);
	}

	public boolean isFalling() {
		return this.isExecuting(EntityAction.FALLING);
	}

	public boolean isFloating() {
		return this.world.rayCast(this.bounds, Direction.DOWN, 3) > 0;
	}

	public boolean canMove(@NotNull final Direction direction) {
		return this.world.rayCast(this.bounds, direction, 3) > 0;
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
	public Rectangle getBounds() {
		return this.bounds;
	}
}