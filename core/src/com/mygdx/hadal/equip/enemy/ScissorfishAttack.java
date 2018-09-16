package com.mygdx.hadal.equip.enemy;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.equip.Equipable;
import com.mygdx.hadal.equip.MeleeWeapon;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.bodies.hitboxes.MeleeHitbox;
import com.mygdx.hadal.schmucks.strategies.HitboxDamageStandardStrategy;
import com.mygdx.hadal.schmucks.strategies.HitboxDefaultStrategy;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;
import com.mygdx.hadal.utils.HitboxFactory;

public class ScissorfishAttack extends MeleeWeapon {

	private final static String name = "Scissorfish Scissor";
	private final static float swingCd = 0.5f;
	private final static float windup = 0.5f;
	private final static float backSwing = 1.0f;
	private final static float baseDamage = 12.0f;
	private final static int hitboxSize = 200;
	private final static int swingArc = 100;
	private final static float knockback = 22.5f;
	private final static float momentum = 5.0f;
	
	
	private final static HitboxFactory onSwing = new HitboxFactory() {

		@Override
		public void makeHitbox(final Schmuck user, PlayState state, Equipable tool, Vector2 startAngle, float x, float y, short filter) {
			
			Hitbox hbox = new MeleeHitbox(state, x, y, hitboxSize, swingArc, swingCd, backSwing, startAngle, 
					new Vector2(0, 0), true, filter, user);
			
			hbox.addStrategy(new HitboxDefaultStrategy(state, hbox, user.getBodyData()));
			hbox.addStrategy(new HitboxDamageStandardStrategy(state, hbox, user.getBodyData(), tool, baseDamage, knockback, DamageTypes.MELEE));	
		}
	};
	
	public ScissorfishAttack(Schmuck user) {
		super(user, name, swingCd, windup, momentum, onSwing);
	}

}
