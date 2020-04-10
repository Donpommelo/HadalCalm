package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.StatChangeStatus;
import com.mygdx.hadal.statuses.Status;
import com.mygdx.hadal.statuses.StatusComposite;
import com.mygdx.hadal.utils.Stats;

public class AuCourant extends Artifact {

	private final static int statusNum = 1;
	private final static int slotCost = 3;
	
	private static final float bonusReloadSpd = -0.4f;
	
	public AuCourant() {
		super(slotCost, statusNum);
	}

	@Override
	public Status[] loadEnchantments(PlayState state, final BodyData b) {
		enchantment[0] = new StatusComposite(state, b, 
				new StatChangeStatus(state, Stats.RANGED_RELOAD, bonusReloadSpd, b), 
				new Status(state, b) {
			
			@Override
			public void timePassing(float delta) {
				for (int i = 0; i < ((PlayerBodyData) inflicted).getMultitools().length; i++) {
					if (i != ((PlayerBodyData) inflicted).getCurrentSlot()) {
						if (((PlayerBodyData) inflicted).getMultitools()[i].getClipLeft() != ((PlayerBodyData) inflicted).getMultitools()[i].getClipSize()) {
							if (((PlayerBodyData) inflicted).getMultitools()[i].reload(delta)) {
								SoundEffect.RELOAD.playUniversal(state, inflicted.getSchmuck().getPixelPosition(), 0.4f, false);
							};
						}
					}
				}
			}
		});
		
		return enchantment;
	}
}
