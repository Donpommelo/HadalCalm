package com.mygdx.hadal.event.utility;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.hadal.event.Event;
import com.mygdx.hadal.event.userdata.EventData;
import com.mygdx.hadal.states.PlayState;

import box2dLight.RayHandler;

/**
 * A Limiter is an event keeps track of the number of times it is triggered and links to another event after specified numbers
 *  until a specified number of times of triggering.
 * 
 * Triggered Behavior: When triggered, this event increments its currentCount field and triggers its linked event.
 * 	when it is triggered a specified number of times, it will no longer trigger its linked event
 * Triggering Behavior: This event will trigger its connected event until its currentCount field reaches its maxCount field.
 * 
 * Fields:
 * count: maxCount. When this event is triggered this many times, it will stop triggering its connected event.
 * 
 * @author Zachary Tu
 *
 */
public class Limiter extends Event {

	private static final String name = "Limiter";

	private int maxCount;
	private int currentCount;
	
	public Limiter(PlayState state, World world, OrthographicCamera camera, RayHandler rays, int width, int height,
			int x, int y, int maxCount) {
		super(state, world, camera, rays, name, width, height, x, y);
		this.maxCount = maxCount;
		this.currentCount = 0;
	}
	
	@Override
	public void create() {
		this.eventData = new EventData(world, this) {
			
			@Override
			public void onActivate(EventData activator) {
				if (activator.getEvent() instanceof TriggerAlt) {
					currentCount = 0;
				} else if (currentCount < maxCount && event.getConnectedEvent() != null) {
					event.getConnectedEvent().getEventData().onActivate(this);
					currentCount++;
				}
			}
		};
	}
}
