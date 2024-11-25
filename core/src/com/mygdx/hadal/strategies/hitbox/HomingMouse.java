package com.mygdx.hadal.strategies.hitbox;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.constants.Constants;
import com.mygdx.hadal.schmucks.entities.Player;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.strategies.HitboxStrategy;

/**
 * This strategy makes a hbox home in on the player's mouse
 * @author Lodelaire Lumpus
 */
public class HomingMouse extends HitboxStrategy {

	private static final float MIN_ANGLE = 5.0f;

	private float controllerCount;
	
	//this is the power of the force applied to the hbox when it tries to home.
	private final float homePower;

	//this is the player that owns this
	private Player owner;

	public HomingMouse(PlayState state, Hitbox proj, BodyData user, float homePower) {
		super(state, proj, user);
		this.homePower = homePower;

		if (user.getSchmuck() instanceof Player player) {
			owner = player;
		}
		hbox.setSynced(true);
		hbox.setSyncedDelete(true);
	}

	private final Vector2 currentVelocity = new Vector2();
	private final Vector2 desiredDirection = new Vector2();
	private final Vector2 currentDirection = new Vector2();
	private final Vector2 lateralDirection = new Vector2();
	@Override
	public void controller(float delta) {
		if (!state.isServer()) { return; }

		controllerCount += delta;
		while (controllerCount >= Constants.INTERVAL) {
			controllerCount -= Constants.INTERVAL;

			if (owner != null) {
				currentVelocity.set(hbox.getLinearVelocity());
				desiredDirection.set(owner.getMouseHelper().getPosition()).sub(hbox.getPosition()).nor();
				currentDirection.set(currentVelocity).nor();

				float angleDifference = desiredDirection.angleDeg(currentDirection);

				if (Math.abs(angleDifference) > MIN_ANGLE) {
					if (Math.signum(MathUtils.sinDeg(angleDifference)) == 1) {
						lateralDirection.set(currentDirection).rotateDeg(94);
					} else {
						lateralDirection.set(currentDirection).rotateDeg(-94);
					}

					lateralDirection.nor().scl(homePower);

					hbox.applyForceToCenter(lateralDirection);
				}
			}
		}
	}	
}
