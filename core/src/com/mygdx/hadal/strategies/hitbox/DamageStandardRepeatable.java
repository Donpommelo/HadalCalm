package com.mygdx.hadal.strategies.hitbox;

import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.schmucks.userdata.HadalData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;
import com.mygdx.hadal.strategies.HitboxStrategy;

/**
 * This strategy makes a hbox damage enemies it comes in contact with
 * This allows a unit to be damaged from the same hbox multiple times. used for stuff like bees and boomerangs.
 * @author Zachary Tu
 */
public class DamageStandardRepeatable extends HitboxStrategy {
	
	//the amount of damage and knockback this hbox will inflict
	private float baseDamage, knockback;
	
	//damage tags determine the type of damage inflicted and is used for certain effects
	private DamageTypes[] tags;
	
	public DamageStandardRepeatable(PlayState state, Hitbox proj, BodyData user, float damage, float knockback, DamageTypes... tags) {
		super(state, proj, user);
		this.baseDamage = damage;
		this.knockback = knockback;
		this.tags = tags;
	}
	
	@Override
	public void onHit(HadalData fixB) {
		if (fixB != null) {
			fixB.receiveDamage(baseDamage * hbox.getDamageMultiplier(), hbox.getLinearVelocity().nor().scl(knockback), creator, true, tags);
		}
	}
}
