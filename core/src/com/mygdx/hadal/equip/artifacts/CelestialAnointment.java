package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.equip.ActiveItem;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.StatChangeStatus;
import com.mygdx.hadal.statuses.Status;
import com.mygdx.hadal.statuses.StatusComposite;
import com.mygdx.hadal.constants.Stats;

public class CelestialAnointment extends Artifact {

	private static final int slotCost = 3;
	
	private static final float bonusActiveCharge = -0.15f;
	private static final float baseDelay = 0.5f;
	
	public CelestialAnointment() {
		super(slotCost);
	}

	@Override
	public void loadEnchantments(PlayState state, PlayerBodyData p) {
		enchantment = new StatusComposite(state, p,
				new StatChangeStatus(state, Stats.ACTIVE_CHARGE_RATE, bonusActiveCharge, p),
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
						
						SoundEffect.MAGIC1_ACTIVE.playUniversal(p.getSchmuck().getState(), p.getSchmuck().getPixelPosition(), 0.4f, false);
						item.useItem(state, p);
					}
				}
			}
			
			@Override
			public void afterActiveItem(ActiveItem tool) {
				item = tool;
				delay = item.getUseDuration() + baseDelay;
				echoing = true;
			}
		});
	}

	@Override
	public String[] getDescFields() {
		return new String[] {
				String.valueOf((int) -(bonusActiveCharge * 100))};
	}
}
