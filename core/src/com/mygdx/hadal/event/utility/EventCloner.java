package com.mygdx.hadal.event.utility;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.mygdx.hadal.constants.BodyConstants;
import com.mygdx.hadal.event.Event;
import com.mygdx.hadal.event.userdata.EventData;
import com.mygdx.hadal.schmucks.entities.Player;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.utils.TiledObjectUtil;
import com.mygdx.hadal.utils.b2d.HadalBody;

/**
 * An EventCloner. This Event will create a copy of a specified event and move it to its own location.
 * <p>
 * Triggered Behavior: When triggered, this event will perform the cloning.
 * Triggering Behavior: The connected event is the one who will be cloned.
 * <p>
 * Fields: N/A
 * 
 * @author Vortilla Vlinestrone
 */
public class EventCloner extends Event {
	
	public EventCloner(PlayState state, Vector2 startPos, Vector2 size) {
		super(state, startPos, size);
	}
	
	@Override
	public void create() {

		this.eventData = new EventData(this) {
			
			@Override
			public void onActivate(EventData activator, Player p) {
				if (event.getConnectedEvent() != null) {
					if (event.getConnectedEvent().getBlueprint() != null) {
						Event clone = TiledObjectUtil.parseSingleEventWithTriggers(state, event.getConnectedEvent().getBlueprint());
						
						if (standardParticle != null) {
							standardParticle.onForBurst(1.0f);
						}
						clone.setStartPos(event.getStartPos());
					}
				}
			}
		};

		this.body = new HadalBody(eventData, startPos, size, BodyConstants.BIT_SENSOR, (short) 0, (short) 0)
				.setBodyType(BodyType.KinematicBody)
				.addToWorld(world);
	}
}
