package com.mygdx.hadal.event;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.event.Event;
import com.mygdx.hadal.event.userdata.EventData;
import com.mygdx.hadal.schmucks.bodies.Player;
import com.mygdx.hadal.states.PlayState;

/**
 * When touched, this event will set as a save point for the player its connected event. 
 * In certain circumstances, the player may be warped back to the last save point, such as respawning.
 * 
 * Triggered Behavior: Set the playstate's save point.
 * Triggering Behavior: This event will be the new save point
 * 
 * @author Zachary Tu
 *
 */
public class SaveSetter extends Event {

	private static final String name = "Save Point";

	private float zoom;
	private boolean clear;
	
	public SaveSetter(PlayState state, float zoom, boolean clear) {
		super(state, name);
		this.zoom = zoom;
		this.clear = clear;
	}
	
	@Override
	public void create() {
		this.eventData = new EventData(this) {
			
			@Override
			public void onActivate(EventData activator, Player p) {
				if (event.getConnectedEvent() != null) {
					state.addSavePoint(new Vector2(event.getConnectedEvent().getPosition().x, event.getConnectedEvent().getPosition().y),
							zoom, null, clear);
				}
			}
		};
	}
}
