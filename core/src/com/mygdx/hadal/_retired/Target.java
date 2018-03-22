package com.mygdx.hadal._retired;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.hadal.event.Event;
import com.mygdx.hadal.event.userdata.EventData;
import com.mygdx.hadal.schmucks.UserDataTypes;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.schmucks.userdata.HadalData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;
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

	private boolean oneTime;
	
	private float gravity;
	
	public Target(PlayState state, World world, OrthographicCamera camera, RayHandler rays, int width, int height,
			int x, int y, boolean oneTime, float gravity) {
		super(state, world, camera, rays, name, width, height, x, y);
		this.oneTime = oneTime;
		this.gravity = gravity;
	}
	
	@Override
	public void create() {
		this.eventData = new EventData(world, this, UserDataTypes.EVENT) {
			
			@Override
			public void receiveDamage(float basedamage, Vector2 knockback, BodyData perp, Boolean procEffects, DamageTypes... tags) {
				//this event should receive no kb from attacks.
			}
			
			@Override
			public void onTouch(HadalData fixB) {
				super.onTouch(fixB);
				if (isAlive()) {
					if (event.getConnectedEvent() != null) {
						event.getConnectedEvent().getEventData().onActivate(this);
					}
					
					if (oneTime) {
						event.queueDeletion();
					}
				}
				
			}
		};
		
		this.body = BodyBuilder.createBox(world, startX, startY, width, height, gravity, 0, 0, false, false, Constants.BIT_SENSOR, 
				(short) (Constants.BIT_PROJECTILE), (short) 0, true, eventData);
	}
}
