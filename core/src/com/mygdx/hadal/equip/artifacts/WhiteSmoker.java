package com.mygdx.hadal.equip.artifacts;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.schmucks.bodies.hitboxes.RangedHitbox;
import com.mygdx.hadal.schmucks.strategies.DamageStandard;
import com.mygdx.hadal.schmucks.strategies.ControllerDefault;
import com.mygdx.hadal.schmucks.strategies.CreateParticles;
import com.mygdx.hadal.schmucks.strategies.ContactUnitStatus;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Ablaze;
import com.mygdx.hadal.statuses.DamageTypes;
import com.mygdx.hadal.statuses.Status;

public class WhiteSmoker extends Artifact {

	private final static int statusNum = 1;
	private final static int slotCost = 2;
	
	private final static float procCd = 0.05f;
	
	private final static float baseDamage = 4.0f;
	private final static float knockback = 2.0f;
	private final static float projectileSpeed = 20.0f;
	private final static Vector2 projectileSize = new Vector2(50, 50);
	private final static float lifespan = 0.25f;
	
	private final static float fireDuration = 3.0f;
	private final static float fireDamage = 2.0f;
	
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
						if (((PlayerBodyData)inflicted).getPlayer().isHovering()) {
							RangedHitbox hbox = new RangedHitbox(state, inflicted.getSchmuck().getPixelPosition(), projectileSize, lifespan, new Vector2(0, -projectileSpeed),
									inflicted.getSchmuck().getHitboxfilter(), false, true, ((PlayerBodyData) inflicted).getPlayer(), Sprite.NOTHING);
							hbox.setDurability(3);
							
							hbox.addStrategy(new ControllerDefault(state, hbox, inflicted));
							hbox.addStrategy(new ContactUnitStatus(state, hbox, inflicted, new Ablaze(state, fireDuration, inflicted, inflicted, fireDamage)));
							hbox.addStrategy(new DamageStandard(state, hbox, inflicted, baseDamage, knockback, DamageTypes.RANGED));
							hbox.addStrategy(new CreateParticles(state, hbox, inflicted, Particle.FIRE, 3.0f));
						}
					}
				}
				procCdCount += delta;
			}
		};
		
		return enchantment;
	}
}
