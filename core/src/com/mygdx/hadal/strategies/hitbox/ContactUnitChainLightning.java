package com.mygdx.hadal.strategies.hitbox;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.bodies.hitboxes.RangedHitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.schmucks.userdata.HadalData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;
import com.mygdx.hadal.strategies.HitboxStrategy;

/**
 * This strategy makes an attached hbox lose durability upon contact with a unit.
 * If a hbox's durability reaches 0, it dies.
 * @author Zachary Tu
 *
 */
public class ContactUnitChainLightning extends HitboxStrategy {
	
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
				hboxNew.addStrategy(new DamageStandard(state, hboxNew, creator, baseDamage, knockback, DamageTypes.ENERGY));
				hboxNew.addStrategy(new CreateParticles(state, hboxNew, creator, Particle.LIGHTNING, 0, 30));
			}
		}
	}
}
