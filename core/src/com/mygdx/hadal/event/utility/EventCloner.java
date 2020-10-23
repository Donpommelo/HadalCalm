package com.mygdx.hadal.event.utility;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.mygdx.hadal.event.Event;
import com.mygdx.hadal.event.userdata.EventData;
import com.mygdx.hadal.schmucks.bodies.Player;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.utils.Constants;
import com.mygdx.hadal.utils.TiledObjectUtil;
import com.mygdx.hadal.utils.b2d.BodyBuilder;

/**
 * An EventCloner. This Event will create a copy of a specified event and move it to its own location.
 * 
 * Triggered Behavior: When triggered, this event will perform the cloning.
 * Triggering Behavior: The connected event is the one who will be cloned.
 * 
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
		
		this.body = BodyBuilder.createBox(world, startPos, size, 1, 1, 0, true, true, Constants.BIT_SENSOR, (short) (0), (short) 0, true, eventData);
		this.body.setType(BodyType.KinematicBody);
	}
}
