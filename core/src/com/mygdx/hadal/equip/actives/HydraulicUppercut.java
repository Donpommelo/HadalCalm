package com.mygdx.hadal.equip.actives;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.equip.ActiveItem;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.bodies.hitboxes.MeleeHitbox;
import com.mygdx.hadal.schmucks.strategies.HitboxDamageStandardStrategy;
import com.mygdx.hadal.schmucks.strategies.HitboxDefaultStrategy;
import com.mygdx.hadal.schmucks.strategies.HitboxOnContactWallSparks;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;
import com.mygdx.hadal.statuses.Invulnerability;
import com.mygdx.hadal.statuses.StatChangeStatus;

public class HydraulicUppercut extends ActiveItem {

	private final static String name = "Hydraulic Uppercut";
	private final static float usecd = 0.0f;
	private final static float usedelay = 0.0f;
	private final static float maxCharge = 6.0f;
	
	private final static float recoil = 150.0f;

	private final static float swingCd = 1.5f;
	private final static float backSwing = 1.0f;
	private final static float baseDamage = 70.0f;
	private final static int hitboxSize = 250;
	private final static int swingArc = 250;
	private final static float knockback = 75.0f;
	
	public HydraulicUppercut(Schmuck user) {
		super(user, name, usecd, usedelay, maxCharge, chargeStyle.byTime);
	}
	
	@Override
	public void useItem(PlayState state, PlayerBodyData user) {
		user.addStatus(new Invulnerability(state, 0.5f, user, user));
		user.addStatus(new StatChangeStatus(state, 0.5f, 9, 10.0f, user, user));

		user.getPlayer().push(0, recoil);
		
		Hitbox hbox = new MeleeHitbox(state, x, y, hitboxSize, swingArc, swingCd, backSwing, new Vector2(0, 0), 
				new Vector2(0, 0), true, user.getPlayer().getHitboxfilter(), user.getPlayer());
		
		hbox.addStrategy(new HitboxDefaultStrategy(state, hbox, user));
		hbox.addStrategy(new HitboxOnContactWallSparks(state, hbox, user));
		hbox.addStrategy(new HitboxDamageStandardStrategy(state, hbox, user, this, baseDamage, knockback, DamageTypes.MELEE));
	}

}
