package com.mygdx.hadal.event;

import com.badlogic.gdx.math.MathUtils;
import com.mygdx.hadal.event.userdata.EventData;
import com.mygdx.hadal.schmucks.entities.Player;
import com.mygdx.hadal.states.PlayState;

/**
 * Rotators connect to other events and either apply a continuous or instant rotation
 * 
 * Triggered Behavior: when triggered, if continuous, this event will toggle on/off the rotating of the connected event
 * Triggering Behavior: This event's connected event is the event that will be rotated
 * 
 * Fields:
 * continuous: do we apply rotation continuously or instantly? Default: true
 * angle: The amount of rotation to apply (either set its angular velocity or its angle)
 * 
 * @author Smeggdrop Shudale
 */
public class Rotator extends Event {

	private final boolean continuous;
	private final float angle;
	
	public Rotator(PlayState state, boolean continuous, float angle) {
		super(state);
		this.continuous = continuous;
		this.angle = angle;
	}
	
	@Override
	public void create() {
		this.eventData = new EventData(this) {
			
			@Override
			public void onActivate(EventData activator, Player p) {
				if (continuous) {
					if (getConnectedEvent() != null) {
						if (getConnectedEvent().getBody() != null) {
							if (getConnectedEvent().getAngularVelocity() == 0) {
								getConnectedEvent().setAngularVelocity(angle);
							} else {
								getConnectedEvent().setAngularVelocity(0);
							}
						}
					}
				} else {
					if (getConnectedEvent() != null) {
						getConnectedEvent().setTransform(getConnectedEvent().getPosition(), angle * MathUtils.degRad);
					}
				}
			}
		};
	}
}
