package com.mygdx.hadal.event;

import com.mygdx.hadal.event.Event;
import com.mygdx.hadal.event.userdata.EventData;
import com.mygdx.hadal.schmucks.bodies.Player;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.utils.Constants;
import com.mygdx.hadal.utils.b2d.BodyBuilder;

/**
 * When touched, this event will set as a save point for the player its connected event. 
 * In certain circumstances, the player may be warped back to the last save point, such as respawning.
 * 
 * Triggered Behavior: Set the playstate's save point at the location of this event (or adds a possible save point).
 * Triggering Behavior: The connected event will be the focus of the camera after returning to the save
 * 
 * @author Zachary Tu
 *
 */
public class SaveSetter extends Event {

	private static final String name = "Save Point";

	private float zoom;
	private boolean clear;
	
	public SaveSetter(PlayState state, int width, int height, int x, int y, float zoom, boolean clear) {
		super(state, name, width, height, x, y);
		this.zoom = zoom;
		this.clear = clear;
	}
	
	@Override
	public void create() {
		this.eventData = new EventData(this) {
			
			@Override
			public void onActivate(EventData activator, Player p) {
				
				if (event.getConnectedEvent() == null) {
					state.addSavePoint(event.getPosition(), null, zoom, clear);
				} else {
					state.addSavePoint(event.getPosition(), event.getConnectedEvent().getPosition(), zoom, clear);
				}
			}
		};
		
		this.body = BodyBuilder.createBox(world, startX, startY, width, height, 1, 1, 0, true, true, Constants.BIT_SENSOR, 
				(short) 0, (short) 0, true, eventData);
	}
}
