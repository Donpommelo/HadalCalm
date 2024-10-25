package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.battle.DamageTag;
import com.mygdx.hadal.constants.Stats;
import com.mygdx.hadal.effects.HadalColor;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.managers.EffectEntityManager;
import com.mygdx.hadal.requests.ParticleCreate;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.ParticleToggleable;

public class Confidence extends Artifact {

	private static final int SLOT_COST = 2;

	private static final float BONUS_DAMAGE = 0.5f;
	private static final float HP_THRESHOLD = 0.9f;

	public Confidence() {
		super(SLOT_COST);
	}

	@Override
	public void loadEnchantments(PlayState state, PlayerBodyData p) {
		enchantment = new ParticleToggleable(state, p) {

			@Override
			public float onDealDamage(float damage, BodyData vic, Hitbox damaging, DamageSource source, DamageTag... tags) {
				if (p.getCurrentHp() >= p.getStat(Stats.MAX_HP) * HP_THRESHOLD) {
					return damage * (1.0f + BONUS_DAMAGE);
				}
				return damage;
			}

			@Override
			public void timePassing(float delta) {
				super.timePassing(delta);
				boolean activated = p.getCurrentHp() >= p.getStat(Stats.MAX_HP) * HP_THRESHOLD;
				setActivated(activated);
			}

			@Override
			public void createParticle() {
				setParticle(EffectEntityManager.getParticle(state, new ParticleCreate(Particle.SPARKLE, p.getPlayer())
						.setStartOn(false)
						.setScale(2.0f)
						.setColor(HadalColor.YELLOW)));
			}
		};
	}

	@Override
	public String[] getDescFields() {
		return new String[] {
				String.valueOf((int) (HP_THRESHOLD * 100)),
				String.valueOf((int) (BONUS_DAMAGE * 100))};
	}
}
