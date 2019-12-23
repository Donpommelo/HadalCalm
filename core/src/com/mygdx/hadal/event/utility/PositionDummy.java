package com.mygdx.hadal.event.utility;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.event.Event;
import com.mygdx.hadal.event.userdata.EventData;
import com.mygdx.hadal.event.userdata.InteractableEventData;
import com.mygdx.hadal.schmucks.bodies.Player;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.utils.Constants;
import com.mygdx.hadal.utils.b2d.BodyBuilder;

/**
 * A PositionDummy is an event that simply provides a reference to its position. This is used by other events that need to connect
 * to an event to do something related to its location like a portal or a moving platform.
 * 
 * Triggered Behavior: N/A.
 * Triggering Behavior: N/A. However, this event is commonly used with moving platforms which use these events as points along a path
 * 	to move along. When reaching one event, the platform will begin moving towards its connected event. As such, when making moving
 * 	platform paths, this should connect to the next event in the path.
 * 
 * Fields:
 * N/A
 * @author Zachary Tu
 *
 */
public class PositionDummy extends Event {

	private static final String name = "Position Dummy";

	private String id;
	
	public PositionDummy(PlayState state, Vector2 startPos, Vector2 size, String id) {
		super(state, name, startPos, size);
		this.id = id;
	}
	
	@Override
	public void create() {
		this.eventData = new InteractableEventData(this) {
			
			@Override
			public void onActivate(EventData activator, Player p) {
				if (standardParticle != null) {
					standardParticle.onForBurst(1.0f);
				}
			}
		};
		
		this.body = BodyBuilder.createBox(world, startPos, size, 1, 1, 0, true, true, Constants.BIT_SENSOR, 
				(short) 0, (short) 0, true, eventData);
		
		if (!id.equals("")) {
			state.addDummyPoint(this, id);
		}
	}
}
