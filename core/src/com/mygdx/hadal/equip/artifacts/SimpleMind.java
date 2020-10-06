package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.equip.Equippable;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.StatChangeStatus;
import com.mygdx.hadal.statuses.Status;
import com.mygdx.hadal.statuses.StatusComposite;
import com.mygdx.hadal.utils.Stats;

public class SimpleMind extends Artifact {

	private static final int statusNum = 1;
	private static final int slotCost = 1;
	
	private static final float bonusClipSize = 0.5f;
	private static final float bonusAtkSpd = 0.5f;
	private static final float bonusReloadSpd = 0.5f;
	private static final float bonusAmmo = 0.5f;
	
	public SimpleMind() {
		super(slotCost, statusNum);
	}

	@Override
	public Status[] loadEnchantments(PlayState state, final BodyData b) {
		enchantment[0] = new StatusComposite(state, b, 
				new StatChangeStatus(state, Stats.RANGED_CLIP, bonusClipSize, b),
				new StatChangeStatus(state, Stats.RANGED_ATK_SPD, bonusAtkSpd, b),
				new StatChangeStatus(state, Stats.RANGED_RELOAD, bonusReloadSpd, b),
				new StatChangeStatus(state, Stats.AMMO_CAPACITY, bonusAmmo, b),
				new Status(state, b) {
			
			@Override
			public void onHitboxCreation(Hitbox hbox) {
				hbox.setGravity(0.0f);
			}
			
			@Override
			public void onShoot(Equippable tool) {
				if (tool.getWeaponVelo().x > 0) {
					tool.setWeaponVelo(tool.getWeaponVelo().setAngle(0));
				} else {
					tool.setWeaponVelo(tool.getWeaponVelo().setAngle(180));
				}
			}
		});
		return enchantment;
	}
}
