package shattered.core.lua.lib;

import shattered.core.lua.ILuaLib;
import shattered.core.sdb.SDBTable;
import shattered.game.GameRegistries;
import shattered.game.entity.Entity;
import shattered.game.entity.EntityType;
import shattered.game.world.World;
import shattered.lib.ReflectionHelper;
import shattered.lib.ResourceLocation;
import shattered.lib.math.Rectangle;
import shattered.lib.registry.NotRegisteredException;
import org.jetbrains.annotations.NotNull;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.ZeroArgFunction;

public final class LuaLibEntity extends ILuaLib {

	@NotNull
	private final World world;
	@NotNull
	private final Entity entity;

	public LuaLibEntity(@NotNull final World world, @NotNull final Entity entity) {
		super("entity");
		this.world = world;
		this.entity = entity;
	}

	@Override
	protected void set(@NotNull final LuaTable object) {
		object.set("getPosX", new _GetPosX());
		object.set("getPosY", new _GetPosY());
		object.set("getSizeX", new _GetSizeX());
		object.set("getSizeY", new _GetSizeY());
		object.set("setVariant", new _SetVariant());
	}

	private class _GetPosX extends ZeroArgFunction {

		@Override
		public LuaValue call() {
			return LuaValue.valueOf(LuaLibEntity.this.entity.getBounds().getX());
		}
	}

	private class _GetPosY extends ZeroArgFunction {

		@Override
		public LuaValue call() {
			return LuaValue.valueOf(LuaLibEntity.this.entity.getBounds().getY());
		}
	}

	private class _GetSizeX extends ZeroArgFunction {

		@Override
		public LuaValue call() {
			return LuaValue.valueOf(LuaLibEntity.this.entity.getBounds().getWidth());
		}
	}

	private class _GetSizeY extends ZeroArgFunction {

		@Override
		public LuaValue call() {
			return LuaValue.valueOf(LuaLibEntity.this.entity.getBounds().getHeight());
		}
	}

	private class _SetVariant extends OneArgFunction {

		@Override
		public LuaValue call(final LuaValue arg) {
			LuaLibEntity.this.world.queueEvent(() -> {
				final ResourceLocation resource = LuaLibEntity.this.entity.getResource().toVariant(arg.checkjstring());
				final EntityType type = GameRegistries.ENTITY().get(resource);
				if (type == null) {
					throw new NotRegisteredException("Entity variant", resource);
				}

				final SDBTable store = LuaLibEntity.this.entity.serialize(new SDBTable());
				LuaLibEntity.this.world.removeEntity(LuaLibEntity.this.entity);

				final Entity newEntity = ReflectionHelper.instantiate(Entity.class, EntityType.class, type, World.class, LuaLibEntity.this.world, Rectangle.class, LuaLibEntity.this.entity.getBounds());
				if (newEntity == null) {
					throw new RuntimeException("Cannot instantiate entity for variant " + resource);
				}
				newEntity.deserialize(store);
				LuaLibEntity.this.world.addEntity(newEntity);
			});
			return LuaValue.NIL;
		}
	}
}
