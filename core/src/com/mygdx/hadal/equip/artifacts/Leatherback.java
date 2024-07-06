package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.battle.DamageTag;
import com.mygdx.hadal.statuses.Status;

public class Leatherback extends Artifact {

	private static final int SLOT_COST = 1;

	private static final int ARMOR_AMOUNT = 2;

	public Leatherback() {
		super(SLOT_COST);
	}

	@Override
	public void loadEnchantments(PlayState state, PlayerBodyData p) {
		enchantment = new Status(state, p) {

			@Override
			public int onCalcArmorReceive(int armor, float damage, BodyData perp, Hitbox damaging, DamageSource source, DamageTag... tags) {
				if (damaging != null) {
					boolean flip = Math.abs(p.getPlayer().getMouseHelper().getAttackAngle()) > 90;
					if (damaging.isPositionBasedOnUser()) {
						if (flip) {
							if ((perp.getSchmuck().getPixelPosition().x - p.getSchmuck().getPixelPosition().x) < p.getSchmuck().getSize().x / 2) {
								return armor + ARMOR_AMOUNT;
							}
						} else {
							if ((perp.getSchmuck().getPixelPosition().x - p.getSchmuck().getPixelPosition().x) > p.getSchmuck().getSize().x / 2) {
								return armor + ARMOR_AMOUNT;
							}
						}
					} else {
						if (flip) {
							if ((damaging.getPixelPosition().x - p.getSchmuck().getPixelPosition().x) < p.getSchmuck().getSize().x / 2) {
								return armor + ARMOR_AMOUNT;
							} else {
								if ((damaging.getPixelPosition().x - p.getSchmuck().getPixelPosition().x) > p.getSchmuck().getSize().x / 2) {
									return armor + ARMOR_AMOUNT;
								}
							}
						}
					}
				}
				return armor;
			}
		};
	}

	@Override
	public String[] getDescFields() {
		return new String[] {
				String.valueOf(ARMOR_AMOUNT)};
	}
}
