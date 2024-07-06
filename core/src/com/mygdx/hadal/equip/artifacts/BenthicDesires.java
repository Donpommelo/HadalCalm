package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.battle.DamageTag;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.map.GameMode;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.ParticleToggleable;
import com.mygdx.hadal.constants.Stats;

public class BenthicDesires extends Artifact {

	private static final int SLOT_COST = 1;

	private static final float SPEED_THRESHOLD = 2.0f;
	private static final float MOVE_COOLDOWN = 1.0f;
	private static final float HP_REGEN = 9.0f;
	private static final int ARMOR_AMOUNT = 1;

	public BenthicDesires() {
		super(SLOT_COST);
	}

	@Override
	public void loadEnchantments(PlayState state, PlayerBodyData p) {
		enchantment = new ParticleToggleable(state, p, Particle.REGEN) {

			@Override
			public int onCalcArmorReceive(int armor, float damage, BodyData perp, Hitbox damaging, DamageSource source, DamageTag... tags) {
				if (activated) {
					return armor + ARMOR_AMOUNT;
				}
				return armor;
			}

			private boolean activated;
			private float count;
			@Override
			public void timePassing(float delta) {
				super.timePassing(delta);

				if (p.getSchmuck().getLinearVelocity().len2() >= SPEED_THRESHOLD) {
					count = MOVE_COOLDOWN;
				}
				count -= delta;

				activated = count <= 0.0f && p.getCurrentHp() < p.getStat(Stats.MAX_HP);
				setActivated(activated);

				if (!GameMode.CAMPAIGN.equals(state.getMode()) && !GameMode.BOSS.equals(state.getMode())) {
					if (activated) {
						p.regainHp(HP_REGEN * delta, p, false, DamageTag.REGEN);
					}
				}
			}
		};
	}

	@Override
	public String[] getDescFields() {
		return new String[] {
				String.valueOf((int) MOVE_COOLDOWN),
				String.valueOf((int) HP_REGEN),
				String.valueOf(ARMOR_AMOUNT)};
	}
}
