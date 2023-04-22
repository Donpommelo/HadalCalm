package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.constants.SyncType;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Status;
import com.mygdx.hadal.strategies.hitbox.ContactUnitBurn;
import com.mygdx.hadal.strategies.hitbox.CreateParticles;

public class RedTideTalisman extends Artifact {

	private static final int SLOT_COST = 1;
	
	private static final float FIRE_DURATION = 4.0f;
	private static final float FIRE_DAMAGE = 3.0f;
	
	public RedTideTalisman() {
		super(SLOT_COST);
	}

	@Override
	public void loadEnchantments(PlayState state, PlayerBodyData p) {
		enchantment = new Status(state, p) {

			@Override
			public void onHitboxCreation(Hitbox hbox) {
				if (hbox.isEffectsHit()) {
					hbox.addStrategy(new ContactUnitBurn(state, hbox, p, FIRE_DURATION, FIRE_DAMAGE, DamageSource.RED_TIDE_TALISMAN));
				}
				if (hbox.isEffectsVisual()) {
					hbox.addStrategy(new CreateParticles(state, hbox, p, Particle.FIRE, hbox.getLifeSpan(), 1.0f)
							.setParticleSize(50).setSyncType(SyncType.NOSYNC));
				}
			}
		};
	}

	@Override
	public String[] getDescFields() {
		return new String[] {
				String.valueOf((int) FIRE_DURATION),
				String.valueOf((int) FIRE_DAMAGE)};
	}
}
