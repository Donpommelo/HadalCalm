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

public class CelestialAnointment extends Artifact {

	private static final int SLOT_COST = 3;
	
	private static final float BONUS_ACTIVE_CHARGE = -0.15f;
	private static final float BASE_DELAY = 0.5f;
	
	public CelestialAnointment() {
		super(SLOT_COST);
	}

	@Override
	public void loadEnchantments(PlayState state, PlayerBodyData p) {
		enchantment = new StatusComposite(state, p,
				new StatChangeStatus(state, Stats.ACTIVE_CHARGE_RATE, BONUS_ACTIVE_CHARGE, p),
				new Status(state, p) {
			
			private boolean echoing;
			private ActiveItem item;
			private float delay;
			@Override
			public void timePassing(float delta) {
				if (echoing) {
					delay -= delta;
					
					if (delay <= 0 && item != null) {
						echoing = false;

						SyncedAttack.ARTIFACT_MAGIC_ACTIVATE.initiateSyncedAttackNoHbox(state, p.getPlayer(), new Vector2(), true);
						item.useItem(state, p);
					}
				}
			}
			
			@Override
			public void afterActiveItem(ActiveItem tool) {
				SyncedAttack.CELESTIAL_ANOINTMENT.initiateSyncedAttackNoHbox(state, p.getPlayer(), new Vector2(), true);

				item = tool;
				delay = item.getUseDuration() + BASE_DELAY;
				echoing = true;
			}
		}).setUserOnly(true);
	}

	@Override
	public String[] getDescFields() {
		return new String[] {
				String.valueOf((int) -(BONUS_ACTIVE_CHARGE * 100))};
	}
}
