package com.mygdx.hadal.equip.ranged;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.RangedWeapon;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.bodies.hitboxes.RangedHitbox;
import com.mygdx.hadal.schmucks.strategies.HitboxDamageStandardStrategy;
import com.mygdx.hadal.schmucks.strategies.HitboxDefaultStrategy;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;

public class IronBallLauncher extends RangedWeapon {

	private final static String name = "Iron Ball Launcher";
	private final static int clipSize = 1;
	private final static int ammoSize = 22;
	private final static float shootCd = 0.25f;
	private final static float shootDelay = 0.25f;
	private final static float reloadTime = 0.9f;
	private final static int reloadAmount = 1;
	private final static float baseDamage = 55.0f;
	private final static float recoil = 15.0f;
	private final static float knockback = 50.0f;
	private final static float projectileSpeed = 40.0f;
	private final static int projectileWidth = 50;
	private final static int projectileHeight = 50;
	private final static float lifespan = 2.5f;

	private final static Sprite projSprite = Sprite.CANNONBALL;
	private final static Sprite weaponSprite = Sprite.MT_IRONBALL;
	private final static Sprite eventSprite = Sprite.P_IRONBALL;
	
	public IronBallLauncher(Schmuck user) {
		super(user, name, clipSize, ammoSize, reloadTime, recoil, projectileSpeed, shootCd, shootDelay, reloadAmount, true, weaponSprite, eventSprite, projectileWidth);
	}
	
	@Override
	public void fire(PlayState state, Schmuck user, Vector2 startVelocity, float x, float y, short filter) {
		Hitbox hbox = new RangedHitbox(state, x, y, projectileWidth, projectileHeight, lifespan, startVelocity,	filter, false, true, user, projSprite);
		hbox.setGravity(10);
		hbox.setFriction(1.0f);
		hbox.setRestitution(0.5f);
		
		hbox.addStrategy(new HitboxDefaultStrategy(state, hbox, user.getBodyData(), false));
		hbox.addStrategy(new HitboxDamageStandardStrategy(state, hbox, user.getBodyData(), this, baseDamage, knockback, DamageTypes.RANGED));	
	}
}
