package com.mygdx.hadal.event;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.event.Event;
import com.mygdx.hadal.event.userdata.EventData;
import com.mygdx.hadal.schmucks.UserDataTypes;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.utils.Constants;
import com.mygdx.hadal.utils.b2d.BodyBuilder;

/**
 * A Semipermeable wall is a wall that some objects can pass through
 * 
 * Triggered Behavior: N/A.
 * Triggering Behavior: N/A.
 * 
 * Fields:
 * player: Boolean that describes whether players can pass through this. Default: true
 * hbox: Boolean that describes whether hitboxes can pass through this. Optional. Default: true
 * event: Boolean that describes whether this sensor touches events. Optional. Default: true
 * enemy: Boolean that describes whether this sensor touches enemies. Optional. Default: true
 * @author Zachary Tu
 *
 */
public class Platform extends Event {

	private short filter;
	private float restitution, friction;
	
	public Platform(PlayState state, Vector2 startPos, Vector2 size, float restitution, float friction, boolean player, boolean hbox, boolean event, boolean enemy) {
		super(state, startPos ,size);
		this.filter = (short) ((player ? Constants.BIT_PLAYER : 0) | (hbox ? Constants.BIT_PROJECTILE: 0) | (event ? Constants.BIT_SENSOR : 0) | (enemy ? Constants.BIT_ENEMY : 0));
		this.restitution = restitution;
		this.friction = friction;
	}
	
	@Override
	public void create() {
		this.eventData = new EventData(this, UserDataTypes.WALL);
		
		this.body = BodyBuilder.createBox(world, startPos, size, 0, 0, restitution, friction, false, true, Constants.BIT_WALL, filter,	(short) 0, false, eventData);
		this.body.setType(BodyDef.BodyType.KinematicBody);
	}
	
	@Override
	public void loadDefaultProperties() {
		setEventSprite(Sprite.UI_MAIN_HEALTHBAR);
		setScaleAlign("CENTER_STRETCH");
	}
}
