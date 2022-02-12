package com.mygdx.hadal.strategies.hitbox;

import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.schmucks.userdata.HadalData;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.strategies.HitboxStrategy;

/**
 * This strategy makes a hbox inflict bonus damage when it does a "headshot"
 * "headshots" in this game just check if the hbox made contact with the top portion of a unit (if they are a player)
 * when activated, this strategy increases the damage multiplier of the hbox, meaning it must be added before the damage-inflicting strategy
 * @author Gicciatello Gnurkagdalena
 */
public class DamageHeadshot extends HitboxStrategy {
	
	//the top percent of the units body that counts as a "head"
	private static final float headshotThreshold = 0.15f;
	
	//the amount of damage and knockback this hbox will inflict
	private final float bonusDamage;

	//max time the projectile continues to get stronger
	private final float maxCharge;

	public DamageHeadshot(PlayState state, Hitbox proj, BodyData user, float damage, float maxCharge) {
		super(state, proj, user);
		this.bonusDamage = damage;
		this.maxCharge = maxCharge;
	}

	private float currentCharge;
	@Override
	public void controller(float delta) {
		if (currentCharge < maxCharge) {
			currentCharge += delta;
			if (currentCharge > maxCharge) {
				currentCharge = maxCharge;
			}
		}
	}

	@Override
	public void onHit(HadalData fixB) {
		if (fixB != null) {
			if (fixB instanceof PlayerBodyData p) {
				if ((hbox.getPixelPosition().y - p.getPlayer().getPixelPosition().y) > headshotThreshold * p.getPlayer().getSize().y) {
					float headshotBonus = currentCharge / maxCharge * bonusDamage;
					hbox.setDamageMultiplier(hbox.getDamageMultiplier() + headshotBonus);
				}
			}
		}
	}
}
