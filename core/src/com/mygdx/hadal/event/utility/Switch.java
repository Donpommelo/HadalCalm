package com.mygdx.hadal.event.utility;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.mygdx.hadal.constants.BodyConstants;
import com.mygdx.hadal.event.Event;
import com.mygdx.hadal.event.userdata.EventData;
import com.mygdx.hadal.event.userdata.InteractableEventData;
import com.mygdx.hadal.schmucks.entities.Player;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.utils.b2d.HadalBody;

/**
 * A switch is an activating event that will activate a connected event when the player interacts with it.
 * <p>
 * Triggered Behavior: N/A.
 * Triggering Behavior: When interacted with by the player, this event will trigger its connected event.
 * <p>
 * Fields:
 * N/A
 * 
 * @author Brasteban Brarroway
 */
public class Switch extends Event {

	public Switch(PlayState state, Vector2 startPos, Vector2 size) {
		super(state, startPos, size);
	}
	
	@Override
	public void create() {
		this.eventData = new InteractableEventData(this) {
			
			@Override
			public void onActivate(EventData activator, Player p) {
				if (event.getConnectedEvent() != null) {
					event.getConnectedEvent().getEventData().preActivate(this, p);
					
					if (standardParticle != null) {
						standardParticle.onForBurst(1.0f);
					}
				}
			}
		};

		this.body = new HadalBody(eventData, startPos, size, BodyConstants.BIT_SENSOR, BodyConstants.BIT_PLAYER, (short) 0)
				.setBodyType(BodyDef.BodyType.KinematicBody)
				.addToWorld(world);
	}
}
