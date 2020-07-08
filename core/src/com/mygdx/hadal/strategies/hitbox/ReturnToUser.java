package com.mygdx.hadal.strategies.hitbox;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.strategies.HitboxStrategy;

/**
 * This strategy makes a hbox return to user
 * @author Zachary Tu
 */
public class ReturnToUser extends HitboxStrategy {
	
	private final static float pushInterval = 1 / 60f;
	
	//this is the power that the hbox returns to the user at
	private float returnAmp;
	
	public ReturnToUser(PlayState state, Hitbox proj, BodyData user, float returnAmp) {
		super(state, proj, user);
		this.returnAmp = returnAmp;
	}
	
	private float controllerCount;
	private Vector2 diff = new Vector2();
	@Override
	public void controller(float delta) {
		controllerCount += delta;

		//hbox repeatedly is pushed towards player. Controllercount is checked to ensure framerate does not affect speed
		while (controllerCount >= pushInterval) {
			controllerCount -= pushInterval;
			diff.set(creator.getSchmuck().getPixelPosition()).sub(hbox.getPixelPosition());
			hbox.applyForceToCenter(diff.nor().scl(returnAmp * hbox.getMass()));
		}
	}
}
