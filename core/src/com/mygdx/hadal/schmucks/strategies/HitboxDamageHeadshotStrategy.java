package com.mygdx.hadal.schmucks.strategies;

import com.mygdx.hadal.equip.Equipable;
import com.mygdx.hadal.schmucks.bodies.Player;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.schmucks.userdata.HadalData;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;

import static com.mygdx.hadal.utils.Constants.PPM;

public class HitboxDamageHeadshotStrategy extends HitboxStrategy{
	
	private static float headshotThreshold = 0.25f;
	
	private float bonusDamage, knockback;
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
				if ((hbox.getPixelPosition().y - p.getPlayer().getPixelPosition().y) > headshotThreshold * Player.hbHeight * Player.scale / PPM) {
					fixB.receiveDamage(bonusDamage, hbox.getLinearVelocity().nor().scl(knockback), creator, tool, true);
				}
			}
		}
	}
}
