package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.battle.DamageTag;
import com.mygdx.hadal.effects.HadalColor;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.constants.SyncType;
import com.mygdx.hadal.schmucks.entities.ParticleEntity;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.ParticleToggleable;
import com.mygdx.hadal.constants.Stats;

public class Confidence extends Artifact {

	private static final int slotCost = 2;

	private static final float bonusDamage = 0.5f;
	private static final float hpThreshold = 0.9f;

	public Confidence() {
		super(slotCost);
	}

	@Override
	public void loadEnchantments(PlayState state, PlayerBodyData p) {
		enchantment = new ParticleToggleable(state, p) {

			@Override
			public float onDealDamage(float damage, BodyData vic, Hitbox damaging, DamageSource source, DamageTag... tags) {
				if (p.getCurrentHp() >= p.getStat(Stats.MAX_HP) * hpThreshold) {
					return damage * (1.0f + bonusDamage);
				}
				return damage;
			}

			@Override
			public void timePassing(float delta) {
				super.timePassing(delta);
				boolean activated = p.getCurrentHp() >= p.getStat(Stats.MAX_HP) * hpThreshold;
				setActivated(activated);
			}

			@Override
			public void createParticle() {
				setParticle(new ParticleEntity(state, p.getPlayer(), Particle.SPARKLE, 0.0f, 0.0f,
						false, SyncType.NOSYNC).setColor(HadalColor.YELLOW).setScale(2.0f));
			}
		};
	}

	@Override
	public String[] getDescFields() {
		return new String[] {
				String.valueOf((int) (hpThreshold * 100)),
				String.valueOf((int) (bonusDamage * 100))};
	}
}
