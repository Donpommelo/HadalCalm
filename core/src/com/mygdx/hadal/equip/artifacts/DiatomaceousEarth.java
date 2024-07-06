package com.mygdx.hadal.equip.artifacts;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.battle.DamageTag;
import com.mygdx.hadal.battle.SyncedAttack;
import com.mygdx.hadal.constants.Stats;
import com.mygdx.hadal.equip.ActiveItem;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.StatChangeStatus;
import com.mygdx.hadal.statuses.Status;
import com.mygdx.hadal.statuses.StatusComposite;

public class DiatomaceousEarth extends Artifact {

	private static final int SLOT_COST = 2;

	private static final float DURATION_MULTIPLIER = 0.5f;
	private static final float KNOCKBACK_RESISTANCE = 0.9f;
	private static final int ARMOR_AMOUNT = 3;

	public DiatomaceousEarth() {
		super(SLOT_COST);
	}

	@Override
	public void loadEnchantments(PlayState state, PlayerBodyData p) {
		enchantment = new Status(state, p) {
			
			@Override
			public void afterActiveItem(ActiveItem tool) {
				SyncedAttack.ARTIFACT_MAGIC_ACTIVATE.initiateSyncedAttackNoHbox(state, p.getPlayer(), new Vector2(), true);

				float dura = tool.getMaxCharge() * DURATION_MULTIPLIER;

				p.addStatus(new StatusComposite(state, dura, false, p, p,
					new StatChangeStatus(state, Stats.KNOCKBACK_RES, KNOCKBACK_RESISTANCE, p),
					new Status(state, p) {

						@Override
						public int onCalcArmorReceive(int armor, float damage, BodyData perp, Hitbox damaging, DamageSource source, DamageTag... tags) {
							return armor + ARMOR_AMOUNT;
						}

					}));
			}
		};
	}

	@Override
	public String[] getDescFields() {
		return new String[] {
				String.valueOf((int) (DURATION_MULTIPLIER * 100)),
				String.valueOf(ARMOR_AMOUNT),
				String.valueOf((int) (KNOCKBACK_RESISTANCE * 100))};
	}
}
