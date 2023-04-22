package com.mygdx.hadal.equip.artifacts;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.battle.DamageTag;
import com.mygdx.hadal.battle.SyncedAttack;
import com.mygdx.hadal.battle.attacks.artifact.CrownOfThornsActivate;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Status;

import static com.mygdx.hadal.constants.Constants.PRIORITY_PROC;

public class CrownOfThorns extends Artifact {

	private static final int slotCost = 1;

	private static final int THORNS_NUMBER = CrownOfThornsActivate.THORNS_NUMBER;
	private static final float THORN_DAMAGE = CrownOfThornsActivate.THORN_DAMAGE;

	private static final float PROC_CD = 0.5f;
	
	public CrownOfThorns() {
		super(slotCost);
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

					SyncedAttack.CROWN_OF_THORNS.initiateSyncedAttackMulti(state, p.getSchmuck(), new Vector2(),
							new Vector2[] {}, new Vector2[] {});
				}
				return damage;
			}
		}.setPriority(PRIORITY_PROC).setUserOnly(true);
	}

	@Override
	public String[] getDescFields() {
		return new String[] {
				String.valueOf(PROC_CD),
				String.valueOf(THORNS_NUMBER),
				String.valueOf((int) THORN_DAMAGE)};
	}
}
