package com.mygdx.hadal.equip.actives;

import static com.mygdx.hadal.utils.Constants.PPM;

import com.mygdx.hadal.equip.ActiveItem;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.schmucks.bodies.hitboxes.HitboxImage;
import com.mygdx.hadal.schmucks.strategies.HitboxDamageStandardStrategy;
import com.mygdx.hadal.schmucks.strategies.HitboxDefaultStrategy;
import com.mygdx.hadal.schmucks.strategies.HitboxOnContactStandardStrategy;
import com.mygdx.hadal.schmucks.strategies.HitboxOnDieFireFragStrategy;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;

public class Uppercut extends ActiveItem {

	private final static String name = "Fireball";
	private final static float usecd = 0.0f;
	private final static float usedelay = 0.1f;
	private final static float maxCharge = 6.0f;
	

	private final static int projectileWidth = 200;
	private final static int projectileHeight = 200;
	private final static float lifespan = 5.0f;
	private final static float gravity = 0;
	private final static float projectileSpeed = 12.0f;

	private final static int projDura = 1;
	private final static int numFrag = 20;
	
	private final static String projSpriteId = "orb_orange";
	
	private final static float baseDamage = 50.0f;
	private final static float knockback = 40.0f;
	private final static float recoil = 20.0f;
	
	public Uppercut(Schmuck user) {
		super(user, name, usecd, usedelay, maxCharge, chargeStyle.byTime);
	}
	
	@Override
	public void useItem(PlayState state, PlayerBodyData user) {
			
			HitboxImage hbox = new HitboxImage(state, 
					user.getPlayer().getBody().getPosition().x * PPM, 
					user.getPlayer().getBody().getPosition().y * PPM,
					projectileWidth, projectileHeight, gravity, lifespan, projDura, 0, this.weaponVelo.scl(projectileSpeed),
					user.getPlayer().getHitboxfilter(), true, true, user.getPlayer(), projSpriteId);
			
			hbox.addStrategy(new HitboxDefaultStrategy(state, hbox, user));
			hbox.addStrategy(new HitboxOnContactStandardStrategy(state, hbox, user));
			hbox.addStrategy(new HitboxDamageStandardStrategy(state, hbox, user, this, baseDamage, knockback, DamageTypes.RANGED));
			hbox.addStrategy(new HitboxOnDieFireFragStrategy(state, hbox, user, this, numFrag, user.getPlayer().getHitboxfilter()));

			user.getPlayer().recoil(x, y, recoil);
	}

}
