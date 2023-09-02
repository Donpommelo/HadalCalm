package com.mygdx.hadal.strategies.hitbox;

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.constants.Constants;
import com.mygdx.hadal.schmucks.entities.HadalEntity;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.strategies.HitboxStrategy;

/**
 * This strategy makes a hbox move around its user in a sin wave
 * @author Tarsula Tranilla
 */
public class LerpEntity extends HitboxStrategy {

	//this is the angle of the hbox compared to the player, the distance and the speed that it rotates
	private final float maxDistance, maxDuration, startAngle;

	//this is the entity that the hbox waves around
	private final HadalEntity target;

	public LerpEntity(PlayState state, Hitbox proj, BodyData user, HadalEntity target, float maxDistance, float maxDuration, float startAngle) {
		super(state, proj, user);
		this.target = target;
		this.maxDistance = maxDistance;
		this.maxDuration = maxDuration;
		this.startAngle = startAngle;
	}

	private float controllerCount;
	private float timer;
	private final Vector2 lastPos = new Vector2();
	private final Vector2 centerPos = new Vector2();
	private final Vector2 offset = new Vector2();
	@Override
	public void controller(float delta) {
		controllerCount += delta;
		timer += delta;

		while (controllerCount >= Constants.INTERVAL) {
			controllerCount -= Constants.INTERVAL;

			float percent = Interpolation.pow5Out.apply(timer / maxDuration);
			float angle = hbox.getLinearVelocity().angleDeg();

			if (target.getBody() != null && target.isAlive()) {
				offset.set(0, maxDistance * percent).setAngleDeg(angle + startAngle);

				centerPos.set(target.getPosition()).add(offset);
				hbox.setTransform(centerPos, MathUtils.degreesToRadians * (angle + startAngle * (1 - percent)));
				lastPos.set(centerPos);
			} else {
				hbox.die();
			}
		}
	}
}
