package com.mygdx.hadal.event;

import com.mygdx.hadal.event.userdata.EventData;
import com.mygdx.hadal.states.PlayState;

/**
 * 
 * @author Zachary Tu
 *
 */
public class Rotator extends Event {

	private boolean continuous, alreadyUsed;
	private float angle;
	
	public Rotator(PlayState state, boolean continuous, float angle) {
		super(state);
		this.continuous = continuous;
		this.angle = angle;
		
		alreadyUsed = false;
	}
	
	@Override
	public void create() {
		this.eventData = new EventData(this);
	}
	
	@Override
	public void controller(float delta) {
		if (!alreadyUsed) {
			alreadyUsed = true;
			if (getConnectedEvent() != null) {
				if (getConnectedEvent().getBody() != null) {
					if (continuous) {
						getConnectedEvent().getBody().setAngularVelocity(angle);
					} else {
						getConnectedEvent().getBody().setTransform(getConnectedEvent().getBody().getPosition(), angle);
					}
					
				}
			}
		}
	}
}
