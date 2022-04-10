package com.mygdx.hadal.strategies.hitbox;

import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.battle.WeaponUtils;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
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

	//is the explosion communicated to client or is it processed independently
	private final boolean synced;

	//this is the effect/item/weapon source of the explosion
	private final DamageSource source;

	public DieExplode(PlayState state, Hitbox proj, BodyData user, int explosionRadius, float explosionDamage,
					  float explosionKnockback, short filter, boolean synced, DamageSource source) {
		super(state, proj, user);
		this.explosionRadius = explosionRadius;
		this.explosionDamage = explosionDamage;
		this.explosionKnockback = explosionKnockback;
		this.filter = filter;
		this.synced = synced;
		this.source = source;
	}
	
	@Override
	public void die() {
		WeaponUtils.createExplosion(state, this.hbox.getPixelPosition(), explosionRadius, creator.getSchmuck(),
				explosionDamage, explosionKnockback, filter, synced, source);
	}
}
