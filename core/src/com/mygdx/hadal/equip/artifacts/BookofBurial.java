package com.mygdx.hadal.equip.artifacts;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.battle.WeaponUtils;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.constants.SyncType;
import com.mygdx.hadal.schmucks.entities.ParticleEntity;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.battle.SyncedAttack;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.battle.DamageTag;
import com.mygdx.hadal.statuses.Status;

import static com.mygdx.hadal.constants.Constants.PRIORITY_PROC;

public class BookofBurial extends Artifact {

	private static final int slotCost = 2;

	private static final float explosionDamage = 75.0f;
	private static final float procCd = 7.5f;

	public BookofBurial() {
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
			
			@Override
			public float onReceiveDamage(float damage, BodyData perp, Hitbox damaging, DamageSource source, DamageTag... tags) {
				if (procCdCount >= procCd && damage > 0) {
					procCdCount = 0;
					new ParticleEntity(state, p.getSchmuck(), Particle.RING, 0.0f, 1.0f, true,
							SyncType.CREATESYNC).setScale(0.4f);
					SyncedAttack.PROXIMITY_MINE.initiateSyncedAttackSingle(state, p.getSchmuck(), p.getPlayer().getPixelPosition(),
							new Vector2(), DamageSource.BOOK_OF_BURIAL, explosionDamage);
				}
				return damage;
			}
		}.setPriority(PRIORITY_PROC);
	}

	@Override
	public String[] getDescFields() {
		return new String[] {
				String.valueOf(procCd),
				String.valueOf((int) WeaponUtils.PRIME_TIME),
				String.valueOf((int) explosionDamage)};
	}
}
