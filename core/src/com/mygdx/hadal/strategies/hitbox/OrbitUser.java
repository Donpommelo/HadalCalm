package com.mygdx.hadal.strategies.hitbox;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.strategies.HitboxStrategy;

/**
 * This strategy makes a hbox return to user
 * @author Zachary Tu
 *
 */
public class OrbitUser extends HitboxStrategy {
	
	private float controllerCount = 0;
	private Vector2 diff = new Vector2();
	
	private final static float pushInterval = 1 / 60f;
	private float returnAmp;
	
	public OrbitUser(PlayState state, Hitbox proj, BodyData user, float returnAmp) {
		super(state, proj, user);
		this.returnAmp = returnAmp;
	}
	
	private Vector2 playerPos = new Vector2();
	
	@Override
	public void create() {
		playerPos.set(creator.getSchmuck().getPosition());
	}
	
	@Override
	public void controller(float delta) {
		
		controllerCount += delta;

		//hbox repeatedly is pushed towards player. Controllercount is checked to ensure framerate does not affect speed
		while (controllerCount >= pushInterval) {
			controllerCount -= pushInterval;
			diff.set(creator.getSchmuck().getPosition()).sub(hbox.getPosition());
			hbox.applyForceToCenter(diff.nor().scl(returnAmp * hbox.getMass()));
		}
		
		if (creator.getSchmuck().getBody() != null) {
			hbox.setTransform(new Vector2(hbox.getPosition()).add(creator.getSchmuck().getPosition()).sub(playerPos), hbox.getBody().getAngle());
			playerPos.set(creator.getSchmuck().getPosition());
		}
	}
}
