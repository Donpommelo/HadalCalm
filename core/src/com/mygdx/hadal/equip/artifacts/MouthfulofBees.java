package com.mygdx.hadal.equip.artifacts;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.equip.WeaponUtils;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;
import com.mygdx.hadal.statuses.Status;

import static com.mygdx.hadal.utils.Constants.PRIORITY_SCALE;

public class MouthfulofBees extends Artifact {

	private static final int slotCost = 1;

	private static final float beeSpeed = 15.0f;
	private static final float damagePerBee = 20.0f;
	private static final int beesOnDeath = 5;
	private static final int homeRadius = 60;

	public MouthfulofBees() {
		super(slotCost);
	}

	@Override
	public void loadEnchantments(PlayState state, PlayerBodyData p) {
		enchantment = new Status(state, p) {
			
			@Override
			public float onReceiveDamage(float damage, BodyData perp, Hitbox damaging, DamageTypes... tags) {
				if (damage > 0) {
					WeaponUtils.createBees(state, p.getSchmuck().getPixelPosition(), p.getSchmuck(),
						(int) (damage / damagePerBee), homeRadius, new Vector2(0, beeSpeed), false, p.getSchmuck().getHitboxfilter());
				}
				return damage;
			}
			
			@Override
			public void onDeath(BodyData perp) {
				WeaponUtils.createBees(state, p.getSchmuck().getPixelPosition(), p.getSchmuck(),
					beesOnDeath, homeRadius, new Vector2(0, beeSpeed), false, p.getSchmuck().getHitboxfilter());
			}
		}.setPriority(PRIORITY_SCALE);
	}
}
