package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.equip.Equippable;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.StatChangeStatus;
import com.mygdx.hadal.statuses.Status;
import com.mygdx.hadal.statuses.StatusComposite;
import com.mygdx.hadal.utils.Stats;

public class SimpleMind extends Artifact {

	private static final int slotCost = 1;
	
	private static final float bonusClipSize = 0.5f;
	private static final float bonusAtkSpd = 0.5f;
	private static final float bonusReloadSpd = 0.5f;
	private static final float bonusAmmo = 0.5f;
	
	public SimpleMind() {
		super(slotCost);
	}

	@Override
	public void loadEnchantments(PlayState state, PlayerBodyData p) {
		enchantment = new StatusComposite(state, p,
				new StatChangeStatus(state, Stats.RANGED_CLIP, bonusClipSize, p),
				new StatChangeStatus(state, Stats.RANGED_ATK_SPD, bonusAtkSpd, p),
				new StatChangeStatus(state, Stats.RANGED_RELOAD, bonusReloadSpd, p),
				new StatChangeStatus(state, Stats.AMMO_CAPACITY, bonusAmmo, p),
				new Status(state, p) {
			
			@Override
			public void onHitboxCreation(Hitbox hbox) {
				hbox.setGravity(0.0f);
			}
			
			@Override
			public void onShoot(Equippable tool) {
				if (tool.getWeaponVelo().x > 0) {
					tool.setWeaponVelo(tool.getWeaponVelo().setAngleDeg(0));
				} else {
					tool.setWeaponVelo(tool.getWeaponVelo().setAngleDeg(180));
				}
			}
		});
	}
}
