package com.mygdx.hadal.equip.actives;


import static com.mygdx.hadal.utils.Constants.PPM;

import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.ActiveItem;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.bodies.hitboxes.HitboxSprite;
import com.mygdx.hadal.schmucks.strategies.HitboxDamageStandardStrategy;
import com.mygdx.hadal.schmucks.strategies.HitboxDefaultStrategy;
import com.mygdx.hadal.schmucks.strategies.HitboxOnContactUnitDieStrategy;
import com.mygdx.hadal.schmucks.strategies.HitboxOnDieExplodeStrategy;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;

public class NauticalMine extends ActiveItem {

	private final static String name = "Nautical Mine";
	private final static float usecd = 0.0f;
	private final static float usedelay = 0.1f;
	private final static float maxCharge = 11.0f;
	
	private final static float baseDamage = 15.0f;
	private final static float knockback = 0.0f;
	private final static int projectileWidth = 250;
	private final static float lifespan = 5.0f;
	private final static float gravity = 0.1f;
	private final static float restitution = 1.0f;
	
	private final static int projDura = 1;
		
	private final static int explosionRadius = 500;
	private final static float explosionDamage = 45.0f;
	private final static float explosionKnockback = 40.0f;
	
	private final static float projectileSpeed = 5.0f;
	
	private final static Sprite projSprite = Sprite.GRENADE;

	public NauticalMine(Schmuck user) {
		super(user, name, usecd, usedelay, maxCharge, chargeStyle.byTime);
	}
	
	@Override
	public void useItem(PlayState state, PlayerBodyData user) {
		
		Hitbox hbox = new HitboxSprite(state, 
				user.getPlayer().getPosition().x * PPM, 
				user.getPlayer().getPosition().y * PPM,
				projectileWidth, projectileWidth, gravity, lifespan, projDura, restitution, 
				this.weaponVelo.scl(projectileSpeed), user.getPlayer().getHitboxfilter(), 
				false, false, user.getPlayer(), projSprite);
		
		hbox.addStrategy(new HitboxDefaultStrategy(state, hbox, user, false));
		hbox.addStrategy(new HitboxOnContactUnitDieStrategy(state, hbox, user));
		hbox.addStrategy(new HitboxDamageStandardStrategy(state, hbox, user, this, baseDamage, knockback, DamageTypes.RANGED));
		hbox.addStrategy(new HitboxOnDieExplodeStrategy(state, hbox, user, this, explosionRadius, explosionDamage, explosionKnockback, (short)0));
	}

}
