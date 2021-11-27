package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.schmucks.bodies.Player;
import com.mygdx.hadal.schmucks.bodies.enemies.Enemy;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;
import com.mygdx.hadal.statuses.Status;

public class FalseFace extends Artifact {

	private static final int statusNum = 1;
	private static final int slotCost = 1;

	private static final float damageAmp = 1.8f;

	public FalseFace() {
		super(slotCost, statusNum);
	}

	@Override
	public Status[] loadEnchantments(PlayState state, final BodyData b) {
		enchantment[0] = new Status(state, b) {

			@Override
			public float onDealDamage(float damage, BodyData vic, Hitbox damaging, DamageTypes... tags) {
				if (damaging != null) {
					boolean flip = false;

					if (vic.getSchmuck() instanceof Enemy e) {
						flip = Math.abs(e.getAttackAngle()) > 90;

					} else if (vic.getSchmuck() instanceof Player p) {
						flip = Math.abs(p.getAttackAngle()) > 90;
					}
					if (damaging.isPositionBasedOnUser()) {
						if (flip) {
							if ((b.getSchmuck().getPixelPosition().x - vic.getSchmuck().getPixelPosition().x) < vic.getSchmuck().getSize().x / 2) {
								return damage * damageAmp;
							}
						} else {
							if ((b.getSchmuck().getPixelPosition().x - vic.getSchmuck().getPixelPosition().x) > vic.getSchmuck().getSize().x / 2) {
								return damage * damageAmp;
							}
						}
					} else {
						if (flip) {
							if ((damaging.getPixelPosition().x - vic.getSchmuck().getPixelPosition().x) < vic.getSchmuck().getSize().x / 2) {
								return damage * damageAmp;							}
						} else {
							if ((damaging.getPixelPosition().x - vic.getSchmuck().getPixelPosition().x) > vic.getSchmuck().getSize().x / 2) {
								return damage * damageAmp;
							}
						}
					}
				}
				return damage;
			}
		};
		return enchantment;
	}
}
