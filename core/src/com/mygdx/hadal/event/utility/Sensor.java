package com.mygdx.hadal.event.utility;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.hadal.event.Event;
import com.mygdx.hadal.event.userdata.EventData;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.schmucks.userdata.HadalData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;
import com.mygdx.hadal.utils.Constants;
import com.mygdx.hadal.utils.b2d.BodyBuilder;

import box2dLight.RayHandler;

/**
 * A Sensor is an activating event that will activate a connected event when touching a speficied type of body.
 * 
 * Triggered Behavior: N/A.
 * Triggering Behavior: When touching a specified type of body, this event will trigger its connected event.
 * 
 * Fields:
 * oneTime: Boolean to be replaced
 * player: Boolean that describes whether this sensor touches player. Optional. Default: true
 * hbox: Boolean that describes whether this sensor touches hit-boxes. Optional. Default: false
 * event: Boolean that describes whether this sensor touches events. Optional. Default: false
 * enemy: Boolean that describes whether this sensor touches enemies. Optional. Default: false
 * gravity: float that determines the gravity of the object. Optional. Default: 0.0f. Currently only used for falling targets in NASU
 * @author Zachary Tu
 *
 */
public class Sensor extends Event {

	private static final String name = "Sensor";

	private boolean oneTime;
	private short filter;
	private float gravity;
	
	public Sensor(PlayState state, World world, OrthographicCamera camera, RayHandler rays, int width, int height,
			int x, int y, boolean oneTime, boolean player, boolean hbox, boolean event, boolean enemy, float gravity) {
		super(state, world, camera, rays, name, width, height, x, y);
		this.oneTime = oneTime;
		this.filter = (short) ((player ? Constants.BIT_PLAYER : 0) | (hbox ? Constants.BIT_PROJECTILE: 0)
				| (event ? Constants.BIT_SENSOR : 0) | (enemy ? Constants.BIT_ENEMY : 0));

		this.gravity = gravity;
	}
	
	@Override
	public void create() {
		this.eventData = new EventData(world, this) {
			
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
				filter,	(short) 0, true, eventData);
	}
}
