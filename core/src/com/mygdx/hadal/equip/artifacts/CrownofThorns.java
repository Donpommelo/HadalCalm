package com.mygdx.hadal.equip.artifacts;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.battle.DamageTag;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.entities.hitboxes.RangedHitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Status;
import com.mygdx.hadal.strategies.hitbox.*;

import static com.mygdx.hadal.constants.Constants.PRIORITY_PROC;

public class CrownofThorns extends Artifact {

	private static final int slotCost = 1;

	private static final int thornsNumber = 6;
	private static final float thornDamage = 24.0f;
	private static final float thornDuration = 0.35f;
	private static final float thornSpeed = 36.0f;
	private static final float thornKnockback = 15.0f;
	private static final Vector2 projectileSize = new Vector2(72, 9);
	
	private static final Sprite projSprite = Sprite.BULLET;
	
	private static final float procCd = 0.5f;
	
	public CrownofThorns() {
		super(slotCost);
	}

	@Override
	public void loadEnchantments(PlayState state, PlayerBodyData p) {
		enchantment = new Status(state, p) {
			
			private float procCdCount = procCd;
			@Override
			public void timePassing(float delta) {
				if (procCdCount < procCd) {
					procCdCount += delta;
				}
			}
			
			private final Vector2 angle = new Vector2(1, 0);
			@Override
			public float onReceiveDamage(float damage, BodyData perp, Hitbox damaging, DamageSource source, DamageTag... tags) {
				if (procCdCount >= procCd && damage > 0) {
					procCdCount = 0;
					
					SoundEffect.SPIKE.playUniversal(state, p.getSchmuck().getPixelPosition(), 0.2f, false);
					
					for (int i = 0; i < thornsNumber; i++) {
						angle.setAngleDeg(angle.angleDeg() + 60);
						Hitbox hbox = new RangedHitbox(state, p.getSchmuck().getPixelPosition(), projectileSize, thornDuration,
								new Vector2(angle).nor().scl(thornSpeed), p.getSchmuck().getHitboxFilter(),
								true, false, p.getSchmuck(), projSprite);
						
						hbox.addStrategy(new ControllerDefault(state, hbox, p));
						hbox.addStrategy(new ContactUnitLoseDurability(state, hbox, p));
						hbox.addStrategy(new AdjustAngle(state, hbox, p));
						hbox.addStrategy(new ContactWallDie(state, hbox, p));
						hbox.addStrategy(new DamageStandard(state, hbox, p, thornDamage, thornKnockback, DamageSource.CROWN_OF_THORNS,
								DamageTag.POKING, DamageTag.RANGED));
						hbox.addStrategy(new ContactUnitSound(state, hbox, p, SoundEffect.STAB, 0.25f, true));
					}
				}
				return damage;
			}
		}.setPriority(PRIORITY_PROC);
	}

	@Override
	public String[] getDescFields() {
		return new String[] {
				String.valueOf(procCd),
				String.valueOf(thornsNumber),
				String.valueOf((int) thornDamage)};
	}
}
