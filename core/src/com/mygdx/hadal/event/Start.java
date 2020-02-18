package com.mygdx.hadal.event;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.mygdx.hadal.event.userdata.EventData;
import com.mygdx.hadal.schmucks.bodies.Player;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.utils.Constants;
import com.mygdx.hadal.utils.b2d.BodyBuilder;

/**
 * 
 * @author Zachary Tu
 *
 */
public class Start extends Event {

	private String startId;
	
	public Start(PlayState state, Vector2 startPos, Vector2 size, String startId) {
		super(state, startPos, size);
		this.startId = startId;
	}
	
	@Override
	public void create() {
		this.eventData = new EventData(this);
		
		this.body = BodyBuilder.createBox(world, startPos, size, 0, 1, 0, true, true, Constants.BIT_SENSOR, (short) (0), (short) 0, true, eventData);
		body.setType(BodyType.KinematicBody);
	}
	
	public void playerStart(final Player p) {
		if (getConnectedEvent() != null) {
			getConnectedEvent().getEventData().preActivate(eventData, p);
		}
	}
	
	public String getStartId() { return startId; }
}
