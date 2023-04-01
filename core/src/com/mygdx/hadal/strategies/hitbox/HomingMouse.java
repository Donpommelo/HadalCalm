package com.mygdx.hadal.strategies.hitbox;

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

	private final Vector2 homingPush = new Vector2();
	@Override
	public void controller(float delta) {
		if (!state.isServer()) { return; }

		controllerCount += delta;
		while (controllerCount >= Constants.INTERVAL) {
			controllerCount -= Constants.INTERVAL;

			if (null != owner) {
				homingPush.set(owner.getMouseHelper().getPosition()).sub(hbox.getPosition()).nor().scl(homePower * hbox.getMass());
				hbox.applyForceToCenter(homingPush);
			}
		}
	}	
}
