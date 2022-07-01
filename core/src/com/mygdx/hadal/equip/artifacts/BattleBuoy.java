package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.equip.RangedWeapon;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Status;
import com.mygdx.hadal.statuses.StatusComposite;
import com.mygdx.hadal.strategies.hitbox.CreateParticles;
import com.mygdx.hadal.strategies.hitbox.DieParticles;

public class BattleBuoy extends Artifact {

	private static final int slotCost = 1;

	private final float DamageBoostMin = 0.1f;
	private final float DamageBoostMax = 1.5f;
	private final int ClipMax = 30;
	private final int ClipMin = 1;

	public BattleBuoy() {
		super(slotCost);
	}

	@Override
	public void loadEnchantments(PlayState state, PlayerBodyData p) {
		enchantment = new StatusComposite(state, p,
				new Status(state, p) {
			
			@Override
			public void onHitboxCreation(Hitbox hbox) {
				if (!hbox.isEffectsHit()) { return; }
				if (p.getCurrentTool() instanceof RangedWeapon weapon) {
					if (weapon.getClipLeft() == 0) {
						int clip = Math.min(weapon.getClipSize(), ClipMax);
						float bonusDamage = DamageBoostMin + (float) (clip - ClipMin) / (ClipMax - ClipMin) * (DamageBoostMax - DamageBoostMin);

						hbox.addStrategy(new CreateParticles(state, hbox, p, Particle.ENERGY_CLOUD, 0.0f, 1.0f));
						hbox.addStrategy(new DieParticles(state, hbox, p, Particle.EXPLOSION));
						hbox.setDamageMultiplier(1.0f + bonusDamage);
					}
				}
			}
		});
	}

	@Override
	public String[] getDescFields() {
		return new String[] {
				String.valueOf((int) (DamageBoostMin * 100)),
				String.valueOf((int) (DamageBoostMax * 100))};
	}
}
