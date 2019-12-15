package com.mygdx.hadal.schmucks.strategies;

import static com.mygdx.hadal.utils.Constants.PPM;

import com.mygdx.hadal.equip.Equipable;
import com.mygdx.hadal.equip.WeaponUtils;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;

public class HitboxOnDieExplodeStrategy extends HitboxStrategy{
	
	private float explosionDamage, explosionKnockback;
	private int explosionRadius;
	private Equipable tool;
	private short filter;
	
	public HitboxOnDieExplodeStrategy(PlayState state, Hitbox proj, BodyData user, Equipable tool, int explosionRadius, float explosionDamage, 
			float explosionKnockback, short filter) {
		super(state, proj, user);
		this.explosionRadius = explosionRadius;
		this.explosionDamage = explosionDamage;
		this.explosionKnockback = explosionKnockback;
		this.tool = tool;
		this.filter = filter;
	}
	
	@Override
	public void die() {
		WeaponUtils.createExplosion(state, this.hbox.getPosition().x * PPM , this.hbox.getPosition().y * PPM, 
				creator.getSchmuck(), tool, explosionRadius, explosionDamage, explosionKnockback, filter);
	}
}
