package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.battle.DamageTag;
import com.mygdx.hadal.effects.Shader;
import com.mygdx.hadal.equip.Equippable;
import com.mygdx.hadal.equip.RangedWeapon;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Status;

public class BottomoftheBarrel extends Artifact {

	private static final int slotCost = 1;

	private static final float bonusDamage = 1.0f;
	private static final float bonusAttackSpeed = 0.25f;
	private static final float ammoThreshold = 0.25f;

	private static final float shaderCount = 0.5f;

	public BottomoftheBarrel() {
		super(slotCost);
	}

	@Override
	public void loadEnchantments(PlayState state, PlayerBodyData p) {
		enchantment = new Status(state, p) {

			private float procCdCount;
			@Override
			public void timePassing(float delta) {
				if (procCdCount < shaderCount) {
					procCdCount += delta;
				}
				if (p.getCurrentTool() instanceof RangedWeapon ranged) {
					if (procCdCount >= shaderCount && ranged.getAmmoPercent() <= ammoThreshold) {
						p.getPlayer().setShader(Shader.PULSE_RED, shaderCount * 2);
					}
				}
			}

			@Override
			public void onShoot(Equippable tool) {
				if (p.getCurrentTool() instanceof RangedWeapon ranged) {
					if (ranged.getAmmoPercent() <= ammoThreshold) {
						float cooldown = p.getPlayer().getShootHelper().getShootCdCount();
						p.getPlayer().getShootHelper().setShootCdCount(cooldown * (1 - bonusAttackSpeed));
					}
				}
			}

			@Override
			public float onDealDamage(float damage, BodyData vic, Hitbox damaging, DamageSource source, DamageTag... tags) {
				if (p.getCurrentTool() instanceof RangedWeapon ranged) {
					if (ranged.getAmmoPercent() <= ammoThreshold) {
						return damage *  (1.0f + bonusDamage);
					}
				}
				return damage;
			}
		}.setClientIndependent(true);
	}

	@Override
	public String[] getDescFields() {
		return new String[] {
				String.valueOf((int) (ammoThreshold * 100)),
				String.valueOf((int) (bonusDamage * 100)),
				String.valueOf((int) (bonusAttackSpeed * 100))};
	}
}
