package com.mygdx.hadal.event;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.Shader;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.event.userdata.EventData;
import com.mygdx.hadal.schmucks.UserDataTypes;
import com.mygdx.hadal.schmucks.bodies.ParticleEntity;
import com.mygdx.hadal.schmucks.bodies.ParticleEntity.particleSyncType;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;
import com.mygdx.hadal.utils.Constants;
import com.mygdx.hadal.utils.b2d.BodyBuilder;

/**
 * This is a block that can be destroyed.
 * 
 * Triggered Behavior: N/A
 * Triggering Behavior: Upon being destroyed, the destructible rock will trigger its connected event.
 * 
 * Fields:
 * Hp: The integer number of Hp this event has before being destroyed.
 * 
 * @author Zachary Tu
 */
public class DestructableBlock extends Event {

	//pseudo-hp. This event does not proc on-damage effects but can be destroyed.
	private int hp;
	
	//when damaged, the event flashes for this duration
	private static final float flashDuration = 0.1f;
	
	//does this event stay in place or is it affected by physics?
	private final boolean isStatic;
	
	public DestructableBlock(PlayState state, Vector2 startPos, Vector2 size, int hp, boolean isStatic) {
		super(state, startPos, size);
		this.hp = hp;
		this.isStatic = isStatic;
	}

	@Override
	public void create() {

		this.eventData = new EventData(this, UserDataTypes.WALL) {
			
			@Override
			public float receiveDamage(float basedamage, Vector2 knockback, BodyData perp, Boolean procEffects, DamageTypes... tags) {
				hp -= basedamage;
				
				if (basedamage > 0) {
					//flash and spawn particles when damaged.
					if (standardParticle != null) {
						standardParticle.onForBurst(0.5f);
					}
					event.setShader(Shader.WHITE, flashDuration);
				}
				
				if (hp <= 0) {
					event.queueDeletion();
					
					new ParticleEntity(state, new Vector2(event.getPixelPosition()), Particle.BOULDER_BREAK, 3.0f, true, particleSyncType.CREATESYNC);
					
					//activated connected event when destroyed.
					if (event.getConnectedEvent() != null) {
						event.getConnectedEvent().getEventData().preActivate(this, null);
					}
				}
				return basedamage;
			}
		};
		
		this.body = BodyBuilder.createBox(world, startPos, size, gravity, 1, 0, isStatic, true, 
				Constants.BIT_WALL, (short) (Constants.BIT_PLAYER | Constants.BIT_ENEMY | Constants.BIT_PROJECTILE | Constants.BIT_WALL | Constants.BIT_SENSOR),
				(short) 0, false, eventData);
	}
	
	@Override
	public void loadDefaultProperties() {
		setEventSprite(Sprite.UI_MAIN_HEALTH_MISSING);
		setScaleAlign("CENTER_STRETCH");
		setStandardParticle(Particle.IMPACT);
		setGravity(0.0f);
	}
}
