package com.mygdx.hadal.equip.melee;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.MeleeWeapon;
import com.mygdx.hadal.schmucks.bodies.Player;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.strategies.HitboxDamageStandardStrategy;
import com.mygdx.hadal.schmucks.strategies.HitboxDefaultStrategy;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;

public class Fisticuffs extends MeleeWeapon {

	private final static float swingCd = 0.15f;
	private final static float windup = 0.0f;
	private final static float baseDamage = 12.0f;

	private final static Vector2 projectileSize = new Vector2(75, 75);
	private final static float lifespan = 0.1f;
	private final static float knockback = 25.0f;
	private final static Sprite projSprite = Sprite.IMPACT;
	private final static Sprite weaponSprite = Sprite.MT_DEFAULT;
	private final static Sprite eventSprite = Sprite.P_DEFAULT;
	
	private final static float range = 50.0f;
	
	public Fisticuffs(Schmuck user) {
		super(user, swingCd, windup, weaponSprite, eventSprite);
	}
	
	@Override
	public void fire(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity, short filter) {
		
		Vector2 projOffset = new Vector2(((Player)user).getMouse().getPixelPosition()).sub(user.getPixelPosition()).nor().scl(range).add(user.getPixelPosition());
		
		Hitbox hbox = new Hitbox(state, projOffset, projectileSize, lifespan, new Vector2(0, 0),	filter, true, true, user, projSprite);
		hbox.makeUnreflectable();
		
		hbox.addStrategy(new HitboxDefaultStrategy(state, hbox, user.getBodyData()));
		hbox.addStrategy(new HitboxDamageStandardStrategy(state, hbox, user.getBodyData(), this, baseDamage, knockback, DamageTypes.MELEE));
	}
}
