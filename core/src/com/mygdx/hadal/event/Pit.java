package com.mygdx.hadal.event;

import com.mygdx.hadal.event.Event;
import com.mygdx.hadal.event.userdata.EventData;
import com.mygdx.hadal.schmucks.bodies.HadalEntity;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.utils.Constants;
import com.mygdx.hadal.utils.b2d.BodyBuilder;

/**
 * This event is something that, when touched, returns the player to their last save point. Maybe add a damage component later?
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
public class Pit extends Event {

	private static final String name = "Pit";

	public Pit(PlayState state, int width, int height, int x, int y) {
		super(state, name, width, height, x, y);
	}
	
	@Override
	public void create() {
		this.eventData = new EventData(this);
		
		this.body = BodyBuilder.createBox(world, startX, startY, width, height, 1, 1, 0, true, true, Constants.BIT_SENSOR, 
				(short) (Constants.BIT_PLAYER),	(short) 0, true, eventData);
	}
	
	@Override
	public void controller(float delta) {
		for (HadalEntity s : eventData.getSchmucks()) {
			//TODO: DEAL DAMAGE?
			s.getBody().setTransform(state.getSafeX(), state.getSafeY(), 0);
		}
		eventData.getSchmucks().clear();
	}
}
