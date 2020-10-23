package com.mygdx.hadal.strategies.hitbox;

import com.mygdx.hadal.equip.WeaponUtils;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.strategies.HitboxStrategy;

/**
 * This strategy creates an explosion when the attached hbox dies
 * @author Flumpernickel Floffinity
 */
public class DieExplode extends HitboxStrategy {
	
	//explosion stats
	private final float explosionDamage, explosionKnockback;
	private final int explosionRadius;
	
	//the hitbox filter of units that can be damaged by the explosion.
	private final short filter;
	
	public DieExplode(PlayState state, Hitbox proj, BodyData user, int explosionRadius, float explosionDamage, float explosionKnockback, short filter) {
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
