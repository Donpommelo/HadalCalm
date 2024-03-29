package com.mygdx.hadal.equip.artifacts;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.equip.Equippable;
import com.mygdx.hadal.equip.RangedWeapon;
import com.mygdx.hadal.schmucks.entities.helpers.LoadoutEquipHelper;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Status;

public class JuryRiggedBindings extends Artifact {

	private static final int SLOT_COST = 3;

	private static final float FIRE_RATE_MULTIPLIER = 0.2f;
	private static final float BASE_FIRE_RATE = 0.5f;

	public JuryRiggedBindings() {
		super(SLOT_COST);
	}

	@Override
	public void loadEnchantments(PlayState state, PlayerBodyData p) {
		enchantment = new Status(state, p) {
			
			private float procCdCount;
			private int lastFiredIndex;
			private final Vector2 startVelo = new Vector2();
			@Override
			public void timePassing(float delta) {
				procCdCount += delta;
			}
			
			@Override
			public void whileAttacking(float delta, Equippable tool) {
				
				if (tool.isReloading()) { return; }
				LoadoutEquipHelper equipHelper = p.getPlayer().getEquipHelper();

				float procCd = -1;
				for (int i = 0; i < equipHelper.getNumWeaponSlots(); i++) {
					if (equipHelper.getMultitools()[(equipHelper.getCurrentSlot() + lastFiredIndex + 1) % equipHelper.getNumWeaponSlots()] instanceof RangedWeapon ranged) {
						procCd = ranged.getUseCd() / FIRE_RATE_MULTIPLIER + BASE_FIRE_RATE;
						break;
					}
					lastFiredIndex = (lastFiredIndex + 1) % (equipHelper.getNumWeaponSlots() - 1);
				}

				if (procCdCount >= procCd && procCd != -1) {
					procCdCount = 0.0f;

					Equippable extraFire = equipHelper.getMultitools()[(equipHelper.getCurrentSlot() + lastFiredIndex + 1) % equipHelper.getNumWeaponSlots()];
					if (extraFire instanceof RangedWeapon ranged) {
						startVelo.set(tool.getWeaponVelo()).nor().scl(ranged.getProjectileSpeed());
						extraFire.fire(state, p.getPlayer(), p.getPlayer().getProjectileOrigin(startVelo, extraFire.getAmmoSize()),
								startVelo, p.getSchmuck().getHitboxFilter());
						lastFiredIndex = (lastFiredIndex + 1) % (equipHelper.getNumWeaponSlots() - 1);
					}
				}
			}
		};
	}

	@Override
	public String[] getDescFields() {
		return new String[] {
				String.valueOf((int) (FIRE_RATE_MULTIPLIER * 100))};
	}
}
