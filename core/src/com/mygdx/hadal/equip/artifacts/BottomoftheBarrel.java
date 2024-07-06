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

	private static final int SLOT_COST = 1;

	private static final float BONUS_DAMAGE = 1.0f;
	private static final float BONUS_ATTACK_SPEED = 0.25f;
	private static final float AMMO_THRESHOLD = 0.25f;

	private static final float SHADER_COUNT = 0.5f;

	public BottomoftheBarrel() {
		super(SLOT_COST);
	}

	@Override
	public void loadEnchantments(PlayState state, PlayerBodyData p) {
		enchantment = new Status(state, p) {

			private float procCdCount;
			@Override
			public void timePassing(float delta) {
				if (procCdCount < SHADER_COUNT) {
					procCdCount += delta;
				}
				if (p.getPlayer().getEquipHelper().getCurrentTool() instanceof RangedWeapon ranged) {
					if (procCdCount >= SHADER_COUNT && ranged.getAmmoPercent() <= AMMO_THRESHOLD) {
						p.getPlayer().getShaderHelper().setShader(Shader.PULSE_RED, SHADER_COUNT * 2);
					}
				}
			}

			@Override
			public void onShoot(Equippable tool) {
				if (p.getPlayer().getEquipHelper().getCurrentTool() instanceof RangedWeapon ranged) {
					if (ranged.getAmmoPercent() <= AMMO_THRESHOLD) {
						float cooldown = p.getPlayer().getShootHelper().getShootCdCount();
						p.getPlayer().getShootHelper().setShootCdCount(cooldown * (1 - BONUS_ATTACK_SPEED));
					}
				}
			}

			@Override
			public float onDealDamage(float damage, BodyData vic, Hitbox damaging, DamageSource source, DamageTag... tags) {
				if (p.getPlayer().getEquipHelper().getCurrentTool() instanceof RangedWeapon ranged) {
					if (ranged.getAmmoPercent() <= AMMO_THRESHOLD) {
						return damage *  (1.0f + BONUS_DAMAGE);
					}
				}
				return damage;
			}
		};
	}

	@Override
	public String[] getDescFields() {
		return new String[] {
				String.valueOf((int) (AMMO_THRESHOLD * 100)),
				String.valueOf((int) (BONUS_DAMAGE * 100)),
				String.valueOf((int) (BONUS_ATTACK_SPEED * 100))};
	}
}
