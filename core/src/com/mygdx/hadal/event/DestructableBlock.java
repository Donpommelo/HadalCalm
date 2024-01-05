package com.mygdx.hadal.event;

import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.battle.DamageTag;
import com.mygdx.hadal.constants.BodyConstants;
import com.mygdx.hadal.constants.Constants;
import com.mygdx.hadal.constants.SyncType;
import com.mygdx.hadal.constants.UserDataType;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.Shader;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.event.userdata.EventData;
import com.mygdx.hadal.schmucks.entities.ClientIllusion;
import com.mygdx.hadal.schmucks.entities.ParticleEntity;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.server.EventDto;
import com.mygdx.hadal.server.packets.Packets;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.utils.b2d.HadalBody;

/**
 * This is a block that can be destroyed.
 * 
 * Triggered Behavior: N/A
 * Triggering Behavior: Upon being destroyed, the destructible rock will trigger its connected event.
 * 
 * Fields:
 * Hp: The integer number of Hp this event has before being destroyed.
 * 
 * @author Wordita Whenzegnatio
 */
public class DestructableBlock extends Event {

	//pseudo-hp. This event does not proc on-damage effects but can be destroyed.
	private int hp;
	
	//does this event stay in place or is it affected by physics?
	private final boolean isStatic;
	
	public DestructableBlock(PlayState state, Vector2 startPos, Vector2 size, int hp, boolean isStatic) {
		super(state, startPos, size);
		this.hp = hp;
		this.isStatic = isStatic;
	}

	@Override
	public void create() {

		this.eventData = new EventData(this, UserDataType.WALL) {
			
			@Override
			public float receiveDamage(float basedamage, Vector2 knockback, BodyData perp, Boolean procEffects, Hitbox hbox,
									   DamageSource source, DamageTag... tags) {

				hp -= basedamage;
				
				if (basedamage > 0) {
					//flash and spawn particles when damaged.
					if (standardParticle != null) {
						standardParticle.onForBurst(0.5f);
					}
					event.getShaderHelper().setStaticShader(Shader.WHITE, Constants.FLASH);
				}
				
				if (hp <= 0 && state.isServer()) {
					event.queueDeletion();
					
					new ParticleEntity(state, new Vector2(event.getPixelPosition()), Particle.BOULDER_BREAK, 3.0f,
							true, SyncType.CREATESYNC);
					
					//activated connected event when destroyed.
					if (event.getConnectedEvent() != null) {
						event.getConnectedEvent().getEventData().preActivate(this, null);
					}
				}
				return basedamage;
			}
		};

		this.body = new HadalBody(eventData, startPos, size, BodyConstants.BIT_WALL,
				(short) (BodyConstants.BIT_PLAYER | BodyConstants.BIT_ENEMY | BodyConstants.BIT_PROJECTILE | BodyConstants.BIT_WALL | BodyConstants.BIT_SENSOR),
				(short) 0)
				.setBodyType(isStatic ? BodyDef.BodyType.StaticBody : BodyDef.BodyType.DynamicBody)
				.setGravity(gravity)
				.setSensor(false)
				.setFriction(1.0f)
				.addToWorld(world);
	}

	@Override
	public Object onServerCreate(boolean catchup) {
		if (blueprint == null) {
			blueprint = new RectangleMapObject(getPixelPosition().x - size.x / 2, getPixelPosition().y - size.y / 2, size.x, size.y);
			blueprint.setName("Destr_Obj");
			blueprint.getProperties().put("Hp", hp);
			blueprint.getProperties().put("static", isStatic);
		}
		return new Packets.CreateEvent(entityID, new EventDto(blueprint), synced);
	}

	@Override
	public void loadDefaultProperties() {
		setEventSprite(Sprite.UI_MAIN_HEALTH_MISSING);
		setScaleAlign(ClientIllusion.alignType.CENTER_STRETCH);
		setStandardParticle(Particle.IMPACT);
		setGravity(0.0f);
	}
}
