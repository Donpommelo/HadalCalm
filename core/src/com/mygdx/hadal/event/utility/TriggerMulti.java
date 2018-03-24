package com.mygdx.hadal.event.utility;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.hadal.event.Event;
import com.mygdx.hadal.event.userdata.EventData;
import com.mygdx.hadal.states.PlayState;

import box2dLight.RayHandler;

/**
 * A Multitrigger is an event that can trigger multiple events simultaneously.
 * 
 * Triggered Behavior: When triggered, this will trigger all events in its triggered list
 * Triggering Behavior: N/A. This event does nothing with its connectedEvent. Instead, it has a triggered list that is filled
 * when parsing the map.
 * 
 * Fields:
 * 
 * triggeringId: This string should be a comma-separated list of triggeredIds of events that can be triggered.
 * NO SPACES IN THIS LIST
 * @author Zachary Tu
 *
 */
public class TriggerMulti extends Event {

	private static final String name = "MultiTrigger";

	private ArrayList<Event> triggered = new ArrayList<Event>();
	
	public TriggerMulti(PlayState state, World world, OrthographicCamera camera, RayHandler rays, int width, int height,
			int x, int y) {
		super(state, world, camera, rays, name, width, height, x, y);
	}
	
	@Override
	public void create() {
		this.eventData = new EventData(world, this) {
			
			@Override
			public void onActivate(EventData activator) {
				for (Event e : triggered) {
					if (e != null) {
						if (e.getEventData() != null) {
							e.getEventData().onActivate(this);
						}
					}
				}
			}
		};
	}
	
	public void addTrigger(Event e) {
		triggered.add(e);
	}
}
