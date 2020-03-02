package com.mygdx.hadal.strategies.hitbox;

import java.util.ArrayList;

import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.schmucks.userdata.HadalData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;
import com.mygdx.hadal.strategies.HitboxStrategy;

/**
 * This strategy makes a hbox damage enemies it comes in contact with
 * @author Zachary Tu
 *
 */
public class DamageStandard extends HitboxStrategy {
	
	//the amount of damage and knockback this hbox will inflict
	private float baseDamage, knockback;
	
	//damage tags determine the type of damage inflicted and is used for certain effects
	private DamageTypes[] tags;
	
	private ArrayList<HadalData> damaged;
	
	public DamageStandard(PlayState state, Hitbox proj, BodyData user, float damage, float knockback, DamageTypes... tags) {
		super(state, proj, user);
		this.baseDamage = damage;
		this.knockback = knockback;
		this.tags = tags;
		
		damaged = new ArrayList<HadalData>();
	}
	
	@Override
	public void onHit(HadalData fixB) {
		if (fixB != null) {
			if (!damaged.contains(fixB)) {
				damaged.add(fixB);
				fixB.receiveDamage(baseDamage, hbox.getLinearVelocity().nor().scl(knockback), creator, true, tags);
			}
		}
	}
}
