package com.mygdx.hadal.event;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.hadal.event.userdata.EventData;
import com.mygdx.hadal.schmucks.UserDataTypes;
import com.mygdx.hadal.schmucks.userdata.HadalData;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.utils.Constants;
import com.mygdx.hadal.utils.b2d.BodyBuilder;

import box2dLight.RayHandler;

/**
 * The fuel restores fuel and is consumed upon being touched by the player.
 * 
 * Triggered Behavior: N/A
 * Triggering Behavior: If this event has a connected event, trigger it when this event is touched by the player.
 * 	NOTE: This event is not created by the map parser, but instead by an event spawner that sets its connected event upon spawning.
 * 	This is useful to have Medpck/Fuel spawners that are triggered by a timer that only starts after the last pickup is used.
 * 
 * Fields:
 * N/A
 * 
 * @author Zachary Tu
 *
 */
public class AirBubble extends Event{

	private static final int width = 16;
	private static final int height = 16;
	
	private static final int fuelRegained = 25;

	
	private static final String name = "Fuel";
	
	public AirBubble(PlayState state, World world, OrthographicCamera camera, RayHandler rays, int x, int y) {
		super(state, world, camera, rays, name, width, height, x, y);
	}

	@Override
	public void create() {

		this.eventData = new EventData(world, this) {
			
			@Override
			public void onTouch(HadalData fixB) {
				if (fixB != null && isAlive()) {
					if (fixB.getType().equals(UserDataTypes.BODY)) {
						if (((PlayerBodyData)fixB).getCurrentFuel() < ((PlayerBodyData)fixB).getMaxFuel()) {
							((PlayerBodyData)fixB).fuelGain(fuelRegained);

							if (event.getConnectedEvent() != null) {
								event.getConnectedEvent().getEventData().onActivate(this);
							}
							
							queueDeletion();
						}
					}
				}
			}
		};
		
		this.body = BodyBuilder.createBox(world, startX, startY, width, height, 1, 1, 0, true, true, Constants.BIT_SENSOR, 
				(short) (Constants.BIT_PLAYER),	(short) 0, true, eventData);
	}
	
}
