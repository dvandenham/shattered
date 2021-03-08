package shattered.game.world;

import java.util.ArrayList;
import java.util.List;
import shattered.Shattered;
import shattered.game.ICollisionListener;
import shattered.game.WorldObjectPhysicsIdentifier;
import shattered.lib.ResourceLocation;
import shattered.lib.math.Rectangle;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.jbox2d.callbacks.ContactImpulse;
import org.jbox2d.callbacks.ContactListener;
import org.jbox2d.collision.Manifold;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.World;
import org.jbox2d.dynamics.contacts.Contact;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

class WorldPhysics {

	private final List<ICollisionListener> collisionListeners = new ObjectArrayList<>();
	public final World physics;

	WorldPhysics() {
		this.physics = new World(new Vec2(0, -10F));
	}

	public void tick() {
		this.physics.step((float) Shattered.SECONDS_PER_TICK, 6, 2);
		this.physics.setContactListener(new ContactListener() {
			@Override
			public void beginContact(final Contact contact) {
				final Body body1 = contact.getFixtureA().m_body;
				if (!(body1.m_userData instanceof WorldObjectPhysicsIdentifier)) {
					throw new RuntimeException("Collision detected with unregistered body!");
				}
				final Body body2 = contact.getFixtureB().m_body;
				if (!(body2.m_userData instanceof WorldObjectPhysicsIdentifier)) {
					throw new RuntimeException("Collision detected with unregistered body!");
				}
				for (final ICollisionListener listener : new ArrayList<>(WorldPhysics.this.collisionListeners)) {
					listener.onCollision(
							body1, body2,
							(WorldObjectPhysicsIdentifier) body1.m_userData, (WorldObjectPhysicsIdentifier) body2.m_userData
					);
				}
			}

			@Override
			public void endContact(final Contact contact) {
			}

			@Override
			public void preSolve(final Contact contact, final Manifold oldManifold) {
			}

			@Override
			public void postSolve(final Contact contact, final ContactImpulse impulse) {
			}
		});
	}

	public Body addEntityBody(@NotNull final ResourceLocation resource, @NotNull final Rectangle bounds) {
		//Create body def and set type, position and identifier
		final BodyDef def = new BodyDef();
		def.type = BodyType.DYNAMIC;
		def.position = new Vec2(bounds.getX(), bounds.getY());
		def.userData = new WorldObjectPhysicsIdentifier(resource, bounds.getPosition());
		//Add the def to the world, creating the body
		final Body body = this.physics.createBody(def);
		//Create the body shape and set it's size
		final PolygonShape shape = new PolygonShape();
		shape.setAsBox(bounds.getWidth() / 2F, bounds.getHeight() / 2F);
		//Create the fixture def and set props
		final FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = shape;
		fixtureDef.density = 1;
		fixtureDef.friction = 0.3F;
		//Register the shape in the body
		body.createFixture(fixtureDef);
		return body;
	}

	public Body addWorldBody(@NotNull final ResourceLocation resource, @NotNull final Rectangle bounds) {
		//Create body def and set position and identifier
		final BodyDef def = new BodyDef();
		def.position = new Vec2(bounds.getX(), bounds.getY());
		def.userData = new WorldObjectPhysicsIdentifier(resource, bounds.getPosition());
		//Add the def to the world, creating the body
		final Body body = this.physics.createBody(def);
		//Create the body shape and set it's size
		final PolygonShape shape = new PolygonShape();
		shape.setAsBox(bounds.getWidth() / 2F, bounds.getHeight() / 2F);
		//Register the shape in the body
		body.createFixture(shape, 0);
		return body;
	}

	public void addCollisionListener(@NotNull final ICollisionListener listener) {
		if (!this.collisionListeners.contains(listener)) {
			this.collisionListeners.add(listener);
		}
	}

	@Nullable
	public Body findBody(@NotNull final WorldObjectPhysicsIdentifier identifier) {
		for (Body body = this.physics.getBodyList(); body != null; body = body.m_next) {
			if (identifier.equals(body.getUserData())) {
				return body;
			}
		}
		return null;
	}
}
