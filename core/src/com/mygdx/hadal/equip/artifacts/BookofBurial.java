package com.mygdx.hadal.equip.artifacts;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.battle.DamageTag;
import com.mygdx.hadal.battle.SyncedAttack;
import com.mygdx.hadal.battle.attacks.general.ProximityMineProjectile;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Status;

import static com.mygdx.hadal.constants.StatusPriority.PRIORITY_PROC;

public class BookofBurial extends Artifact {

	private static final int SLOT_COST = 2;

	private static final float EXPLOSION_DAMAGE = 75.0f;
	private static final float PROC_CD = 7.5f;

	public BookofBurial() {
		super(SLOT_COST);
	}

	@Override
	public void loadEnchantments(PlayState state, PlayerBodyData p) {
		enchantment = new Status(state, p) {
			
			private float procCdCount = PROC_CD;
			@Override
			public void timePassing(float delta) {
				if (procCdCount < PROC_CD) {
					procCdCount += delta;
				}
			}
			
			@Override
			public float onReceiveDamage(float damage, BodyData perp, Hitbox damaging, DamageSource source, DamageTag... tags) {
				if (procCdCount >= PROC_CD && damage > 0) {
					procCdCount = 0;
					SyncedAttack.PROXIMITY_MINE_BOOK.initiateSyncedAttackSingle(state, p.getSchmuck(), p.getPlayer().getPixelPosition(),
							new Vector2(), EXPLOSION_DAMAGE);
				}
				return damage;
			}
		}.setPriority(PRIORITY_PROC).setUserOnly(true);
	}

	@Override
	public String[] getDescFields() {
		return new String[] {
				String.valueOf(PROC_CD),
				String.valueOf((int) ProximityMineProjectile.PRIME_TIME),
				String.valueOf((int) EXPLOSION_DAMAGE)};
	}
}
