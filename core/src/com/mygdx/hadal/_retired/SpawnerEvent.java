package com.mygdx.hadal._retired;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.hadal.event.Event;
import com.mygdx.hadal.event.userdata.EventData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.utils.Constants;
import com.mygdx.hadal.utils.b2d.BodyBuilder;

import box2dLight.RayHandler;

/**
 * An event spawner an event that activates when triggered by another event.
 * This event spawns a specified event.
 * 
 * Triggered Behavior: When triggered, this will spawn an event.
 * Triggering Behavior: optionally, when an event is spawned, its connected event can be set to this event's connected event.
 * 
 * Fields:
 * id: The id of the type of event to spawn
 * reset: boolean. Should newly spawned events trigger this event's connected event? Optional. default: true
 * args: string extra args for newly spawned events. Optional. Default: ""
 * 
 * @author Zachary Tu
 *
 */
public class SpawnerEvent extends Event {
	
	private int spawnX, spawnY;
	
	private int id;
	private boolean reset;
	
	private String args;
	
	private static final String name = "Event Spawner";

	public SpawnerEvent(PlayState state, World world, OrthographicCamera camera, RayHandler rays, int width, int height,
			int x, int y, int id, boolean resetActivator, String extraArgs) {
		super(state, world, camera, rays, name, width, height, x, y, "event_base", 0.25f, 2);
		this.spawnX = x;
		this.spawnY = y;
		this.id = id;
		this.reset = resetActivator;
		this.args = extraArgs;
	}
	
	@Override
	public void create() {

		this.eventData = new EventData(world, this) {
			
			@Override
			public void onActivate(EventData activator) {
				Event event = null;
				
				switch(id) {
				case 0:
					
					break;
				case 1:
					event = new Medpak(state, world, camera, rays, spawnX, spawnY);
					break;
				case 2:
					event = new AirBubble(state, world, camera, rays, spawnX, spawnY);
					break;
				case 3:
					event = new ScrapHeap(state, world, camera, rays, spawnX, spawnY, Integer.parseInt(args));
					break;
				}
				
				//If this event has a connected event, can specify that new events inherit it.
				//Otherwise, they can also activate the event that activates this as default.
				if (reset) {
					if (getEvent().getConnectedEvent() != null) {
						event.setConnectedEvent(getEvent().getConnectedEvent());
					} else {
						event.setConnectedEvent(activator.getEvent());
					}
				}
			}
		};
		
		this.body = BodyBuilder.createBox(world, startX, startY, width, height, 1, 1, 0, true, true, Constants.BIT_SENSOR, 
				(short) (0), (short) 0, true, eventData);
	}
	
	public String getArgs() {
		return args;
	}	
}
