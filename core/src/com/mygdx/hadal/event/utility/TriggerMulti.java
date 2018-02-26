package com.mygdx.hadal.event.utility;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.hadal.event.Event;
import com.mygdx.hadal.event.userdata.EventData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.utils.Constants;
import com.mygdx.hadal.utils.b2d.BodyBuilder;

import box2dLight.RayHandler;

/**
 * A Multitrigger is an event that can trigger multiple events.
 * 
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
		
		this.body = BodyBuilder.createBox(world, startX, startY, width, height, 1, 1, 0, true, true, Constants.BIT_SENSOR, 
				(short) (Constants.BIT_PLAYER),
				(short) 0, true, eventData);
	}
	
	public void addTrigger(Event e) {
		triggered.add(e);
	}
}
