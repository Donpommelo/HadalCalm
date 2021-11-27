package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;
import com.mygdx.hadal.statuses.Status;

public class Leatherback extends Artifact {

	private static final int statusNum = 1;
	private static final int slotCost = 1;

	private static final float res = 0.6f;

	public Leatherback() {
		super(slotCost, statusNum);
	}

	@Override
	public Status[] loadEnchantments(PlayState state, BodyData b) {
		enchantment[0] = new Status(state, b) {
			
			@Override
			public float onReceiveDamage(float damage, BodyData perp, Hitbox damaging, DamageTypes... tags) {
				if (damaging != null && inflicted instanceof PlayerBodyData playerData) {
					boolean flip = Math.abs(playerData.getPlayer().getAttackAngle()) > 90;
					if (damaging.isPositionBasedOnUser()) {
						if (flip) {
							if ((perp.getSchmuck().getPixelPosition().x - b.getSchmuck().getPixelPosition().x) < b.getSchmuck().getSize().x / 2) {
								return damage * res;
							}
						} else {
							if ((perp.getSchmuck().getPixelPosition().x - b.getSchmuck().getPixelPosition().x) > b.getSchmuck().getSize().x / 2) {
								return damage * res;
							}
						}
					} else {
						if (flip) {
							if ((damaging.getPixelPosition().x - b.getSchmuck().getPixelPosition().x) < b.getSchmuck().getSize().x / 2) {
								return damage * res;							}
						} else {
							if ((damaging.getPixelPosition().x - b.getSchmuck().getPixelPosition().x) > b.getSchmuck().getSize().x / 2) {
								return damage * res;
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
