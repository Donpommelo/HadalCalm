package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.equip.Equippable;
import com.mygdx.hadal.equip.RangedWeapon;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.battle.DamageTag;
import com.mygdx.hadal.statuses.Status;
import com.mygdx.hadal.statuses.StatusComposite;

public class BottomoftheBarrel extends Artifact {

	private static final int slotCost = 1;

	private static final float bonusDamage = 2.0f;
	private static final float bonusAttackSpeed = 0.25f;
	private static final float ammoThreshold = 0.25f;
	
	public BottomoftheBarrel() {
		super(slotCost);
	}

	@Override
	public void loadEnchantments(PlayState state, PlayerBodyData p) {
		enchantment = new StatusComposite(state, p, new Status(state, p) {

			@Override
			public void onShoot(Equippable tool) {
				if (p.getCurrentTool() instanceof RangedWeapon ranged) {
					if (ranged.getAmmoPercent() <= ammoThreshold) {
						float cooldown = p.getSchmuck().getShootCdCount();
						p.getSchmuck().setShootCdCount(cooldown * (1 - bonusAttackSpeed));
					}
				}
			}

			@Override
			public float onDealDamage(float damage, BodyData vic, Hitbox damaging, DamageSource source, DamageTag... tags) {
				if (p.getCurrentTool() instanceof RangedWeapon ranged) {
					if (ranged.getAmmoPercent() <= ammoThreshold) {
						return damage * bonusDamage;
					}
				}
				return damage;
			}
		});
	}
}
