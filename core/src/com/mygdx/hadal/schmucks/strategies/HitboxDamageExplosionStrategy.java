package com.mygdx.hadal.schmucks.strategies;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.equip.Equipable;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.schmucks.userdata.HadalData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;

public class HitboxDamageExplosionStrategy extends HitboxStrategy{
	
	private float baseDamage, knockback, selfDamageReduction;
	private Equipable tool;
	private DamageTypes[] tags;
	
	public HitboxDamageExplosionStrategy(PlayState state, Hitbox proj, BodyData user, Equipable tool,
			float damage, float knockback, float selfDamageReduction, DamageTypes... tags) {
		super(state, proj, user);
		this.baseDamage = damage;
		this.knockback = knockback;
		this.selfDamageReduction = selfDamageReduction;
		this.tool = tool;
		this.tags = tags;
	}
	
	private Vector2 kb = new Vector2();
	@Override
	public void onHit(HadalData fixB) {
		if (fixB != null) {
			kb.set(fixB.getEntity().getPosition().x - this.hbox.getPosition().x,
					fixB.getEntity().getPosition().y - this.hbox.getPosition().y);
			
			if (fixB.equals(creator)) {
				fixB.receiveDamage(baseDamage * selfDamageReduction, kb.nor().scl(knockback), 
						creator, tool, true, tags);
			} else {
				fixB.receiveDamage(baseDamage, kb.nor().scl(knockback), 
						creator, tool, true, tags);
			}
		}
	}
}
