package com.mygdx.hadal.strategies.hitbox;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.schmucks.entities.HadalEntity;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.strategies.HitboxStrategy;

/**
 * This strategy makes a hbox move around its user in a sin wave
 * @author Tarsula Tranilla
 */
public class WaveEntity extends HitboxStrategy {
	
	//this is the angle of the hbox compared to the player, the distance and the speed that it rotates
	private final float amplitude, frequency, startAngle;
	
	//this is the entity that the hbox waves around
	private final HadalEntity target;
	
	public WaveEntity(PlayState state, Hitbox proj, BodyData user, HadalEntity target, float amplitude, float frequency, float startAngle) {
		super(state, proj, user);
		this.target = target;
		this.amplitude = amplitude;
		this.frequency = frequency;
		this.startAngle = startAngle;
	}

	private float controllerCount;
	private float timer;
	private static final float pushInterval = 1 / 60f;
	private final Vector2 lastPos = new Vector2();
	private final Vector2 centerPos = new Vector2();
	private final Vector2 offset = new Vector2();
	@Override
	public void controller(float delta) {
		controllerCount += delta;
		timer += delta;
		while (controllerCount >= pushInterval) {
			controllerCount -= pushInterval;

			if (target.getBody() != null && target.isAlive()) {
				offset.set(0, amplitude * MathUtils.sin(timer * frequency)).setAngleDeg(hbox.getLinearVelocity().angleDeg() + startAngle);

				centerPos.set(target.getPosition()).add(offset);
				hbox.setTransform(centerPos, lastPos.sub(centerPos).angleRad());
				lastPos.set(centerPos);
			} else {
				hbox.die();
			}
		}
	}
}
