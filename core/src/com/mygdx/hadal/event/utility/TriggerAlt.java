package com.mygdx.hadal.event.utility;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.hadal.event.Event;
import com.mygdx.hadal.event.userdata.EventData;
import com.mygdx.hadal.states.PlayState;

import box2dLight.RayHandler;

/**
 * An AltTrigger is a trigger used to activate events with multiple ways to activate.
 * 
 * For example, usually activating a spawn makes it spawn enemies. An AltTrigger activating it could change its wave size instead.
 * 
 * When normally triggered, this event will trigger its connected event.
 * When alt triggered, this event will set its message to the alt-trigger's message.
 * 
 * @author Zachary Tu
 *
 */
public class TriggerAlt extends Event {

	private static final String name = "AltTrigger";

	private String message;
	
	public TriggerAlt(PlayState state, World world, OrthographicCamera camera, RayHandler rays, int width, int height,
			int x, int y, String message) {
		super(state, world, camera, rays, name, width, height, x, y);
		this.message = message;
	}
	
	@Override
	public void create() {
		this.eventData = new EventData(world, this) {
			
			@Override
			public void onActivate(EventData activator) {
				
				if (activator.getEvent() instanceof TriggerAlt) {
					setMessage(((TriggerAlt)activator.getEvent()).getMessage());
				} else {
					if (event.getConnectedEvent() != null) {
						event.getConnectedEvent().getEventData().onActivate(this);
					}
				}
			}
		};
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
