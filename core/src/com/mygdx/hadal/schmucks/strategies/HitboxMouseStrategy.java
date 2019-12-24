package com.mygdx.hadal.schmucks.strategies;

import com.badlogic.gdx.ai.steer.SteeringAcceleration;
import com.badlogic.gdx.ai.steer.behaviors.Seek;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.schmucks.bodies.Player;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;

/**
 * This strategy makes a hbox home in on the player's mouse
 * @author Zachary Tu
 *
 */
public class HitboxMouseStrategy extends HitboxStrategy {
	
	public HitboxMouseStrategy(PlayState state, Hitbox proj, BodyData user, float maxLinSpd, float maxLinAcc, float maxAngSpd,
			float maxAngAcc, int boundingRad, int decelerationRadius) {
		super(state, proj, user);
		
		if (user.getSchmuck() instanceof Player) {
			hbox.setBehavior(new Seek<Vector2>(hbox, ((Player)user.getSchmuck()).getMouse()));
		} else {
			hbox.setBehavior(new Seek<Vector2>(hbox, state.getPlayer()));
		}
		
		hbox.setMaxLinearSpeed(maxLinSpd);
		hbox.setMaxLinearAcceleration(maxLinAcc);
		hbox.setMaxAngularSpeed(maxAngSpd);
		hbox.setMaxAngularAcceleration(maxAngAcc);
		
		hbox.setBoundingRadius(boundingRad);
		hbox.setDecelerationRad(decelerationRadius);
		
		hbox.setTagged(false);
		hbox.setSteeringOutput(new SteeringAcceleration<Vector2>(new Vector2()));
	}
	
	@Override
	public void controller(float delta) {					
		if (hbox.getBehavior() != null) {
			hbox.getBehavior().calculateSteering(hbox.getSteeringOutput());
			hbox.applySteering(delta);
		}	
	}	
}
