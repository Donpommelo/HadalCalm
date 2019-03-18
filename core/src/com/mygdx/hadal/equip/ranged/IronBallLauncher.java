package com.mygdx.hadal.equip.ranged;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.Equipable;
import com.mygdx.hadal.equip.RangedWeapon;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.bodies.hitboxes.HitboxSprite;
import com.mygdx.hadal.schmucks.strategies.HitboxDamageStandardStrategy;
import com.mygdx.hadal.schmucks.strategies.HitboxDefaultStrategy;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;
import com.mygdx.hadal.utils.HitboxFactory;

public class IronBallLauncher extends RangedWeapon {

	private final static String name = "Iron Ball Launcher";
	private final static int clipSize = 1;
	private final static float shootCd = 0.25f;
	private final static float shootDelay = 0.25f;
	private final static float reloadTime = 1.25f;
	private final static int reloadAmount = 1;
	private final static float baseDamage = 45.0f;
	private final static float recoil = 15.0f;
	private final static float knockback = 50.0f;
	private final static float projectileSpeed = 40.0f;
	private final static int projectileWidth = 100;
	private final static int projectileHeight = 100;
	private final static float lifespan = 2.5f;
	private final static float gravity = 10;
	
	private final static int projDura = 5;
	
	private final static float restitution = 0.5f;

	private final static Sprite projSprite = Sprite.CANNONBALL;
	private final static Sprite weaponSprite = Sprite.MT_IRONBALL;
	private final static Sprite eventSprite = Sprite.P_IRONBALL;
	
	private final static HitboxFactory onShoot = new HitboxFactory() {

		@Override
		public void makeHitbox(final Schmuck user, PlayState state, Equipable tool, Vector2 startVelocity, float x, float y, short filter) {
			
			Hitbox hbox = new HitboxSprite(state, x, y, projectileWidth, projectileHeight, gravity, lifespan, projDura, restitution, startVelocity,
					filter, false, true, user, projSprite);
			
			hbox.addStrategy(new HitboxDefaultStrategy(state, hbox, user.getBodyData(), false));
			hbox.addStrategy(new HitboxDamageStandardStrategy(state, hbox, user.getBodyData(), tool, baseDamage, knockback, DamageTypes.RANGED));	
		}
	};
	
	public IronBallLauncher(Schmuck user) {
		super(user, name, clipSize, reloadTime, recoil, projectileSpeed, shootCd, shootDelay, reloadAmount, true, onShoot, weaponSprite, eventSprite);
	}
}
