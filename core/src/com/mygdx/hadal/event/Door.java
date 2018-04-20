package com.mygdx.hadal.event;

import com.mygdx.hadal.event.userdata.EventData;
import com.mygdx.hadal.schmucks.UserDataTypes;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.utils.Constants;
import com.mygdx.hadal.utils.b2d.BodyBuilder;

/**
 * do not open
 * 
 * Triggered Behavior: When triggered, this event despawns
 * Triggering Behavior: N/A
 * 
 * Fields:
 * N/A
 * 
 * @author Zachary Tu
 *
 */
public class Door extends Event {

	private static String name = "Door";
	
	public Door(PlayState state, int width, int height, int x, int y) {
		super(state, name, width, height, x, y);
	}
	
	@Override
	public void create() {
		this.eventData = new EventData(this, UserDataTypes.WALL) {
			
			@Override
			public void onActivate(EventData activator) {
				if (event.isAlive()) {
					event.queueDeletion();
				}
			}
		};
		
		this.body = BodyBuilder.createBox(world, startX, startY, width, height, 1, 1, 0, true, true, Constants.BIT_WALL, 
				(short) (Constants.BIT_PLAYER | Constants.BIT_ENEMY | Constants.BIT_PROJECTILE | Constants.BIT_SENSOR | Constants.BIT_WALL),
				(short) 0, false, eventData);
	}
}