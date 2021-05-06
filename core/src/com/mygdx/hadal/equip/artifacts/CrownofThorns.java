package com.mygdx.hadal.equip.artifacts;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.bodies.hitboxes.RangedHitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;
import com.mygdx.hadal.statuses.Status;
import com.mygdx.hadal.strategies.hitbox.AdjustAngle;
import com.mygdx.hadal.strategies.hitbox.ContactUnitLoseDurability;
import com.mygdx.hadal.strategies.hitbox.ContactUnitSound;
import com.mygdx.hadal.strategies.hitbox.ContactWallDie;
import com.mygdx.hadal.strategies.hitbox.ControllerDefault;
import com.mygdx.hadal.strategies.hitbox.DamageStandard;

public class CrownofThorns extends Artifact {

	private static final int statusNum = 1;
	private static final int slotCost = 1;

	private static final float thornDamage = 20.0f;
	private static final float thornDuration = 0.35f;
	private static final float thornSpeed = 30.0f;
	private static final float thornKnockback = 15.0f;
	private static final Vector2 projectileSize = new Vector2(72, 9);
	
	private static final Sprite projSprite = Sprite.BULLET;
	
	private static final float procCd = 1.0f;
	
	public CrownofThorns() {
		super(slotCost, statusNum);
	}

	@Override
	public Status[] loadEnchantments(PlayState state, BodyData b) {
		enchantment[0] = new Status(state, b) {
			
			private float procCdCount = procCd;
			
			@Override
			public void timePassing(float delta) {
				if (procCdCount < procCd) {
					procCdCount += delta;
				}
			}
			
			private final Vector2 angle = new Vector2(1, 0);
			@Override
			public float onReceiveDamage(float damage, BodyData perp, DamageTypes... tags) {
				
				if (procCdCount >= procCd && damage > 0) {
					procCdCount = 0;
					
					SoundEffect.SPIKE.playUniversal(state, inflicted.getSchmuck().getPixelPosition(), 0.2f, false);
					
					for (int i = 0; i < 6; i++) {
						angle.setAngleDeg(angle.angleDeg() + 60);
						Hitbox hbox = new RangedHitbox(state, inflicted.getSchmuck().getPixelPosition(), projectileSize, thornDuration, new Vector2(angle).nor().scl(thornSpeed), inflicted.getSchmuck().getHitboxfilter(), 
								true, false, inflicted.getSchmuck(), projSprite);
						
						hbox.addStrategy(new ControllerDefault(state, hbox, inflicted));
						hbox.addStrategy(new ContactUnitLoseDurability(state, hbox, inflicted));
						hbox.addStrategy(new AdjustAngle(state, hbox, inflicted));
						hbox.addStrategy(new ContactWallDie(state, hbox, inflicted));
						hbox.addStrategy(new DamageStandard(state, hbox, inflicted, thornDamage, thornKnockback, DamageTypes.POKING, DamageTypes.RANGED));
						hbox.addStrategy(new ContactUnitSound(state, hbox, inflicted, SoundEffect.STAB, 0.25f, true));
					}
				}
				
				return damage;
			}
		};
		return enchantment;
	}
}
