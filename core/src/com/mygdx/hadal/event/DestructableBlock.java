package com.mygdx.hadal.event;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.Equipable;
import com.mygdx.hadal.event.userdata.EventData;
import com.mygdx.hadal.schmucks.UserDataTypes;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;
import com.mygdx.hadal.utils.Constants;
import com.mygdx.hadal.utils.b2d.BodyBuilder;

/**
 * This is a block that can be destroyed.
 * 
 * Triggered Behavior: N/A
 * Triggering Behavior: Upon being destroyed, the destructable rock will trigger its connected event.
 * 
 * Fields:
 * Hp: The integer number of Hp this event has before being destroyed.
 * 
 * @author Zachary Tu
 *
 */
public class DestructableBlock extends Event {

	//psuedo-hp. This event does not proc on-damage effects but can be destroyed.
	private int hp;
	
	public DestructableBlock(PlayState state, Vector2 startPos, Vector2 size, int hp) {
		super(state, startPos, size);
		this.hp = hp;
	}

	@Override
	public void create() {

		this.eventData = new EventData(this, UserDataTypes.WALL) {
			
			@Override
			public void receiveDamage(float basedamage, Vector2 knockback, BodyData perp, Equipable tool,
					Boolean procEffects, DamageTypes... tags) {
				hp -= basedamage;
				
				if (standardParticle != null) {
					standardParticle.onForBurst(1.0f);
				}
				
				if (hp <= 0) {
					event.queueDeletion();
					
					if (event.getConnectedEvent() != null) {
						event.getConnectedEvent().getEventData().preActivate(this, null);
					}
				}
			}
		};
		
		this.body = BodyBuilder.createBox(world, startPos, size, 1, 1, 0, true, true, 
				Constants.BIT_WALL, (short) (Constants.BIT_PLAYER | Constants.BIT_ENEMY | Constants.BIT_PROJECTILE | Constants.BIT_WALL | Constants.BIT_SENSOR),
				(short) 0, false, eventData);
	}

	@Override
	public void loadDefaultProperties() {
		setEventSprite(Sprite.UI_MAIN_HEALTH_MISSING);
		setScaleAlign("CENTER_STRETCH");
	}
}
