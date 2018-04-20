package com.mygdx.hadal.event;

import com.mygdx.hadal.event.userdata.EventData;
import com.mygdx.hadal.schmucks.bodies.HadalEntity;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.utils.Constants;
import com.mygdx.hadal.utils.b2d.BodyBuilder;

/**
 * The Freeze bubble is created temporarily by the player's momentum-freezing ability.
 * This repeatedly sets the velocity of enemy entities to 0.
 * 
 * Triggered Behavior: N/A
 * Triggering Behavior: N/A
 * 
 * Fields:
 * filter: what faction this bubble will freeze.
 * 
 * @author Zachary Tu
 *
 */
public class FreezeBubble extends Event {
	

	//This keeps track of engine timer.
	private float controllerCount = 0;
	
	private short filter;
	
	private static final String name = "Freeze Bubble";

	public FreezeBubble(PlayState state, int width, int height,	int x, int y, float duration, short filter) {
		super(state, name, width, height, x, y, duration);
		this.filter = filter;
	}
	
	@Override
	public void create() {

		this.eventData = new EventData(this);
		
		this.body = BodyBuilder.createBox(world, startX, startY, width, height, 1, 1, 0, true, true, Constants.BIT_SENSOR, 
				(short) (Constants.BIT_PLAYER | Constants.BIT_ENEMY | Constants.BIT_PROJECTILE),
				filter, true, eventData);
	}
	
	@Override
	public void controller(float delta) {
		controllerCount+=delta;
		if (controllerCount >= 1/60f) {
			controllerCount = 0;
			
			for (HadalEntity entity : eventData.getSchmucks()) {
				entity.getBody().setLinearVelocity(0, 0);
			}
		}
		super.controller(delta);
		
	}
	
}
