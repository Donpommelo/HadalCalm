package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.schmucks.entities.Player;
import com.mygdx.hadal.schmucks.entities.enemies.Enemy;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.battle.DamageTag;
import com.mygdx.hadal.statuses.Status;

public class ErsatzSmile extends Artifact {

	private static final int SLOT_COST = 2;

	private static final float DAMAGE_AMP = 0.8f;

	public ErsatzSmile() {
		super(SLOT_COST);
	}

	@Override
	public void loadEnchantments(PlayState state, PlayerBodyData p) {
		enchantment = new Status(state, p) {

			@Override
			public float onDealDamage(float damage, BodyData vic, Hitbox damaging, DamageSource source, DamageTag... tags) {
				if (damaging != null) {
					boolean flip = false;

					if (vic.getSchmuck() instanceof Enemy e) {
						flip = Math.abs(e.getAttackAngle()) > 90;

					} else if (vic.getSchmuck() instanceof Player p) {
						flip = Math.abs(p.getMouseHelper().getAttackAngle()) > 90;
					}
					if (damaging.isPositionBasedOnUser()) {
						if (flip) {
							if ((p.getSchmuck().getPixelPosition().x - vic.getSchmuck().getPixelPosition().x) < vic.getSchmuck().getSize().x / 2) {
								return damage * (1.0f + DAMAGE_AMP);
							}
						} else {
							if ((p.getSchmuck().getPixelPosition().x - vic.getSchmuck().getPixelPosition().x) > vic.getSchmuck().getSize().x / 2) {
								return damage * (1.0f + DAMAGE_AMP);
							}
						}
					} else {
						if (flip) {
							if ((damaging.getPixelPosition().x - vic.getSchmuck().getPixelPosition().x) < vic.getSchmuck().getSize().x / 2) {
								return damage * (1.0f + DAMAGE_AMP);							}
						} else {
							if ((damaging.getPixelPosition().x - vic.getSchmuck().getPixelPosition().x) > vic.getSchmuck().getSize().x / 2) {
								return damage * (1.0f + DAMAGE_AMP);
							}
						}
					}
				}
				return damage;
			}
		};
	}

	@Override
	public String[] getDescFields() {
		return new String[] {
				String.valueOf((int) (DAMAGE_AMP * 100))};
	}
}
