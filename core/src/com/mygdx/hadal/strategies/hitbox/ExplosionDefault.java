package com.mygdx.hadal.strategies.hitbox;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.schmucks.userdata.HadalData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.battle.DamageTag;
import com.mygdx.hadal.strategies.HitboxStrategy;

/**
 * This strategy is used by explosives.
 * Explosions deal damage to all units but less damage to the user.
 * Because explosions are static, their knock back is based on positioning rather than their own momentum
 * @author Blirfield Blildwin
 */
public class ExplosionDefault extends HitboxStrategy {
	
	//the amount of damage and knockback this hbox will inflict and the percentage reduction on self damage
	private final float baseDamage, knockback, selfDamageReduction;

	//this is the effect/item/weapon source of the explosion
	private final DamageSource source;

	//damage tags determine the type of damage inflicted and is used for certain effects
	private final DamageTag[] tags;
	
	public ExplosionDefault(PlayState state, Hitbox proj, BodyData user, float damage, float knockback, float selfDamageReduction,
							DamageSource source, DamageTag... tags) {
		super(state, proj, user);
		this.baseDamage = damage;
		this.knockback = knockback;
		this.selfDamageReduction = selfDamageReduction;
		this.source = source;
		this.tags = tags;
	}
	
	private final Vector2 kb = new Vector2();
	@Override
	public void onHit(HadalData fixB, Body body) {
		if (fixB != null) {
			kb.set(fixB.getEntity().getPixelPosition().x - this.hbox.getPixelPosition().x, fixB.getEntity().getPixelPosition().y - this.hbox.getPixelPosition().y);
			
			if (fixB instanceof BodyData bodyData) {
				if (bodyData.getSchmuck().getHitboxFilter() == creator.getSchmuck().getHitboxFilter()) {
					fixB.receiveDamage(baseDamage * selfDamageReduction, kb.nor().scl(knockback), creator,
							true, hbox, source, tags);
				} else {
					fixB.receiveDamage(baseDamage, kb.nor().scl(knockback),	creator, true, hbox, source, tags);
				}
			} else {
				fixB.receiveDamage(baseDamage, kb.nor().scl(knockback),	creator, true, hbox, source, tags);
			}
		}
	}
}
