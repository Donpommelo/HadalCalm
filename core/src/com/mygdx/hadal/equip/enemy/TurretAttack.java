package com.mygdx.hadal.equip.enemy;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.RangedWeapon;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.bodies.hitboxes.HitboxSprite;
import com.mygdx.hadal.schmucks.strategies.HitboxDamageStandardStrategy;
import com.mygdx.hadal.schmucks.strategies.HitboxDefaultStrategy;
import com.mygdx.hadal.schmucks.strategies.HitboxOnContactUnitLoseDuraStrategy;
import com.mygdx.hadal.schmucks.strategies.HitboxOnContactWallDieStrategy;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;

public class TurretAttack extends RangedWeapon {

	private final static String name = "Turret Gun";
	private final static int clipSize = 6;
	private final static int ammoSize = 1000;
	private final static float shootCd = 0.0f;
	private final static float shootDelay = 0.25f;
	private final static float reloadTime = 1.0f;
	private final static int reloadAmount = 0;
	private final static float baseDamage = 20.0f;
	private final static float recoil = 0.0f;
	private final static float knockback = 5.0f;
	private final static float projectileSpeed = 13.0f;
	private final static int projectileWidth = 192;
	private final static int projectileHeight = 24;
	private final static float lifespan = 2.0f;
	private final static float gravity = 0.0f;
	
	private final static Sprite projSprite = Sprite.BULLET;
	private final static int projDura = 1;
	
	public TurretAttack(Schmuck user) {
		super(user, name, clipSize, ammoSize, reloadTime, recoil, projectileSpeed, shootCd, shootDelay, reloadAmount);
	}
	
	@Override
	public void fire(PlayState state, Schmuck user, Vector2 startVelocity, float x, float y, short filter) {
		
		Hitbox hbox = new HitboxSprite(state, x, y, projectileWidth, projectileHeight, gravity, lifespan, projDura, 0, startVelocity,
				filter, true, true, user, projSprite);
		
		hbox.addStrategy(new HitboxDefaultStrategy(state, hbox, user.getBodyData()));
		hbox.addStrategy(new HitboxOnContactUnitLoseDuraStrategy(state, hbox, user.getBodyData()));
		hbox.addStrategy(new HitboxOnContactWallDieStrategy(state, hbox, user.getBodyData()));
		hbox.addStrategy(new HitboxDamageStandardStrategy(state, hbox, user.getBodyData(), this, baseDamage, knockback, DamageTypes.RANGED));		
	}
}
