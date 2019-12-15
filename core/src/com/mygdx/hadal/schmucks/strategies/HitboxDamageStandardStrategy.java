package com.mygdx.hadal.schmucks.strategies;

import com.mygdx.hadal.equip.Equipable;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.schmucks.userdata.HadalData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;

public class HitboxDamageStandardStrategy extends HitboxStrategy{
	
	private float baseDamage, knockback;
	private Equipable tool;
	private DamageTypes[] tags;
	
	public HitboxDamageStandardStrategy(PlayState state, Hitbox proj, BodyData user, Equipable tool, 
			float damage, float knockback, DamageTypes... tags) {
		super(state, proj, user);
		this.baseDamage = damage;
		this.knockback = knockback;
		this.tool = tool;
		this.tags = tags;
	}
	
	@Override
	public void onHit(HadalData fixB) {
		if (fixB != null) {
			fixB.receiveDamage(baseDamage, hbox.getLinearVelocity().nor().scl(knockback), creator, tool, true, tags);
		}
	}
}
