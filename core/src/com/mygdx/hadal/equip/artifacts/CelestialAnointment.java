package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.equip.ActiveItem;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.StatChangeStatus;
import com.mygdx.hadal.statuses.Status;
import com.mygdx.hadal.statuses.StatusComposite;
import com.mygdx.hadal.utils.Stats;

public class CelestialAnointment extends Artifact {

	private final static int statusNum = 1;
	private final static int slotCost = 3;
	
	private final static float bonusActiveCharge = -0.25f;
	private final static float baseDelay = 0.5f;
	
	public CelestialAnointment() {
		super(slotCost, statusNum);
	}

	@Override
	public Status[] loadEnchantments(PlayState state, BodyData b) {
		enchantment[0] = new StatusComposite(state, b, 
				new StatChangeStatus(state, Stats.ACTIVE_CHARGE_RATE, bonusActiveCharge, b),
				new Status(state, b) {
			
			private boolean echoing = false;
			private ActiveItem item;
			private float delay;
			
			@Override
			public void timePassing(float delta) {
				if (echoing) {
					delay -= delta;
					
					if (delay <= 0 && item != null) {
						echoing = false;
						
						SoundEffect.MAGIC1_ACTIVE.playUniversal(inflicted.getSchmuck().getState(), inflicted.getSchmuck().getPixelPosition(), 0.4f, false);
						item.useItem(state, (PlayerBodyData) inflicted);
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
		return enchantment;
	}
}
