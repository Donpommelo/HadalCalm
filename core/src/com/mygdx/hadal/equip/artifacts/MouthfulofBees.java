package com.mygdx.hadal.equip.artifacts;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.battle.SyncedAttack;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.battle.DamageTag;
import com.mygdx.hadal.statuses.Status;

import static com.mygdx.hadal.constants.Constants.PRIORITY_SCALE;

public class MouthfulofBees extends Artifact {

	private static final int slotCost = 1;

	private static final float beeSpeed = 15.0f;
	private static final float damagePerBee = 20.0f;
	private static final int beesOnDeath = 5;

	public MouthfulofBees() {
		super(slotCost);
	}

	@Override
	public void loadEnchantments(PlayState state, PlayerBodyData p) {
		enchantment = new Status(state, p) {
			
			@Override
			public float onReceiveDamage(float damage, BodyData perp, Hitbox damaging, DamageSource source, DamageTag... tags) {
				if (damage > 0) {
					int numBees = (int) (damage / damagePerBee);
					for (int i = 0; i < numBees; i++) {
						SyncedAttack.BEE_MOUTHFUL.initiateSyncedAttackSingle(state, p.getSchmuck(), p.getSchmuck().getPixelPosition(),
								new Vector2(0, beeSpeed));
					}
				}
				return damage;
			}
			
			@Override
			public void onDeath(BodyData perp, DamageSource source) {
				for (int i = 0; i < beesOnDeath; i++) {
					SyncedAttack.BEE.initiateSyncedAttackSingle(state, p.getSchmuck(), p.getSchmuck().getPixelPosition(),
							new Vector2(0, beeSpeed));
				}
			}
		}.setPriority(PRIORITY_SCALE);
	}

	@Override
	public String[] getDescFields() {
		return new String[] {
				String.valueOf((int) damagePerBee),
				String.valueOf(beesOnDeath)};
	}
}
