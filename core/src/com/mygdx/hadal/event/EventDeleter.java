package com.mygdx.hadal.event;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.hadal.event.userdata.EventData;
import com.mygdx.hadal.states.PlayState;

import box2dLight.RayHandler;

/**
 * An event deleter TBA
 * @author Zachary Tu
 *
 */
public class EventDeleter extends Event {
	
	private static final String name = "Event Deleter";

	public EventDeleter(PlayState state, World world, OrthographicCamera camera, RayHandler rays, int width, int height,
			int x, int y) {
		super(state, world, camera, rays, name, width, height, x, y);
	}
	
	@Override
	public void create() {

		this.eventData = new EventData(world, this) {
			
			@Override
			public void onActivate(EventData activator) {
				
				activator.getEvent().queueDeletion();
				
				if (event.getConnectedEvent() != null) {
					event.getConnectedEvent().getEventData().onActivate(this);
				}
			}
		};
	}
	
}
