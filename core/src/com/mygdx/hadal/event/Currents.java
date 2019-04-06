package com.mygdx.hadal.event;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.event.userdata.EventData;
import com.mygdx.hadal.schmucks.bodies.HadalEntity;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.utils.Constants;
import com.mygdx.hadal.utils.b2d.BodyBuilder;

/**
 * Currents are an event that apply a continuous force to all schmucks inside of it.
 * 
 * Triggered Behavior: N/A
 * Triggering Behavior: N/A
 * 
 * Fields:
 * N/A
 * 
 * @author Zachary Tu
 *
 */
public class Currents extends Event {
	
	//force applied every 1/60 seconds
	private Vector2 vec;

	//This keeps track of engine timer.
	private float controllerCount = 0;
	
	private static final String name = "Current";

	public Currents(PlayState state, int width, int height, int x, int y, Vector2 vec) {
		super(state, name, width, height, x, y);
		this.vec = vec;
	}
	
	public Currents(PlayState state, int width, int height, int x, int y, Vector2 vec, float duration) {
		super(state, name, width, height, x, y, duration);
		this.vec = vec;
	}
	
	@Override
	public void create() {

		this.eventData = new EventData(this);
		
		this.body = BodyBuilder.createBox(world, startX, startY, width, height, 1, 1, 0, true, true, Constants.BIT_SENSOR, 
				(short) (Constants.BIT_PLAYER | Constants.BIT_ENEMY | Constants.BIT_PROJECTILE),
				(short) 0, true, eventData);
	}
	
	@Override
	public void controller(float delta) {
		controllerCount+=delta;
		if (controllerCount >= 1/60f) {
			controllerCount = 0;
			
			for (HadalEntity entity : eventData.getSchmucks()) {
				entity.applyLinearImpulse(vec);
			}
		}
	}
	
	@Override
	public String getText() {
		return  name + " " + vec;
	}
	
}
