package com.mygdx.hadal.strategies.hitbox;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.strategies.HitboxStrategy;
import com.mygdx.hadal.constants.Constants;

/**
 * This strategy makes an hbox's trajectory curve towards a particular coordinates.
 * hboxes start off with optional spread and continuously adjust their movement to aim to a static vector
 * @author Glovecraft Gronxderf
 */
public class Curve extends HitboxStrategy {

	//this is the distance from the target where the hbox will stop curving. This value is squared to avoid having to calculate a square root.
	private static final float BOUND_DIST = 80.0f;

	//this is the range of spread in degrees that the hbox can be set to
	private final int spreadMin, spreadMax;
	
	//the starting velocity of the hbox and the speed that it curves towards its target.
	private final float startSpeed, lerp;

	//has the hbox reached its target yet? if so, stop adjusting its movement.
	private boolean found;
	
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
		float newDegrees = hbox.getStartVelo().angleDeg();
		
		if (MathUtils.random.nextBoolean()) {
			newDegrees += (MathUtils.random(spreadMin, spreadMax));
		} else {
			newDegrees -= (MathUtils.random(spreadMin, spreadMax));
		}
		hbox.setLinearVelocity(hbox.getLinearVelocity().setAngleDeg(newDegrees));
	}
	
	private float controllerCount;
	private final Vector2 startTarget = new Vector2();
	private final Vector2 lerpTowards = new Vector2();
	private final Vector2 entityLocation = new Vector2();
	@Override
	public void controller(float delta) {
		controllerCount += delta;

		while (controllerCount >= Constants.INTERVAL) {
			controllerCount -= Constants.INTERVAL;
			
			if (!found) {
				entityLocation.set(hbox.getPixelPosition());
				hbox.setLinearVelocity(hbox.getLinearVelocity().lerp(lerpTowards.set(startTarget).sub(entityLocation).nor().scl(startSpeed), lerp));
				
				if (entityLocation.dst2(startTarget) < BOUND_DIST * BOUND_DIST) {
					found = true;
				}
			}
		}
	}
}
