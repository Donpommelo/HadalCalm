package com.mygdx.hadal.event.utility;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.mygdx.hadal.constants.BodyConstants;
import com.mygdx.hadal.event.Event;
import com.mygdx.hadal.event.userdata.EventData;
import com.mygdx.hadal.event.userdata.InteractableEventData;
import com.mygdx.hadal.schmucks.entities.Player;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.utils.b2d.HadalBody;

/**
 * A PositionDummy is an event that simply provides a reference to its position. This is used by other events that need to connect
 * to an event to do something related to its location like a portal or a moving platform.
 * <p>
 * Triggered Behavior: N/A.
 * Triggering Behavior: N/A. However, this event is commonly used with moving platforms which use these events as points along a path
 * 	to move along. When reaching one event, the platform will begin moving towards its connected event. As such, when making moving
 * 	platform paths, this should connect to the next event in the path.
 * <p>
 * Fields:
 * dummyId: This id is used for special dummy interactable (for example, a boss might look for a "ceiling" dummy to spawn falling rocks)
 * @author Stidorf Swineymaker
 */
public class PositionDummy extends Event {

	private final String id;
	
	public PositionDummy(PlayState state, Vector2 startPos, Vector2 size, String id) {
		super(state, startPos, size);
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

		this.body = new HadalBody(eventData, startPos, size, BodyConstants.BIT_SENSOR, (short) 0, (short) 0)
				.setBodyType(BodyType.KinematicBody)
				.addToWorld(world);

		if (!"".equals(id)) {
			state.addDummyPoint(this, id);
		}
	}
}
