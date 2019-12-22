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

import static com.mygdx.hadal.utils.Constants.PPM;

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
	private final static int projectileWidth = 10;
	private final static int projectileHeight = 10;
	private final static float lifespan = 1.8f;
	
	private final static float explosionInterval = 1/60f;
	private final static int explosionMaxSize = 120;
	
	private final static Sprite projSprite = Sprite.ORB_YELLOW;
	private final static Sprite weaponSprite = Sprite.MT_STORMCALLER;
	private final static Sprite eventSprite = Sprite.P_STORMCALLER;
	
	public Stormcaller(Schmuck user) {
		super(user, name, clipSize, ammoSize, reloadTime, recoil, projectileSpeed, shootCd, shootDelay, reloadAmount, true, weaponSprite, eventSprite, projectileWidth);
	}

	@Override
	public void fire(PlayState state, final Schmuck user, Vector2 startVelocity, float x, float y, final short filter) {
		Hitbox hbox = new RangedHitbox(state, x, y, projectileWidth, projectileHeight, lifespan, startVelocity, filter, true, true, user, projSprite);
		
		final Equipable tool = this;
		
		hbox.addStrategy(new HitboxDefaultStrategy(state, hbox, user.getBodyData()));
		hbox.addStrategy(new HitboxOnContactWallDieStrategy(state, hbox, user.getBodyData()));
		hbox.addStrategy(new HitboxStrategy(state, hbox, user.getBodyData()) {
			
			private float controllerCount = 0;
			private int explosionSize = projectileHeight;
			
			@Override
			public void controller(float delta) {
				controllerCount+=delta;

				if (controllerCount >= explosionInterval) {
					Hitbox pulse = new Hitbox(state, hbox.getPosition().x * PPM, hbox.getPosition().y * PPM, explosionSize, explosionSize, 
							explosionInterval, new Vector2(), filter, true, true, user, projSprite);
					pulse.addStrategy(new HitboxDefaultStrategy(state, pulse, user.getBodyData()));
					pulse.addStrategy(new HitboxDamageStandardStrategy(state, pulse, user.getBodyData(), tool, baseDamage, knockback, DamageTypes.RANGED));
					
					if (explosionSize <= explosionMaxSize) {
						explosionSize += 5;
					}
					
					controllerCount -= delta;
				}
			}
		});
	}
}
