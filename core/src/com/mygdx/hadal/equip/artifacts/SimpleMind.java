package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.equip.Equippable;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.StatChangeStatus;
import com.mygdx.hadal.statuses.Status;
import com.mygdx.hadal.statuses.StatusComposite;
import com.mygdx.hadal.constants.Stats;

public class SimpleMind extends Artifact {

	private static final int SLOT_COST = 1;
	
	private static final float BONUS_CLIP_SIZE = 0.5f;
	private static final float BONUS_ATK_SPD = 0.5f;
	private static final float BONUS_RELOAD_SPD = 0.5f;
	private static final float BONUS_AMMO = 0.5f;
	
	public SimpleMind() {
		super(SLOT_COST);
	}

	@Override
	public void loadEnchantments(PlayState state, PlayerBodyData p) {
		enchantment = new StatusComposite(state, p,
				new StatChangeStatus(state, Stats.RANGED_CLIP, BONUS_CLIP_SIZE, p),
				new StatChangeStatus(state, Stats.RANGED_ATK_SPD, BONUS_ATK_SPD, p),
				new StatChangeStatus(state, Stats.RANGED_RELOAD, BONUS_RELOAD_SPD, p),
				new StatChangeStatus(state, Stats.AMMO_CAPACITY, BONUS_AMMO, p),
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
