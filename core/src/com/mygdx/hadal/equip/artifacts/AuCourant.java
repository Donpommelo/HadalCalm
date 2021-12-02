package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.StatChangeStatus;
import com.mygdx.hadal.statuses.Status;
import com.mygdx.hadal.statuses.StatusComposite;
import com.mygdx.hadal.utils.Stats;

public class AuCourant extends Artifact {

	private static final int slotCost = 3;
	
	private static final float bonusReloadSpd = -0.20f;
	
	public AuCourant() {
		super(slotCost);
	}

	@Override
	public void loadEnchantments(PlayState state, PlayerBodyData p) {
		enchantment = new StatusComposite(state, p,
				new StatChangeStatus(state, Stats.RANGED_RELOAD, bonusReloadSpd, p),
				new Status(state, p) {
			
			@Override
			public void timePassing(float delta) {
				for (int i = 0; i < p.getNumWeaponSlots(); i++) {
					if (i != p.getCurrentSlot()) {
						if (p.getMultitools()[i].getClipLeft() != p.getMultitools()[i].getClipSize()) {
							if (p.getMultitools()[i].reload(delta)) {
								SoundEffect.RELOAD.playUniversal(state, p.getSchmuck().getPixelPosition(), 0.4f, false);
							}
						}
					}
				}
			}
		});
	}
}
