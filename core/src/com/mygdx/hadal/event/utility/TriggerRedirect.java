package com.mygdx.hadal.event.utility;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.hadal.event.Event;
import com.mygdx.hadal.event.userdata.EventData;
import com.mygdx.hadal.states.PlayState;

import box2dLight.RayHandler;

/**
 * A Redirecttrigger is an event that can trigger another event while saying that another event did it.
 * 
 * @author Zachary Tu
 *
 */
public class TriggerRedirect extends Event {

	private static final String name = "RedirectTrigger";

	private Event blame ;
	
	public TriggerRedirect(PlayState state, World world, OrthographicCamera camera, RayHandler rays, int width, int height,
			int x, int y) {
		super(state, world, camera, rays, name, width, height, x, y);
	}
	
	@Override
	public void create() {
		this.eventData = new EventData(world, this) {
			
			@Override
			public void onActivate(EventData activator) {
				if (event.getConnectedEvent() != null && blame != null) {
					event.getConnectedEvent().getEventData().onActivate(blame.getEventData());
				}
			}
		};
	}
	
	public void setBlame(Event e) {
		blame = e;
	}
}
