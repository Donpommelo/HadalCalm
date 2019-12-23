package com.mygdx.hadal.equip.ranged;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.Equipable;
import com.mygdx.hadal.equip.RangedWeapon;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.bodies.hitboxes.RangedHitbox;
import com.mygdx.hadal.schmucks.strategies.HitboxDamageStandardStrategy;
import com.mygdx.hadal.schmucks.strategies.HitboxDefaultStrategy;
import com.mygdx.hadal.schmucks.strategies.HitboxOnContactWallDieStrategy;
import com.mygdx.hadal.schmucks.strategies.HitboxStrategy;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;

public class Stormcaller extends RangedWeapon {

	private final static String name = "Stormcaller";
	private final static int clipSize = 3;
	private final static int ammoSize = 12;
	private final static float shootCd = 0.3f;
	private final static float shootDelay = 0;
	private final static float reloadTime = 1.4f;
	private final static int reloadAmount = 0;
	private final static float baseDamage = 5.0f;
	private final static float recoil = 6.0f;
	private final static float knockback = 25.0f;
	private final static float projectileSpeed = 15.0f;
	private final static Vector2 projectileSize = new Vector2(10, 10);
	private final static float lifespan = 1.8f;
	
	private final static float explosionInterval = 1/60f;
	private final static Vector2 explosionSize = new Vector2(0, 0);
	private final static int explosionMaxSize = 150;
	
	private final static Sprite projSprite = Sprite.ORB_YELLOW;
	private final static Sprite weaponSprite = Sprite.MT_STORMCALLER;
	private final static Sprite eventSprite = Sprite.P_STORMCALLER;
	
	
	
	public Stormcaller(Schmuck user) {
		super(user, name, clipSize, ammoSize, reloadTime, recoil, projectileSpeed, shootCd, shootDelay, reloadAmount, true, weaponSprite, eventSprite, projectileSize.x);
	}

	@Override
	public void fire(PlayState state, final Schmuck user, Vector2 startPosition, Vector2 startVelocity, final short filter) {
		Hitbox hbox = new RangedHitbox(state, startPosition, projectileSize, lifespan, startVelocity, filter, true, true, user, Sprite.NOTHING);
		
		final Equipable tool = this;
		
		explosionSize.set(projectileSize);
		
		hbox.addStrategy(new HitboxDefaultStrategy(state, hbox, user.getBodyData()));
		hbox.addStrategy(new HitboxOnContactWallDieStrategy(state, hbox, user.getBodyData()));
		hbox.addStrategy(new HitboxStrategy(state, hbox, user.getBodyData()) {
			
			private float controllerCount = 0;
			
			@Override
			public void controller(float delta) {
				controllerCount+=delta;

				if (controllerCount >= explosionInterval) {
					
					Hitbox pulse = new Hitbox(state, hbox.getPixelPosition(), explosionSize, explosionInterval, new Vector2(), filter, true, true, user, projSprite);
					pulse.addStrategy(new HitboxDefaultStrategy(state, pulse, user.getBodyData()));
					pulse.addStrategy(new HitboxDamageStandardStrategy(state, pulse, user.getBodyData(), tool, baseDamage, knockback, DamageTypes.RANGED));
					
					if (explosionSize.x <= explosionMaxSize) {
						explosionSize.add(3, 3);
					}
					
					controllerCount -= explosionInterval;
				}
			}
		});
	}
}
