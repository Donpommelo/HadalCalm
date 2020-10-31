package com.mygdx.hadal.event;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.joints.PrismaticJointDef;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.event.userdata.EventData;
import com.mygdx.hadal.schmucks.UserDataTypes;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.utils.Constants;
import com.mygdx.hadal.utils.b2d.BodyBuilder;

/**
 * A scale platform can slide downwards when stepped on
 * 
 * Triggered Behavior: N/A
 * Triggering Behavior: N/A
 * 
 * Fields:
 * @author Fuwald Fuppings
 */
public class ScalePlatform extends Event {

	private final float minHeight, density;

	public ScalePlatform(PlayState state, Vector2 startPos, Vector2 size, float minHeight, float density) {
		super(state, startPos, size);
		setSyncDefault(false);
		setSyncInstant(true);
		this.minHeight = minHeight;
		this.density = density;
	}
	
	@Override
	public void create() {
		this.eventData = new EventData(this, UserDataTypes.WALL);

		this.body = BodyBuilder.createBox(world, startPos, size, -1.0f, density, 0, false, false,
				Constants.BIT_WALL, (short) (Constants.BIT_PLAYER | Constants.BIT_ENEMY | Constants.BIT_PROJECTILE | Constants.BIT_WALL | Constants.BIT_SENSOR),
				(short) 0, false, eventData);

		Vector2 position = getPosition();

		//attach the body to the world anchor
		PrismaticJointDef joint = new PrismaticJointDef();
		joint.bodyA = state.getAnchor().getBody();
		joint.bodyB = body;
		joint.localAnchorA.set(position.x, position.y);
		joint.localAnchorB.set(0, 0);
		joint.enableLimit = true;
		joint.upperTranslation = 0;
		joint.lowerTranslation = minHeight;
		joint.localAxisA.set(0, 1);

		state.getWorld().createJoint(joint);
	}
	
	@Override
	public void loadDefaultProperties() {
		setEventSprite(Sprite.UI_MAIN_HEALTHBAR);
		setScaleAlign("CENTER_STRETCH");
		setSyncType(eventSyncTypes.ALL);
		setSynced(true);
	}
}
