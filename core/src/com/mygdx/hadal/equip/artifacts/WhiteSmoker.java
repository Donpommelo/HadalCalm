package com.mygdx.hadal.equip.artifacts;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.schmucks.bodies.hitboxes.RangedHitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;
import com.mygdx.hadal.statuses.Status;
import com.mygdx.hadal.strategies.hitbox.ContactUnitBurn;
import com.mygdx.hadal.strategies.hitbox.ControllerDefault;
import com.mygdx.hadal.strategies.hitbox.CreateParticles;
import com.mygdx.hadal.strategies.hitbox.DamageStandard;

public class WhiteSmoker extends Artifact {

	private static final int statusNum = 1;
	private static final int slotCost = 2;
	
	private static final float procCd = 0.05f;
	
	private static final float baseDamage = 4.0f;
	private static final float knockback = 2.0f;
	private static final float projectileSpeed = 20.0f;
	private static final Vector2 projectileSize = new Vector2(50, 50);
	private static final float lifespan = 0.25f;
	
	private static final float fireDuration = 4.0f;
	private static final float fireDamage = 3.0f;
	
	public WhiteSmoker() {
		super(slotCost, statusNum);
	}

	@Override
	public Status[] loadEnchantments(PlayState state, BodyData b) {
		enchantment[0] = new Status(state, b) {
			
			private float procCdCount;
			
			@Override
			public void timePassing(float delta) {
				if (procCdCount >= procCd) {
					procCdCount -= procCd;
					
					if (inflicted instanceof PlayerBodyData) {
						if (((PlayerBodyData) inflicted).getPlayer().isHovering()) {
							RangedHitbox hbox = new RangedHitbox(state, inflicted.getSchmuck().getPixelPosition(), projectileSize, lifespan, new Vector2(0, -projectileSpeed),
									inflicted.getSchmuck().getHitboxfilter(), false, true, ((PlayerBodyData) inflicted).getPlayer(), Sprite.NOTHING);
							hbox.setDurability(3);
							
							hbox.addStrategy(new ControllerDefault(state, hbox, inflicted));
							hbox.addStrategy(new ContactUnitBurn(state, hbox, inflicted, fireDuration, fireDamage));
							hbox.addStrategy(new DamageStandard(state, hbox, inflicted, baseDamage, knockback, DamageTypes.FIRE, DamageTypes.RANGED));
							hbox.addStrategy(new CreateParticles(state, hbox, inflicted, Particle.FIRE, 0.0f, 1.0f));
						}
					}
				}
				procCdCount += delta;
			}
		};
		
		return enchantment;
	}
}
