package com.mygdx.hadal.event;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.event.Event;
import com.mygdx.hadal.event.userdata.EventData;
import com.mygdx.hadal.schmucks.UserDataTypes;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;
import com.mygdx.hadal.utils.Constants;
import com.mygdx.hadal.utils.b2d.BodyBuilder;
import com.mygdx.hadal.utils.b2d.FixtureBuilder;

/**
 * A See saw platform can rotate with physics input
 * 
 * Triggered Behavior: N/A
 * Triggering Behavior: N/A
 * 
 * Fields:
 * @author Zachary Tu
 */
public class SeeSawPlatform extends Event {

	//properties of each segment fixture that process damage knockback
	private final static float sectionWidth = 64.0f;
	private final static float sectionPadding = 10.0f;
	
	//max knockback that an instance of damage can apply to this event
	private final static float kbCap = 20.0f;
	
	public SeeSawPlatform(PlayState state, Vector2 startPos, Vector2 size) {
		super(state, startPos, size);
		setSyncDefault(false);
		setSyncInstant(true);
	}
	
	@Override
	public void create() {
		this.eventData = new EventData(this, UserDataTypes.WALL) {
			
			@Override
			public float receiveDamage(float basedamage, Vector2 knockback, BodyData perp, Boolean procEffects, DamageTypes... tags) { return 0; }
		};
		
		this.body = BodyBuilder.createBox(world, startPos, size, 1.0f, 1, 0, false, false, 
				Constants.BIT_WALL, (short) (Constants.BIT_PLAYER | Constants.BIT_ENEMY | Constants.BIT_PROJECTILE | Constants.BIT_WALL | Constants.BIT_SENSOR),
				(short) 0, false, eventData);
		
		//attach the body to the world anchor
		RevoluteJointDef joint = new RevoluteJointDef();
		joint.bodyA = state.getAnchor().getBody();
		joint.bodyB = body;
		joint.localAnchorA.set(body.getPosition().x, body.getPosition().y);
		joint.localAnchorB.set(0, 0);
		joint.enableLimit = true;
		joint.lowerAngle = (float) (-Math.PI / 3);
		joint.upperAngle = (float) (Math.PI / 3);
		state.getWorld().createJoint(joint);
		
		//create the segment fixtures. Each responds to knockback and applies it to the respective parts of the platform
		for (float i = -size.x / 2 + sectionWidth / 2; i < size.x / 2; i += sectionWidth) {
			
			final float sectioncenter = i;
			
			EventData tempData = new EventData(this, UserDataTypes.WALL) {
				
				@Override
				public float receiveDamage(float basedamage, Vector2 knockback, BodyData perp, Boolean procEffects, DamageTypes... tags) {
					
					if (getEntity().isAlive()) {
						if (getEntity().getBody() != null) {
							getEntity().getBody().applyLinearImpulse(new Vector2(knockback).limit(kbCap), new Vector2(sectioncenter, 0), true);
						}
					}
					return 0;
				}
			};
			FixtureBuilder.createFixtureDef(body, new Vector2(i, 0), new Vector2(sectionWidth, size.y + sectionPadding), true, 0, 0, 0, 0,
					Constants.BIT_WALL, Constants.BIT_PROJECTILE, (short) 0).setUserData(tempData);
		}
	}
	
	@Override
	public void loadDefaultProperties() {
		setEventSprite(Sprite.UI_MAIN_HEALTHBAR);
		setScaleAlign("ROTATE");
		setSyncType(eventSyncTypes.ALL);
		setSynced(true);
	}
}
