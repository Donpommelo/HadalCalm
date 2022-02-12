package com.mygdx.hadal.strategies.hitbox;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.schmucks.entities.HadalEntity;
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
	
	private static final float pushInterval = 1 / 60f;
	private float controllerCount;
	
	//this is the power of the force applied to the hbox when it tries to home.
	private final float homePower;
	
	//this is the entity we home towards. (either the player's mouse or the player)
	private final HadalEntity target;
	
	public HomingMouse(PlayState state, Hitbox proj, BodyData user, float homePower) {
		super(state, proj, user);
		this.homePower = homePower;
		
		if (user.getSchmuck() instanceof Player player) {
			target = player.getMouse();
		} else {
			target = state.getPlayer();
		}
	}
	
	private final Vector2 homingPush = new Vector2();
	@Override
	public void controller(float delta) {					
		controllerCount += delta;
		while (controllerCount >= pushInterval) {
			controllerCount -= pushInterval;
			
			homingPush.set(target.getPosition()).sub(hbox.getPosition()).nor().scl(homePower * hbox.getMass());
			hbox.applyForceToCenter(homingPush);
		}
	}	
}
