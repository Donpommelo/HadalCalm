package com.mygdx.hadal.strategies.hitbox;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.schmucks.bodies.HadalEntity;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.strategies.HitboxStrategy;

/**
 * This strategy makes a hbox move around its user in a sin wave
 * @author Zachary Tu
 */
public class WaveEntity extends HitboxStrategy {
	
	//this is the angle of the hbox compared to the player, the distance and the speed that it rotates
	private float amplitude, frequency, startAngle;
	
	private HadalEntity target;
	
	public WaveEntity(PlayState state, Hitbox proj, BodyData user, HadalEntity target, float amplitude, float frequency, float startAngle) {
		super(state, proj, user);
		this.target = target;
		this.amplitude = amplitude;
		this.frequency = frequency;
		this.startAngle = startAngle;
	}
	
	private float timer;
	private Vector2 lastPos = new Vector2();
	private Vector2 centerPos = new Vector2();
	private Vector2 offset = new Vector2();
	@Override
	public void controller(float delta) {
		if (target.getBody() != null && target.isAlive()) {
			timer += delta;
			offset.set(0, (float) (amplitude * Math.sin(timer * frequency))).setAngle(hbox.getLinearVelocity().angle() + startAngle);
			
			centerPos.set(target.getPosition()).add(offset);
			hbox.setTransform(centerPos, lastPos.sub(centerPos).angleRad());
			lastPos.set(centerPos);
		} else {
			hbox.die();
		}
	}
}
