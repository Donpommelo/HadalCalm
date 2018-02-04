package com.mygdx.hadal.event;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.hadal.event.userdata.EventData;
import com.mygdx.hadal.schmucks.UserDataTypes;
import com.mygdx.hadal.schmucks.userdata.HadalData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.utils.Constants;
import com.mygdx.hadal.utils.b2d.BodyBuilder;

import box2dLight.RayHandler;

/**
 * A Target is an activating event that will activate a connected event when it touches a hitbox
 * @author Zachary Tu
 *
 */
public class Target extends Event {

	private static final String name = "Target";

	boolean oneTime;
	
	public Target(PlayState state, World world, OrthographicCamera camera, RayHandler rays, int width, int height,
			int x, int y, boolean oneTime) {
		super(state, world, camera, rays, name, width, height, x, y);
		this.oneTime = oneTime;
	}
	
	@Override
	public void create() {
		this.eventData = new EventData(world, this, UserDataTypes.EVENT) {
			
			@Override
			public void onTouch(HadalData fixB) {
				super.onTouch(fixB);
				if (!consumed) {
					if (event.getConnectedEvent() != null) {
						event.getConnectedEvent().eventData.onActivate(this);
					}
					
					if (oneTime) {
						event.queueDeletion();
					}
				}
				
			}
		};
		
		this.body = BodyBuilder.createBox(world, startX, startY, width, height, 1, 1, 0, true, true, Constants.BIT_SENSOR, 
				(short) (Constants.BIT_PROJECTILE),
				(short) 0, true, eventData);
	}
}
