package com.mygdx.hadal.equip.artifacts;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.battle.SyncedAttack;
import com.mygdx.hadal.constants.Stats;
import com.mygdx.hadal.equip.ActiveItem;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.StatChangeStatus;
import com.mygdx.hadal.statuses.Status;
import com.mygdx.hadal.statuses.StatusComposite;

public class DiatomaceousEarth extends Artifact {

	private static final int SLOT_COST = 2;

	private static final float DURATION_MULTIPLIER = 0.5f;
	private static final float DAMAGE_RESISTANCE = 0.75f;
	private static final float KNOCKBACK_RESISTANCE = 0.9f;

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
					new StatChangeStatus(state, Stats.DAMAGE_RES, DAMAGE_RESISTANCE, p),
					new StatChangeStatus(state, Stats.KNOCKBACK_RES, KNOCKBACK_RESISTANCE, p)));
			}
		};
	}

	@Override
	public String[] getDescFields() {
		return new String[] {
				String.valueOf((int) (DURATION_MULTIPLIER * 100)),
				String.valueOf((int) (DAMAGE_RESISTANCE * 100)),
				String.valueOf((int) (KNOCKBACK_RESISTANCE * 100))};
	}
}
