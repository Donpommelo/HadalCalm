package com.mygdx.hadal.schmucks.strategies;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.schmucks.bodies.ParticleEntity;
import com.mygdx.hadal.schmucks.bodies.ParticleEntity.particleSyncType;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.bodies.hitboxes.RangedHitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.schmucks.userdata.HadalData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;

/**
 * This strategy makes an attached hbox lose durability upon contact with a unit.
 * If a hbox's durability reaches 0, it dies.
 * @author Zachary Tu
 *
 */
public class ContactUnitChainLightning extends HitboxStrategy{
	
	private final static Vector2 projectileSize = new Vector2(30, 30);
	private final static float lifespan = 1.0f;
	private final static float knockback = 0.0f;
	
	private int chainAmount;
	private float baseDamage;
	
	public ContactUnitChainLightning(PlayState state, Hitbox proj, BodyData user, int chainAmount, float damage) {
		super(state, proj, user);
		this.chainAmount = chainAmount;
		this.baseDamage = damage;
	}
	
	@Override
	public void onHit(HadalData fixB) {
		if (fixB != null) {
			if (fixB instanceof BodyData) {
				Hitbox hboxNew = new RangedHitbox(state, hbox.getPixelPosition(), projectileSize, lifespan, new Vector2(), creator.getSchmuck().getHitboxfilter(), true, false, creator.getSchmuck(), Sprite.NOTHING);
				hboxNew.setDurability(chainAmount);
				
				hboxNew.addStrategy(new ControllerDefault(state, hboxNew, creator));
				hboxNew.addStrategy(new ContactWallParticles(state, hboxNew, creator, Particle.SPARK_TRAIL));
				hboxNew.addStrategy(new ContactWallDie(state, hboxNew, creator));
				hboxNew.addStrategy(new ContactChain(state, hboxNew, creator, chainAmount, creator.getSchmuck().getHitboxfilter()));
				hboxNew.addStrategy(new DamageStandard(state, hboxNew, creator, baseDamage, knockback, DamageTypes.RANGED));
				
				new ParticleEntity(state, hboxNew, Particle.LIGHTNING, 3.0f, 0.0f, true, particleSyncType.TICKSYNC);
			}
		}
	}
}
