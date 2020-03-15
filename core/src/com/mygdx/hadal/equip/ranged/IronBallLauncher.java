package com.mygdx.hadal.equip.ranged;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.RangedWeapon;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.bodies.hitboxes.RangedHitbox;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;
import com.mygdx.hadal.strategies.hitbox.ControllerDefault;
import com.mygdx.hadal.strategies.hitbox.DamageStandard;

public class IronBallLauncher extends RangedWeapon {

	private final static int clipSize = 1;
	private final static int ammoSize = 22;
	private final static float shootCd = 0.25f;
	private final static float shootDelay = 0.25f;
	private final static float reloadTime = 0.9f;
	private final static int reloadAmount = 1;
	private final static float baseDamage = 50.0f;
	private final static float recoil = 15.0f;
	private final static float knockback = 50.0f;
	private final static float projectileSpeed = 40.0f;
	private final static Vector2 projectileSize = new Vector2(50, 50);
	private final static float lifespan = 2.5f;

	private final static Sprite projSprite = Sprite.CANNONBALL;
	private final static Sprite weaponSprite = Sprite.MT_IRONBALL;
	private final static Sprite eventSprite = Sprite.P_IRONBALL;
	
	public IronBallLauncher(Schmuck user) {
		super(user, clipSize, ammoSize, reloadTime, recoil, projectileSpeed, shootCd, shootDelay, reloadAmount, true, weaponSprite, eventSprite, projectileSize.x);
	}
	
	@Override
	public void fire(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity, short filter) {
		Hitbox hbox = new RangedHitbox(state, startPosition, projectileSize, lifespan, startVelocity, filter, false, true, user, projSprite);
		hbox.setGravity(10);
		hbox.setFriction(1.0f);
		hbox.setRestitution(0.5f);
		
		hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));
		hbox.addStrategy(new DamageStandard(state, hbox, user.getBodyData(), baseDamage, knockback, DamageTypes.RANGED));	
	}
}
