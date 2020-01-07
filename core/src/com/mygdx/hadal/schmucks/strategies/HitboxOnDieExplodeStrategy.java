package com.mygdx.hadal.schmucks.strategies;

import com.mygdx.hadal.equip.WeaponUtils;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;

/**
 * This strategy creates an explosion when the attached hbox dies
 * @author Zachary Tu
 *
 */
public class HitboxOnDieExplodeStrategy extends HitboxStrategy{
	
	//explosion stats
	private float explosionDamage, explosionKnockback;
	private int explosionRadius;
	
	//the hitbox filter of units that can be damaged b ythe explosion.
	private short filter;
	
	public HitboxOnDieExplodeStrategy(PlayState state, Hitbox proj, BodyData user, int explosionRadius, float explosionDamage, 
			float explosionKnockback, short filter) {
		super(state, proj, user);
		this.explosionRadius = explosionRadius;
		this.explosionDamage = explosionDamage;
		this.explosionKnockback = explosionKnockback;
		this.filter = filter;
	}
	
	@Override
	public void die() {
		WeaponUtils.createExplosion(state, this.hbox.getPixelPosition(), explosionRadius, creator.getSchmuck(), explosionDamage, explosionKnockback, filter);
	}
}
