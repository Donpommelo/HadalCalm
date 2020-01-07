package com.mygdx.hadal.equip.artifacts;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.Equipable;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.bodies.hitboxes.RangedHitbox;
import com.mygdx.hadal.schmucks.strategies.HitboxDamageStandardStrategy;
import com.mygdx.hadal.schmucks.strategies.HitboxDefaultStrategy;
import com.mygdx.hadal.schmucks.strategies.HitboxOnContactUnitLoseDuraStrategy;
import com.mygdx.hadal.schmucks.strategies.HitboxOnContactWallDieStrategy;
import com.mygdx.hadal.schmucks.strategies.HitboxSpreadStrategy;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;
import com.mygdx.hadal.statuses.Status;

public class Nurdler extends Artifact {

	private final static int statusNum = 1;
	private final static int slotCost = 1;
	
	private final static float procCd = 0.25f;
	
	private final static Vector2 projectileSize = new Vector2(14, 14);
	private final static float lifespan = 0.4f;
	private final static Sprite projSprite = Sprite.ORB_BLUE;
	
	private final static float baseDamage = 7.5f;
	private final static float knockback = 2.5f;
	private final static int spread = 30;
	
	private final static float projSpeed = 30.0f;
	
	public Nurdler() {
		super(slotCost, statusNum);
	}

	@Override
	public Status[] loadEnchantments(PlayState state, BodyData b) {
		enchantment[0] = new Status(state, b) {
			
			private float procCdCount;
			private Vector2 startVelo = new Vector2();
			
			@Override
			public void whileAttacking(float delta, Equipable tool) {
				
				if (procCdCount < procCd) {
					procCdCount += delta;
				}
				
				if (procCdCount >= procCd) {
					procCdCount -= procCd;
					
					Hitbox hbox = new RangedHitbox(state, inflicted.getSchmuck().getPixelPosition(), projectileSize, lifespan, startVelo.set(tool.getWeaponVelo()).nor().scl(projSpeed), inflicted.getSchmuck().getHitboxfilter(),
							true, true, inflicted.getSchmuck(), projSprite);
					
					hbox.addStrategy(new HitboxDefaultStrategy(state, hbox, inflicted));
					hbox.addStrategy(new HitboxOnContactUnitLoseDuraStrategy(state, hbox, inflicted));
					hbox.addStrategy(new HitboxOnContactWallDieStrategy(state, hbox, inflicted));
					hbox.addStrategy(new HitboxDamageStandardStrategy(state, hbox, inflicted, baseDamage, knockback, DamageTypes.RANGED));
					hbox.addStrategy(new HitboxSpreadStrategy(state, hbox, inflicted, spread));
				}
			}
		};
		return enchantment;
	}
}
