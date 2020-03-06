package com.mygdx.hadal.strategies.hitbox;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.schmucks.bodies.HadalEntity;
import com.mygdx.hadal.schmucks.bodies.Player;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.strategies.HitboxStrategy;

/**
 * This strategy makes a hbox home in on the player's mouse
 * @author Zachary Tu
 *
 */
public class HomingMouse extends HitboxStrategy {
	
	private final static float pushInterval = 1 / 60f;
	private float controllerCount = 0;
	//this is the power of the force applied to the hbox when it tries to home.
	private float homePower;
	
	//this is the amount of seconds the hbox will attempt to predict its target's position
	private float maxPredictionTime = 0.5f;

	//this is the entity we home towards. (either the player's mouse or the player)
	private HadalEntity target;
	
	public HomingMouse(PlayState state, Hitbox proj, BodyData user, float homePower) {
		super(state, proj, user);
		this.homePower = homePower;
		
		if (user.getSchmuck() instanceof Player) {
			target = ((Player)user.getSchmuck()).getMouse();
		} else {
			target =  state.getPlayer();
		}
	}
	
	private Vector2 homingPush = new Vector2();
	@Override
	public void controller(float delta) {					
		controllerCount += delta;

		while (controllerCount >= pushInterval) {
			controllerCount -= pushInterval;
			
			homingPush.set(target.getPosition())
			.sub(hbox.getPosition().mulAdd(hbox.getLinearVelocity(), maxPredictionTime)).nor().scl(homePower * hbox.getMass());
			
			hbox.push(homingPush);
		}
	}	
}
