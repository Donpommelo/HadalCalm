package com.mygdx.hadal.equip.artifacts;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.battle.DamageTag;
import com.mygdx.hadal.battle.SyncedAttack;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Status;

import static com.mygdx.hadal.constants.StatusPriority.PRIORITY_SCALE;

public class MouthfulofBees extends Artifact {

	private static final int SLOT_COST = 1;

	private static final float BEE_SPEED = 15.0f;
	private static final float DAMAGE_PER_BEE = 20.0f;
	private static final int BEES_ON_DEATH = 5;

	public MouthfulofBees() {
		super(SLOT_COST);
	}

	@Override
	public void loadEnchantments(PlayState state, PlayerBodyData p) {
		enchantment = new Status(state, p) {
			
			@Override
			public float onReceiveDamage(float damage, BodyData perp, Hitbox damaging, DamageSource source, DamageTag... tags) {
				if (damage > 0) {
					int numBees = (int) (damage / DAMAGE_PER_BEE);
					for (int i = 0; i < numBees; i++) {
						SyncedAttack.BEE_MOUTHFUL.initiateSyncedAttackSingle(state, p.getSchmuck(), p.getSchmuck().getPixelPosition(),
								new Vector2(0, BEE_SPEED));
					}
				}
				return damage;
			}
			
			@Override
			public void onDeath(BodyData perp, DamageSource source) {
				for (int i = 0; i < BEES_ON_DEATH; i++) {
					SyncedAttack.BEE.initiateSyncedAttackSingle(state, p.getSchmuck(), p.getSchmuck().getPixelPosition(),
							new Vector2(0, BEE_SPEED));
				}
			}
		}.setPriority(PRIORITY_SCALE).setUserOnly(true);
	}

	@Override
	public String[] getDescFields() {
		return new String[] {
				String.valueOf((int) DAMAGE_PER_BEE),
				String.valueOf(BEES_ON_DEATH)};
	}
}
