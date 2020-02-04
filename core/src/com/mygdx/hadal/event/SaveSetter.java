package com.mygdx.hadal.event;

import com.badlogic.gdx.math.Vector2;
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

	private float zoom;
	private boolean clear;
	
	public SaveSetter(PlayState state, Vector2 startPos, Vector2 size, float zoom, boolean clear, boolean onInit) {
		super(state, startPos, size);
		this.zoom = zoom;
		this.clear = clear;
		
		if (onInit) {
			state.addSavePoint(startPos, null, zoom, clear);
		}
	}
	
	@Override
	public void create() {
		this.eventData = new EventData(this) {
			
			@Override
			public void onActivate(EventData activator, Player p) {
				
				if (event.getConnectedEvent() == null) {
					state.addSavePoint(event.getPixelPosition(), null, zoom, clear);
				} else {
					state.addSavePoint(event.getPixelPosition(), event.getConnectedEvent().getPixelPosition(), zoom, clear);
				}
			}
		};
		
		this.body = BodyBuilder.createBox(world, startPos, size, 1, 1, 0, true, true, Constants.BIT_SENSOR, (short) 0, (short) 0, true, eventData);
	}
}
