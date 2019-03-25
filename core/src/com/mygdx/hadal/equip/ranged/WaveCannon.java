package com.mygdx.hadal.equip.ranged;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.effects.Sprite;
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

public class WaveCannon extends RangedWeapon {

	private final static String name = "Wave Cannon";
	private final static int clipSize = 4;
	private final static int ammoSize = 16;
	private final static float shootCd = 0.5f;
	private final static float shootDelay = 0;
	private final static float reloadTime = 1.5f;
	private final static int reloadAmount = 0;
	private final static float baseDamage = 20.0f;
	private final static float recoil = 12.5f;
	private final static float knockback = 18.0f;
	private final static float projectileSpeed = 30.0f;
	private final static int projectileWidth = 30;
	private final static int projectileHeight = 30;
	private final static float lifespan = 2.50f;
	private final static float gravity = 0;
	
	private final static int projDura = 1;
	
	private final static Sprite projSprite = Sprite.ORB_ORANGE;
	private final static Sprite weaponSprite = Sprite.MT_DEFAULT;
	private final static Sprite eventSprite = Sprite.P_DEFAULT;
	
	private final static float amplitude = 0.20f;
	private final static float frequency = 30;

	public WaveCannon(Schmuck user) {
		super(user, name, clipSize, ammoSize, reloadTime, recoil, projectileSpeed, shootCd, shootDelay, reloadAmount, true, weaponSprite, eventSprite);
	}
	
	@Override
	public void fire(PlayState state, Schmuck user, Vector2 startVelocity, float x, float y, short filter) {

		Hitbox hbox = new HitboxSprite(state, x, y, projectileWidth, projectileHeight, gravity, lifespan, projDura, 0, startVelocity,
				filter, true, true, user, projSprite);
		
		hbox.addStrategy(new HitboxDefaultStrategy(state, hbox, user.getBodyData()));
		hbox.addStrategy(new HitboxOnContactWallDieStrategy(state, hbox, user.getBodyData()));
		hbox.addStrategy(new HitboxDamageStandardStrategy(state, hbox, user.getBodyData(), this, baseDamage, knockback, DamageTypes.RANGED));
		
		hbox.addStrategy(new HitboxStrategy(state, hbox, user.getBodyData()) {
			
			private float elapsed = 0;
			
			@Override
			public void controller(float delta) {
				elapsed += delta;
				
				float c = (float)(Math.cos(hbox.getBody().getLinearVelocity().angleRad()));
				float s = (float)(Math.sin(hbox.getBody().getLinearVelocity().angleRad()));

				float wobble = amplitude * (float)Math.cos(frequency * elapsed) * frequency;

				hbox.getBody().setLinearVelocity(c * projectileSpeed - s * wobble, s * projectileSpeed + c * wobble);
			}
		});
		
		Hitbox hbox2 = new HitboxSprite(state, x, y, projectileWidth, projectileHeight, gravity, lifespan, projDura, 0, startVelocity,
				filter, true, true, user, projSprite);
		
		hbox2.addStrategy(new HitboxDefaultStrategy(state, hbox2, user.getBodyData()));
		hbox2.addStrategy(new HitboxOnContactWallDieStrategy(state, hbox2, user.getBodyData()));
		hbox2.addStrategy(new HitboxDamageStandardStrategy(state, hbox2, user.getBodyData(), this, baseDamage, knockback, DamageTypes.RANGED));
		
		hbox2.addStrategy(new HitboxStrategy(state, hbox2, user.getBodyData()) {
			
			private float elapsed = 0;
			
			@Override
			public void controller(float delta) {
				elapsed += delta;
				
				float c = (float)(Math.cos(hbox.getBody().getLinearVelocity().angleRad()));
				float s = (float)(Math.sin(hbox.getBody().getLinearVelocity().angleRad()));

				float wobble = -amplitude * (float)Math.cos(frequency * elapsed) * frequency;

				hbox.getBody().setLinearVelocity(c * projectileSpeed - s * wobble, s * projectileSpeed + c * wobble);
			}
		});
		
	}
}