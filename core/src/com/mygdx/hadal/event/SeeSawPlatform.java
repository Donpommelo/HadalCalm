package com.mygdx.hadal.event;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;
import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.battle.DamageTag;
import com.mygdx.hadal.constants.BodyConstants;
import com.mygdx.hadal.constants.UserDataType;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.event.userdata.EventData;
import com.mygdx.hadal.schmucks.entities.ClientIllusion;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.utils.b2d.HadalBody;
import com.mygdx.hadal.utils.b2d.HadalFixture;

/**
 * A See saw platform can rotate with physics input
 * <p>
 * Triggered Behavior: N/A
 * Triggering Behavior: N/A
 * <p>
 * Fields:
 * @author Quimpus Quectavio
 */
public class SeeSawPlatform extends Event {

	//properties of each segment fixture that process damage knockback
	private static final float SECTION_WIDTH = 64.0f;
	private static final float SECTION_PADDING = 10.0f;
	
	//max knockback that an instance of damage can apply to this event
	private static final float KB_CAP = 20.0f;
	
	public SeeSawPlatform(PlayState state, Vector2 startPos, Vector2 size) {
		super(state, startPos, size);
		setSyncDefault(false);
		setSyncInstant(true);
	}
	
	@Override
	public void create() {
		this.eventData = new EventData(this, UserDataType.WALL) {
			
			@Override
			public float receiveDamage(float basedamage, Vector2 knockback, BodyData perp, Boolean procEffects, Hitbox hbox,
									   DamageSource source, DamageTag... tags) { return 0; }
		};

		this.body = new HadalBody(eventData, startPos, size, BodyConstants.BIT_WALL,
				(short) (BodyConstants.BIT_PLAYER | BodyConstants.BIT_ENEMY | BodyConstants.BIT_PROJECTILE | BodyConstants.BIT_WALL | BodyConstants.BIT_SENSOR), (short) 0)
				.setGravity(1.0f)
				.setFixedRotate(false)
				.setSensor(false)
				.addToWorld(world);

		if (state.isServer()) {
			//attach the body to the world anchor
			RevoluteJointDef joint = new RevoluteJointDef();
			joint.bodyA = state.getAnchor().getBody();
			joint.bodyB = body;
			joint.localAnchorA.set(getPosition().x, getPosition().y);
			joint.localAnchorB.set(0, 0);
			joint.enableLimit = true;
			joint.lowerAngle = -MathUtils.PI / 3;
			joint.upperAngle = MathUtils.PI / 3;
			state.getWorld().createJoint(joint);

			//create the segment fixtures. Each responds to knockback and applies it to the respective parts of the platform
			for (float i = -size.x / 2 + SECTION_WIDTH / 2; i < size.x / 2; i += SECTION_WIDTH) {

				EventData tempData = getEventData(i);
				new HadalFixture(new Vector2(i, 0), new Vector2(SECTION_WIDTH, size.y + SECTION_PADDING),
						BodyConstants.BIT_WALL, BodyConstants.BIT_PROJECTILE, (short) 0)
						.addToBody(body)
						.setUserData(tempData);
			}
		} else {
			this.body.setType(BodyDef.BodyType.KinematicBody);
		}
	}

	private EventData getEventData(float i) {
		final float sectioncenter = i;

        return new EventData(SeeSawPlatform.this, UserDataType.WALL) {

			@Override
			public float receiveDamage(float basedamage, Vector2 knockback, BodyData perp, Boolean procEffects, Hitbox hbox,
									   DamageSource source, DamageTag... tags) {

				if (getEntity().isAlive()) {
					if (getEntity().getBody() != null) {
						getEntity().getBody().applyLinearImpulse(new Vector2(knockback).limit(KB_CAP), new Vector2(sectioncenter, 0), true);
					}
				}
				return 0;
			}
		};
	}

	@Override
	public void loadDefaultProperties() {
		setEventSprite(Sprite.UI_MAIN_HEALTHBAR);
		setScaleAlign(ClientIllusion.alignType.ROTATE);
		setIndependent(false);
		setSynced(true);
	}
}
