package com.mygdx.hadal.event.utility;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.hadal.event.Event;
import com.mygdx.hadal.event.userdata.EventData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.utils.Constants;
import com.mygdx.hadal.utils.b2d.BodyBuilder;

import box2dLight.RayHandler;

/**
 * The Entity spawner periodically spawns entities.
 * @author Zachary Tu
 *
 */
public class Timer extends Event {
	
	//How frequently will the spawns occur? Every interval seconds.
	private float interval;
	
	//The event will spawn limit entites before stopping. If this is 0, the event will never stop.
	private int limit;
	
	private float timeCount = 0;
	private int amountCount = 0;
	private boolean on;
	
	private static final String name = "Timer";

	public Timer(PlayState state, World world, OrthographicCamera camera, RayHandler rays, int width, int height,
			int x, int y, float interval, int limit, boolean startOn) {
		super(state, world, camera, rays, name, width, height, x, y);
		this.interval = interval;
		this.limit = limit;
		this.on = startOn;
	}
	
	@Override
	public void create() {

		this.eventData = new EventData(world, this){
			
			@Override
			public void onActivate(EventData activator) {
				((Timer)event).on = !((Timer)event).on;
				amountCount = 0;
				timeCount = 0;
			}
		};
		
		this.body = BodyBuilder.createBox(world, startX, startY, width, height, 1, 1, 0, true, true, Constants.BIT_SENSOR, 
				(short) (Constants.BIT_PLAYER | Constants.BIT_ENEMY | Constants.BIT_PROJECTILE),
				(short) 0, true, eventData);
	}
	
	@Override
	public void controller(float delta) {
		if (on) {
			timeCount += delta;
			if (timeCount >= interval) {
				timeCount = 0;
				amountCount++;
				if (getConnectedEvent() != null) {
					getConnectedEvent().getEventData().onActivate(eventData);
				}
			}
			if ((limit != 0 && amountCount >= limit)) {
				on = false;
			}
		}
	}
}
