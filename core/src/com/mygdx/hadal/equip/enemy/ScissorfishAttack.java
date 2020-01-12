package com.mygdx.hadal.equip.enemy;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.MeleeWeapon;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.strategies.DamageStandard;
import com.mygdx.hadal.schmucks.strategies.ControllerDefault;
import com.mygdx.hadal.schmucks.strategies.FixedToUser;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;

public class ScissorfishAttack extends MeleeWeapon {

	private final static float swingCd = 0.5f;
	private final static float windup = 0.5f;
	private final static float baseDamage = 8.0f;
	private final static Vector2 hitboxSize = new Vector2(100, 50);
	private final static float knockback = 22.5f;
	
	public ScissorfishAttack(Schmuck user) {
		super(user, swingCd, windup, Sprite.MT_DEFAULT, Sprite.P_DEFAULT);
	}
	
	@Override
	public void fire(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity, short filter) {
		Hitbox hbox = new Hitbox(state, startPosition, hitboxSize, swingCd, weaponVelo, filter, true, true, user, Sprite.NOTHING);
		hbox.makeUnreflectable();
		
		hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));
		hbox.addStrategy(new DamageStandard(state, hbox, user.getBodyData(), baseDamage, knockback, DamageTypes.MELEE));
		hbox.addStrategy(new FixedToUser(state, hbox, user.getBodyData(), startVelocity, new Vector2(0, 0), false));
	}
}
