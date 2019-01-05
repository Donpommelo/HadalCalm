package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.equip.RangedWeapon;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.StatChangeStatus;
import com.mygdx.hadal.statuses.Status;
import com.mygdx.hadal.statuses.StatusComposite;
import com.mygdx.hadal.utils.Stats;

public class TyphonFang extends Artifact {

	private final static String name = "Typhon Fang";
	private final static String descr = "-75% Reload Speed. Refill 50% clip on kill.";
	private final static String descrLong = "";
	private final static int statusNum = 1;
	
	private static final float cliprefill = 0.50f;
	
	public TyphonFang() {
		super(name, descr, descrLong, statusNum);
	}

	@Override
	public Status[] loadEnchantments(PlayState state, BodyData b) {
		enchantment[0] = new StatusComposite(state, name, descr, b, 
				new StatChangeStatus(state, Stats.RANGED_RELOAD, -0.75f, b), 
				new Status(state, "", "", b) {
			
			@Override
			public void onKill(BodyData vic) {
				if (this.inflicted instanceof PlayerBodyData) {
					if (((PlayerBodyData)this.inflicted).getCurrentTool() instanceof RangedWeapon) {
						RangedWeapon weapon = (RangedWeapon)((PlayerBodyData)this.inflicted).getCurrentTool();
						weapon.gainAmmo((int)(weapon.getClipSize() * cliprefill));
					}
				}
			}
			
		});
		return enchantment;
	}
}
