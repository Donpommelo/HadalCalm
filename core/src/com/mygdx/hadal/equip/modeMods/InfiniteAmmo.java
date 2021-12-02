package com.mygdx.hadal.equip.modeMods;

import com.mygdx.hadal.equip.Equippable;
import com.mygdx.hadal.equip.RangedWeapon;
import com.mygdx.hadal.equip.artifacts.Artifact;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Status;

/**
 * This "Artifact" is automatically applied to all characters for modes that have no ammo restriction
 * (gun game and football)
 * @author Humquat Hanek
 */
public class InfiniteAmmo extends Artifact {

	private static final int slotCost = 0;

	public InfiniteAmmo() { super(slotCost); }

	@Override
	public void loadEnchantments(PlayState state, PlayerBodyData p) {
		enchantment = new Status(state, p) {

			@Override
			public void onReloadFinish(Equippable tool) {
				if (p.getCurrentTool() instanceof RangedWeapon weapon) {
					weapon.gainAmmo(1.0f);
				}
			}
		};
	}
}
