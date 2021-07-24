package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.equip.RangedWeapon;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Status;
import com.mygdx.hadal.statuses.StatusComposite;

public class TyphonFang extends Artifact {

	private static final int statusNum = 1;
	private static final int slotCost = 2;
	
	public TyphonFang() {
		super(slotCost, statusNum);
	}

	@Override
	public Status[] loadEnchantments(PlayState state, BodyData b) {
		enchantment[0] = new StatusComposite(state, b, new Status(state, b) {
			
			@Override
			public void onKill(BodyData vic) {
				if (this.inflicted instanceof PlayerBodyData) {
					if (this.inflicted.getCurrentTool() instanceof RangedWeapon weapon) {
						SoundEffect.RELOAD.playUniversal(state, inflicted.getSchmuck().getPixelPosition(), 0.4f, false);

						if (vic instanceof PlayerBodyData) {
							weapon.gainClip(weapon.getClipSize());
						} else {
							weapon.gainClip(1);
						}
					}
				}
			}
		});
		return enchantment;
	}
}
