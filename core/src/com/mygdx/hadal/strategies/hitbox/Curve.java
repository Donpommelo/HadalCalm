package com.mygdx.hadal.strategies.hitbox;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.strategies.HitboxStrategy;

import java.util.concurrent.ThreadLocalRandom;

/**
 * This strategy makes an hbox's trajectory curve towards a particular coordinates.
 * hboxes start off with optional spread and continuously adjust their movement to aim to a static vector
 * @author Zachary Tu
 */
public class Curve extends HitboxStrategy {
	
	//this is the range of spread in degrees that the hbox can be set to
	private final int spreadMin, spreadMax;
	
	//the starting velocity of the hbox and the speed that it curves towards its target.
	private final float startSpeed, lerp;
	
	private static final float pushInterval = 1 / 60f;
	
	//has the hbox reached its target yet? if so, stop adjusting its movement.
	private boolean found = false;
	
	//this is the distance from the target where the hbox will stop curving. This value is squared to avoid having to calculate a square root.
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
		float newDegrees = hbox.getStartVelo().angle();
		
		if (ThreadLocalRandom.current().nextBoolean()) {
			newDegrees += (ThreadLocalRandom.current().nextInt(spreadMin, spreadMax));
		} else {
			newDegrees -= (ThreadLocalRandom.current().nextInt(spreadMin, spreadMax));
		}
		hbox.setLinearVelocity(hbox.getLinearVelocity().setAngle(newDegrees));
	}
	
	private float controllerCount;
	private final Vector2 startTarget = new Vector2();
	private final Vector2 lerpTowards = new Vector2();
	private final Vector2 entityLocation = new Vector2();
	@Override
	public void controller(float delta) {
		controllerCount += delta;

		while (controllerCount >= pushInterval) {
			controllerCount -= pushInterval;
			
			if (!found) {
				entityLocation.set(hbox.getPixelPosition());
				hbox.getBody().setLinearVelocity(hbox.getLinearVelocity().lerp(lerpTowards.set(startTarget).sub(entityLocation).nor().scl(startSpeed), lerp));
				
				if (entityLocation.dst2(startTarget) < boundDist * boundDist) {
					found = true;
				}
			}
		}
	}
}
