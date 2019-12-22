package com.mygdx.hadal.equip.actives;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.ActiveItem;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.strategies.HitboxDamageStandardStrategy;
import com.mygdx.hadal.schmucks.strategies.HitboxDefaultStrategy;
import com.mygdx.hadal.schmucks.strategies.HitboxFixedToUserStrategy;
import com.mygdx.hadal.schmucks.strategies.HitboxOnContactWallParticles;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;
import com.mygdx.hadal.statuses.Invulnerability;
import com.mygdx.hadal.statuses.StatChangeStatus;
import com.mygdx.hadal.utils.Stats;

public class HydraulicUppercut extends ActiveItem {

	private final static String name = "Hydraulic Uppercut";
	private final static float usecd = 0.0f;
	private final static float usedelay = 0.0f;
	private final static float maxCharge = 6.0f;
	
	private final static float recoil = 150.0f;

	private final static float baseDamage = 40.0f;
	private final static int hitboxSize = 125;
	private final static float lifespan = 0.25f;
	private final static float knockback = 75.0f;
	
	public HydraulicUppercut(Schmuck user) {
		super(user, name, usecd, usedelay, maxCharge, chargeStyle.byTime);
	}
	
	@Override
	public void useItem(PlayState state, PlayerBodyData user) {
		user.addStatus(new Invulnerability(state, 0.5f, user, user));
		user.addStatus(new StatChangeStatus(state, 0.5f, Stats.AIR_DRAG, 10.0f, user, user));

		user.getPlayer().pushMomentumMitigation(0, recoil);
		
		Hitbox hbox = new Hitbox(state, x, y, hitboxSize, hitboxSize, lifespan, new Vector2(), user.getPlayer().getHitboxfilter(),  true, true, user.getPlayer(), Sprite.NOTHING);
		hbox.makeUnreflectable();
		
		hbox.addStrategy(new HitboxDefaultStrategy(state, hbox, user));
		hbox.addStrategy(new HitboxOnContactWallParticles(state, hbox, user , Particle.SPARK_TRAIL));
		hbox.addStrategy(new HitboxDamageStandardStrategy(state, hbox, user, this, baseDamage, knockback, DamageTypes.MELEE));
		hbox.addStrategy(new HitboxFixedToUserStrategy(state, hbox, user, new Vector2(), new Vector2(), false));
	}
}
