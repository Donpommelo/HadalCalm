package com.mygdx.hadal.event.utility;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.mygdx.hadal.event.Event;
import com.mygdx.hadal.event.userdata.EventData;
import com.mygdx.hadal.schmucks.bodies.Player;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.schmucks.userdata.HadalData;
import com.mygdx.hadal.schmucks.userdata.HitboxData;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;
import com.mygdx.hadal.utils.Constants;
import com.mygdx.hadal.utils.b2d.BodyBuilder;
import com.mygdx.hadal.utils.b2d.FixtureBuilder;

/**
 * A Sensor is an activating event that will activate a connected event when touching a specified type of body.
 * 
 * Triggered Behavior: N/A.
 * Triggering Behavior: When touching a specified type of body, this event will trigger its connected event.
 * 
 * Fields:
 * player: Boolean that describes whether this sensor touches player. Optional. Default: true
 * hbox: Boolean that describes whether this sensor touches hit-boxes. Optional. Default: false
 * event: Boolean that describes whether this sensor touches events. Optional. Default: false
 * enemy: Boolean that describes whether this sensor touches enemies. Optional. Default: false
 * gravity: float that determines the gravity of the object. Optional. Default: 0.0f. Currently only used for falling targets in NASU
 * collision: Do we add a collision hbox to this event? This is used on dynamically spawned pickups so they can have gravity while not passing through walls.
 * @author Zachary Tu
 */
public class Sensor extends Event {

	private final short filter;
	private final boolean collision;
	
	public Sensor(PlayState state, Vector2 startPos, Vector2 size, boolean player, boolean hbox, boolean event, boolean enemy,	float gravity, boolean collision) {
		super(state, startPos, size);
		this.filter = (short) ((player ? Constants.BIT_PLAYER : 0) | (hbox ? Constants.BIT_PROJECTILE: 0) | (event ? Constants.BIT_SENSOR : 0) | (enemy ? Constants.BIT_ENEMY : 0));
		this.gravity = gravity;
		this.collision = collision;
	}

	@Override
	public void create() {
		this.eventData = new EventData(this) {
			
			@Override
			public float receiveDamage(float basedamage, Vector2 knockback, BodyData perp, Boolean procEffects, DamageTypes... tags) {
				//this event should receive no kb from attacks.
				return basedamage;
			}
			
			@Override
			public void onTouch(HadalData fixB) {
				super.onTouch(fixB);
				
				if (isAlive()) {
					if (event.getConnectedEvent() != null) {
						if (fixB instanceof PlayerBodyData) {
							event.getConnectedEvent().getEventData().preActivate(this, ((PlayerBodyData) fixB).getPlayer());
						} else if (fixB instanceof HitboxData) {
							if (((HitboxData) fixB).getHbox().getCreator().getBodyData() instanceof PlayerBodyData) {
								event.getConnectedEvent().getEventData().preActivate(this, ((Player) ((HitboxData) fixB).getHbox().getCreator()));
							}
						} else {
							event.getConnectedEvent().getEventData().preActivate(this, null);
						}
						
						if (standardParticle != null) {
							standardParticle.onForBurst(1.0f);
						}
					}
				}
			}
		};
		
		this.body = BodyBuilder.createBox(world, startPos, size, gravity, 0, 0, false, false, Constants.BIT_SENSOR, filter,	(short) 0, true, eventData);
		this.body.setType(BodyDef.BodyType.KinematicBody);
		
		if (collision) {
			FixtureBuilder.createFixtureDef(body, new Vector2(), new Vector2(size).scl(2), false, 0, 0, 0.0f, 1.0f, Constants.BIT_SENSOR, Constants.BIT_WALL, (short) 0);
		}
	}
}
