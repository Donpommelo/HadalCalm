package com.mygdx.hadal.event;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.event.Event;
import com.mygdx.hadal.event.userdata.EventData;
import com.mygdx.hadal.schmucks.UserDataTypes;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.utils.Constants;
import com.mygdx.hadal.utils.b2d.BodyBuilder;

/**
 * A See saw platform can rotate with physics input
 * 
 * Triggered Behavior: N/A
 * Triggering Behavior: N/A
 * 
 * Fields:
 * @author Zachary Tu
 *
 */
public class SeeSawPlatform extends Event {

	public SeeSawPlatform(PlayState state, Vector2 startPos, Vector2 size) {
		super(state, startPos ,size);
	}
	
	@Override
	public void create() {
		this.eventData = new EventData(this, UserDataTypes.WALL);
		
		this.body = BodyBuilder.createBox(world, startPos, size, 1.0f, 1, 0, false, false, 
				Constants.BIT_WALL, (short) (Constants.BIT_PLAYER | Constants.BIT_ENEMY | Constants.BIT_PROJECTILE | Constants.BIT_WALL | Constants.BIT_SENSOR),
				(short) 0, false, eventData);
		
		this.body.setType(BodyDef.BodyType.DynamicBody);
		
		RevoluteJointDef joint = new RevoluteJointDef();
		joint.bodyA = state.getAnchor().getBody();
		joint.bodyB = body;
		joint.localAnchorA.set(body.getPosition().x, body.getPosition().y);
		joint.localAnchorB.set(0, 0);
		joint.enableLimit = true;
		joint.lowerAngle = (float) (-Math.PI / 3);
		joint.upperAngle = (float) (Math.PI / 3);
		state.getWorld().createJoint(joint);
	}
	
	@Override
	public void loadDefaultProperties() {
		setEventSprite(Sprite.UI_MAIN_HEALTHBAR);
		setScaleAlign("ROTATE");
		setSyncType(eventSyncTypes.ALL);
	}
}
