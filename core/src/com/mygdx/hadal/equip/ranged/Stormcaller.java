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
import com.mygdx.hadal.schmucks.strategies.HitboxOnContactWallDieStrategy;
import com.mygdx.hadal.schmucks.strategies.HitboxStrategy;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;

import static com.mygdx.hadal.utils.Constants.PPM;

public class Stormcaller extends RangedWeapon {

	private final static String name = "Stormcaller";
	private final static int clipSize = 4;
	private final static int ammoSize = 16;
	private final static float shootCd = 0.3f;
	private final static float shootDelay = 0;
	private final static float reloadTime = 1.4f;
	private final static int reloadAmount = 0;
	private final static float baseDamage = 4.5f;
	private final static float recoil = 6.0f;
	private final static float knockback = 25.0f;
	private final static float projectileSpeed = 15.0f;
	private final static int projectileWidth = 20;
	private final static int projectileHeight = 20;
	private final static float lifespan = 2.5f;
	private final static float gravity = 0;
	
	private final static int projDura = 1;
	
	private final static float explosionInterval = 1/60f;
	
	private final static Sprite projSprite = Sprite.ORB_YELLOW;
	private final static Sprite weaponSprite = Sprite.MT_STORMCALLER;
	private final static Sprite eventSprite = Sprite.P_STORMCALLER;
	
	public Stormcaller(Schmuck user) {
		super(user, name, clipSize, ammoSize, reloadTime, recoil, projectileSpeed, shootCd, shootDelay, reloadAmount, true, weaponSprite, eventSprite);
	}

	@Override
	public void fire(PlayState state, final Schmuck user, Vector2 startVelocity, float x, float y, final short filter) {
		Hitbox hbox = new HitboxSprite(state, x, y, projectileWidth, projectileHeight, gravity, lifespan, projDura, 0, startVelocity,
				filter, true, true, user, projSprite);
		
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
					Hitbox pulse = new HitboxSprite(state, hbox.getPosition().x * PPM, hbox.getPosition().y * PPM, explosionSize, explosionSize, 
							gravity, explosionInterval, projDura, 0, new Vector2(0, 0), filter, true, true, user, projSprite);
					pulse.addStrategy(new HitboxDefaultStrategy(state, pulse, user.getBodyData()));
					pulse.addStrategy(new HitboxDamageStandardStrategy(state, pulse, user.getBodyData(), tool, baseDamage, knockback, DamageTypes.RANGED));
					
					explosionSize += 5;
					
					controllerCount -= delta;
				}
			}
		});
	}
}
