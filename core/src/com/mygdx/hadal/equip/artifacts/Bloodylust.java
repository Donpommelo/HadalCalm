package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.equip.RangedWeapon;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.StatChangeStatus;
import com.mygdx.hadal.statuses.Status;
import com.mygdx.hadal.statuses.StatusComposite;

public class Bloodylust extends Artifact {

	private final static String name = "Bloody Lust";
	private final static String descr = "75% Reload Speed. Refill 50% clip on kill.";
	private final static String descrLong = "";
	private final static int statusNum = 1;
	
	private static final float cliprefill = 0.50f;
	
	public Bloodylust() {
		super(name, descr, descrLong, statusNum);
	}

	@Override
	public Status[] loadEnchantments(PlayState state, BodyData b) {
		enchantment[0] = new StatusComposite(state, name, descr, b, 
				new StatChangeStatus(state, 28, -0.75f, b), 
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
