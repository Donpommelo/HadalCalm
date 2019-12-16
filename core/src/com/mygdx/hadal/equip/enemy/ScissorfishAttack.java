package com.mygdx.hadal.equip.enemy;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.MeleeWeapon;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.bodies.hitboxes.MeleeHitbox;
import com.mygdx.hadal.schmucks.strategies.HitboxDamageStandardStrategy;
import com.mygdx.hadal.schmucks.strategies.HitboxDefaultStrategy;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;

public class ScissorfishAttack extends MeleeWeapon {

	private final static String name = "Scissorfish Scissor";
	private final static float swingCd = 0.5f;
	private final static float windup = 0.5f;
	private final static float backSwing = 1.0f;
	private final static float baseDamage = 8.0f;
	private final static int hitboxSize = 100;
	private final static int swingArc = 50;
	private final static float knockback = 22.5f;
	private final static float momentum = 5.0f;
	
	public ScissorfishAttack(Schmuck user) {
		super(user, name, swingCd, windup, momentum, Sprite.MT_DEFAULT, Sprite.P_DEFAULT);
	}
	
	@Override
	public void fire(PlayState state, Schmuck user, Vector2 startVelocity, float x, float y, short filter) {
		Hitbox hbox = new MeleeHitbox(state, x, y, hitboxSize, swingArc, swingCd, backSwing, weaponVelo, 
				new Vector2(0, 0), true, filter, user);
		
		hbox.addStrategy(new HitboxDefaultStrategy(state, hbox, user.getBodyData()));
		hbox.addStrategy(new HitboxDamageStandardStrategy(state, hbox, user.getBodyData(), this, baseDamage, knockback, DamageTypes.MELEE));	
	}
}
