package com.mygdx.hadal.event;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.joints.PrismaticJointDef;
import com.mygdx.hadal.constants.BodyConstants;
import com.mygdx.hadal.constants.UserDataType;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.event.userdata.EventData;
import com.mygdx.hadal.schmucks.entities.ClientIllusion;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.utils.b2d.HadalBody;

/**
 * A scale platform can slide downwards when stepped on
 * <p>
 * Triggered Behavior: N/A
 * Triggering Behavior: N/A
 * <p>
 * Fields:
 * @author Fuwald Fuppings
 */
public class ScalePlatform extends Event {

	private final float minHeight, density;

	public ScalePlatform(PlayState state, Vector2 startPos, Vector2 size, float minHeight, float density) {
		super(state, startPos, size);
		this.minHeight = minHeight;
		this.density = density;
		setSyncDefault(false);
		setSyncInstant(true);
	}
	
	@Override
	public void create() {
		this.eventData = new EventData(this, UserDataType.WALL);

		this.body = new HadalBody(eventData, startPos, size, BodyConstants.BIT_WALL,
				(short) (BodyConstants.BIT_PLAYER | BodyConstants.BIT_ENEMY | BodyConstants.BIT_PROJECTILE | BodyConstants.BIT_WALL | BodyConstants.BIT_SENSOR), (short) 0)
				.setGravity(-1.0f)
				.setSensor(false)
				.setDensity(density)
				.addToWorld(world);

		if (state.isServer()) {
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
		} else {
			this.body.setType(BodyDef.BodyType.KinematicBody);
		}
	}
	
	@Override
	public void loadDefaultProperties() {
		setEventSprite(Sprite.UI_MAIN_HEALTHBAR);
		setScaleAlign(ClientIllusion.alignType.CENTER_STRETCH);
		setSyncType(eventSyncTypes.ALL);
		setSynced(true);
	}
}
