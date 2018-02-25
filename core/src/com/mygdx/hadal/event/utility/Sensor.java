package com.mygdx.hadal.event.utility;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.hadal.event.Event;
import com.mygdx.hadal.event.userdata.EventData;
import com.mygdx.hadal.schmucks.userdata.HadalData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.utils.Constants;
import com.mygdx.hadal.utils.b2d.BodyBuilder;

import box2dLight.RayHandler;

/**
 * A Sensor is an activating event that will activate a connected event when the player (or anyone else?) touches it
 * TODO: enemy sensors + filters.
 * @author Zachary Tu
 *
 */
public class Sensor extends Event {

	private static final String name = "Sensor";

	private boolean oneTime;
	
	public Sensor(PlayState state, World world, OrthographicCamera camera, RayHandler rays, int width, int height,
			int x, int y, boolean oneTime) {
		super(state, world, camera, rays, name, width, height, x, y);
		this.oneTime = oneTime;
	}
	
	@Override
	public void create() {
		this.eventData = new EventData(world, this) {
			
			@Override
			public void onTouch(HadalData fixB) {
				super.onTouch(fixB);
				
				if (!consumed) {
					if (event.getConnectedEvent() != null) {
						event.getConnectedEvent().getEventData().onActivate(this);
					}
					
					if (oneTime) {
						event.queueDeletion();
					}
				}
			}
		};
		
		this.body = BodyBuilder.createBox(world, startX, startY, width, height, 1, 1, 0, true, true, Constants.BIT_SENSOR, 
				(short) (Constants.BIT_PLAYER),
				(short) 0, true, eventData);
	}
}
