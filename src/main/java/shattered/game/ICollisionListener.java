package shattered.game;

import org.jbox2d.dynamics.Body;
import org.jetbrains.annotations.NotNull;

public interface ICollisionListener {

	void onCollision(@NotNull Body body1, @NotNull Body body2, @NotNull WorldObjectPhysicsIdentifier obj1, @NotNull WorldObjectPhysicsIdentifier obj2);
}