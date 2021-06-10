package com.mygdx.hadal.equip.modeMods;

import com.mygdx.hadal.equip.Equippable;
import com.mygdx.hadal.equip.RangedWeapon;
import com.mygdx.hadal.equip.artifacts.Artifact;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Status;

/**
 * This "Artifact" is automatically applied to all characters for modes that have no ammo restriction
 * (gun game and football)
 * @author Humquat Hanek
 */
public class InfiniteAmmo extends Artifact {

	private static final int statusNum = 1;
	private static final int slotCost = 0;

	public InfiniteAmmo() {
		super(slotCost, statusNum);
	}

	@Override
	public Status[] loadEnchantments(PlayState state, BodyData b) {
		enchantment[0] = new Status(state, b) {

			@Override
			public void onReloadFinish(Equippable tool) {
				if (this.inflicted instanceof PlayerBodyData) {
					if (this.inflicted.getCurrentTool() instanceof RangedWeapon) {
						RangedWeapon weapon = (RangedWeapon) this.inflicted.getCurrentTool();
						weapon.gainAmmo(1.0f);
					}
				}
			}
		};
		return enchantment;
	}
}
