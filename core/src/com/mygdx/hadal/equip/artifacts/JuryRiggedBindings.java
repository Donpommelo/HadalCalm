package com.mygdx.hadal.equip.artifacts;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.equip.Equippable;
import com.mygdx.hadal.equip.RangedWeapon;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Status;

public class JuryRiggedBindings extends Artifact {

	private static final int slotCost = 3;

	private static final float fireRateMultiplier = 0.2f;
	private static final float baseFireRate = 0.5f;

	public JuryRiggedBindings() {
		super(slotCost);
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
				
				if (tool.isReloading()) {
					return;
				}

				float procCd = -1;
				for (int i = 0; i < p.getNumWeaponSlots(); i++) {
					if (p.getMultitools()[(p.getCurrentSlot() + lastFiredIndex + 1) % p.getNumWeaponSlots()] instanceof RangedWeapon ranged) {
						procCd = ranged.getUseCd() / fireRateMultiplier + baseFireRate;
						break;
					}
					lastFiredIndex = (lastFiredIndex + 1) % (p.getNumWeaponSlots() - 1);
				}

				if (procCdCount >= procCd && procCd != -1) {
					procCdCount -= procCd;

					Equippable extraFire = p.getMultitools()[(p.getCurrentSlot() + lastFiredIndex + 1) % p.getNumWeaponSlots()];
					if (extraFire instanceof RangedWeapon ranged) {
						startVelo.set(tool.getWeaponVelo()).nor().scl(ranged.getProjectileSpeed());
						extraFire.fire(state, p.getSchmuck(), p.getSchmuck().getProjectileOrigin(startVelo, extraFire.getAmmoSize()),
								startVelo, p.getSchmuck().getHitboxfilter());
						lastFiredIndex = (lastFiredIndex + 1) % (p.getNumWeaponSlots() - 1);
					}
				}
			}
		};
	}

	@Override
	public String[] getDescFields() {
		return new String[] {
				String.valueOf((int) (fireRateMultiplier * 100))};
	}
}
