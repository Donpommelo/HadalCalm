package com.mygdx.hadal.schmucks.strategies;

import com.mygdx.hadal.equip.Equipable;
import com.mygdx.hadal.schmucks.bodies.Player;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.schmucks.userdata.HadalData;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;

/**
 * This strategy makes a hbox inflict bonus damage when it does a "headshot"
 * "headshots" in this game just check if the hbox made contact with the top portion of a unit (if they are a player)
 * @author Zachary Tu
 *
 */
public class HitboxDamageHeadshotStrategy extends HitboxStrategy{
	
	//the top percent of the units body that counts as a "head"
	private static float headshotThreshold = 0.25f;
	
	//the amount of damage and knockback this hbox will inflict
	private float bonusDamage, knockback;
	
	////this is the tool that fired the hbox that has this strategy.
	private Equipable tool;
	
	public HitboxDamageHeadshotStrategy(PlayState state, Hitbox proj, BodyData user, Equipable tool, float damage, float knockback) {
		super(state, proj, user);
		this.bonusDamage = damage;
		this.knockback = knockback;
		this.tool = tool;
	}
	
	@Override
	public void onHit(HadalData fixB) {
		if (fixB != null) {
			if (fixB instanceof PlayerBodyData) {
				PlayerBodyData p = (PlayerBodyData) fixB;
				if ((hbox.getPixelPosition().y - p.getPlayer().getPixelPosition().y) > headshotThreshold * Player.hbHeight * Player.scale) {
					fixB.receiveDamage(bonusDamage, hbox.getLinearVelocity().nor().scl(knockback), creator, tool, true);
				}
			}
		}
	}
}
