package com.mygdx.hadal.equip.artifacts;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.bodies.hitboxes.RangedHitbox;
import com.mygdx.hadal.schmucks.strategies.HitboxDamageStandardStrategy;
import com.mygdx.hadal.schmucks.strategies.HitboxDefaultStrategy;
import com.mygdx.hadal.schmucks.strategies.HitboxOnContactStickStrategy;
import com.mygdx.hadal.schmucks.strategies.HitboxOnContactUnitLoseDuraStrategy;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;
import com.mygdx.hadal.statuses.Status;

public class CrownofThorns extends Artifact {

	private final static int statusNum = 1;
	private final static int slotCost = 1;

	private static final float thornDamage = 25.0f;
	private static float thornDuration = 5.0f;
	private static final float thornSpeed = -25.0f;
	private final static float thornKnockback = 15.0f;
	private final static Vector2 projectileSize = new Vector2(72, 9);
	
	private final static Sprite projSprite = Sprite.BULLET;
	
	private static final float procCd = 0.25f;
	
	public CrownofThorns() {
		super(slotCost, statusNum);
	}

	@Override
	public Status[] loadEnchantments(PlayState state, BodyData b) {
		enchantment[0] = new Status(state, b) {
			
			private float procCdCount;
			
			@Override
			public void timePassing(float delta) {
				if (procCdCount < procCd) {
					procCdCount += delta;
				}
			}
			
			@Override
			public float onReceiveDamage(float damage, BodyData perp, DamageTypes... tags) {
				
				Hitbox hbox = new RangedHitbox(state, inflicted.getSchmuck().getPixelPosition(), projectileSize, thornDuration, new Vector2(0, thornSpeed), inflicted.getSchmuck().getHitboxfilter(), 
						true, true, inflicted.getSchmuck(), projSprite);
				
				hbox.addStrategy(new HitboxDefaultStrategy(state, hbox, inflicted));
				hbox.addStrategy(new HitboxOnContactUnitLoseDuraStrategy(state, hbox, inflicted));
				hbox.addStrategy(new HitboxOnContactStickStrategy(state, hbox, inflicted, true, false));
				hbox.addStrategy(new HitboxDamageStandardStrategy(state, hbox, inflicted, thornDamage, thornKnockback, DamageTypes.RANGED));
				
				return damage;
			}
			
		};
		return enchantment;
	}
}
