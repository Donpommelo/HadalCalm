package com.mygdx.hadal.strategies.hitbox;

import java.util.concurrent.ThreadLocalRandom;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.strategies.HitboxStrategy;

/**
 * @author Zachary Tu
 *
 */
public class Curve extends HitboxStrategy {
	
	//this is the range of spread in degrees that the hbox can be set to
	private int spreadMin, spreadMax;
	private Vector2 startTarget = new Vector2();
	private Vector2 lerpTowards = new Vector2();
	private float startSpeed, lerp;
	
	private float controllerCount = 0;
	private final static float pushInterval = 1 / 60f;
	
	private boolean found = false;
	private static final float boundDist = 60.0f;
	
	public Curve(PlayState state, Hitbox proj, BodyData user, int spreadMin, int spreadMax, Vector2 startTarget, float startSpeed, float lerp) {
		super(state, proj, user);
		this.spreadMin = spreadMin;
		this.spreadMax = spreadMax;
		this.startTarget.set(startTarget);
		this.startSpeed = startSpeed;
		this.lerp = lerp;
	}
	
	@Override
	public void create() {
		float newDegrees = (float) hbox.getStartVelo().angle();
		
		if (ThreadLocalRandom.current().nextBoolean()) {
			newDegrees += (ThreadLocalRandom.current().nextInt(spreadMin, spreadMax));
		} else {
			newDegrees -= (ThreadLocalRandom.current().nextInt(spreadMin, spreadMax));
		}
		
		hbox.setLinearVelocity(hbox.getLinearVelocity().setAngle(newDegrees));
	}
	
	@Override
	public void controller(float delta) {
		controllerCount += delta;

		while (controllerCount >= pushInterval) {
			controllerCount -= pushInterval;
			
			if (!found) {
				hbox.getBody().setLinearVelocity(hbox.getLinearVelocity().lerp(lerpTowards.set(startTarget).sub(hbox.getPixelPosition()).nor().scl(startSpeed), lerp));
				
				if (hbox.getPixelPosition().dst2(startTarget) < boundDist * boundDist) {
					found = true;
				}
			}
		}
	}
}
