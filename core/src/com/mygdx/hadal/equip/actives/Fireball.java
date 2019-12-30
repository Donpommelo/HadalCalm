package com.mygdx.hadal.equip.actives;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.ActiveItem;
import com.mygdx.hadal.schmucks.bodies.ParticleEntity;
import com.mygdx.hadal.schmucks.bodies.ParticleEntity.particleSyncType;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.bodies.hitboxes.RangedHitbox;
import com.mygdx.hadal.schmucks.strategies.HitboxDamageStandardStrategy;
import com.mygdx.hadal.schmucks.strategies.HitboxDefaultStrategy;
import com.mygdx.hadal.schmucks.strategies.HitboxOnContactUnitDieStrategy;
import com.mygdx.hadal.schmucks.strategies.HitboxOnContactWallDieStrategy;
import com.mygdx.hadal.schmucks.strategies.HitboxOnDieFireFragStrategy;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;

public class Fireball extends ActiveItem {

	private final static float usecd = 0.0f;
	private final static float usedelay = 0.1f;
	private final static float maxCharge = 6.0f;
	

	private final static Vector2 projectileSize = new Vector2(50, 50);
	private final static float lifespan = 5.0f;
	private final static float projectileSpeed = 12.0f;

	private final static int numFrag = 20;
	
	private final static float baseDamage = 40.0f;
	private final static float knockback = 40.0f;
	
	public Fireball(Schmuck user) {
		super(user, usecd, usedelay, maxCharge, chargeStyle.byTime);
	}
	
	@Override
	public void useItem(PlayState state, PlayerBodyData user) {
			
		Hitbox hbox = new RangedHitbox(state, user.getPlayer().getPixelPosition(), projectileSize, lifespan, this.weaponVelo.scl(projectileSpeed),
				user.getPlayer().getHitboxfilter(), true, true, user.getPlayer(), Sprite.NOTHING);
		
		hbox.addStrategy(new HitboxDefaultStrategy(state, hbox, user));
		hbox.addStrategy(new HitboxOnContactUnitDieStrategy(state, hbox, user));
		hbox.addStrategy(new HitboxOnContactWallDieStrategy(state, hbox, user));
		hbox.addStrategy(new HitboxDamageStandardStrategy(state, hbox, user, this, baseDamage, knockback, DamageTypes.RANGED));
		hbox.addStrategy(new HitboxOnDieFireFragStrategy(state, hbox, user, this, numFrag, user.getPlayer().getHitboxfilter()));
		new ParticleEntity(state, hbox, Particle.FIRE, 3.0f, 0.0f, true, particleSyncType.TICKSYNC);
	}
}
