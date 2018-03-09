package com.mygdx.hadal.event.utility;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.hadal.event.Event;
import com.mygdx.hadal.event.userdata.EventData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.utils.Constants;
import com.mygdx.hadal.utils.b2d.BodyBuilder;

import box2dLight.RayHandler;

/**
 * A Conditional trigger is like an if statement
 * 
 * @author Zachary Tu
 *
 */
public class TriggerCond extends Event {

	private static final String name = "CondTrigger";

	private Map<String, Event> triggered = new HashMap<String, Event>();
	private String condition;
	
	public TriggerCond(PlayState state, World world, OrthographicCamera camera, RayHandler rays, int width, int height,
			int x, int y, String start) {
		super(state, world, camera, rays, name, width, height, x, y);
		this.condition = start;
	}
	
	@Override
	public void create() {
		this.eventData = new EventData(world, this) {
			
			@Override
			public void onActivate(EventData activator) {
				
				if (activator.getEvent() instanceof TriggerAlt) {
					condition = ((TriggerAlt)activator.getEvent()).getMessage();
				} else {
					if (triggered.get(condition) != null) {
						triggered.get(condition).getEventData().onActivate(this);
					}
				}
			}
		};
		
		this.body = BodyBuilder.createBox(world, startX, startY, width, height, 1, 1, 0, true, true, Constants.BIT_SENSOR, 
				(short) 0, (short) 0, true, eventData);
	}
	
	public void addTrigger(String s, Event e) {
		triggered.put(s, e);
	}
}
