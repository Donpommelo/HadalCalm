package com.mygdx.hadal.schmucks.strategies;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;

/**
 * This strategy makes a hbox return to user
 * @author Zachary Tu
 *
 */
public class ReturnToUser extends HitboxStrategy {
	
	private float controllerCount = 0;
	private Vector2 diff = new Vector2();
	
	private float returnAmp;
	
	public ReturnToUser(PlayState state, Hitbox proj, BodyData user, float returnAmp) {
		super(state, proj, user);
		this.returnAmp = returnAmp;
	}
	
	@Override
	public void controller(float delta) {
		controllerCount += delta;

		//Boomerang repeatedly is pushed towards player. Controllercount is checked to ensure framerate does not affect speed
		if (controllerCount >= 1/60f) {
			diff.set(creator.getSchmuck().getPixelPosition().x - hbox.getPixelPosition().x, creator.getSchmuck().getPixelPosition().y - hbox.getPixelPosition().y);
			
			hbox.applyForceToCenter(diff.nor().scl(returnAmp * hbox.getMass()));

			controllerCount -= 1/60f;
		}
	}
}
