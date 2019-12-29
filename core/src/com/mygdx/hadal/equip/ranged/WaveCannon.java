package com.mygdx.hadal.equip.ranged;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.RangedWeapon;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.bodies.hitboxes.RangedHitbox;
import com.mygdx.hadal.schmucks.strategies.HitboxDamageStandardStrategy;
import com.mygdx.hadal.schmucks.strategies.HitboxDefaultStrategy;
import com.mygdx.hadal.schmucks.strategies.HitboxOnContactUnitLoseDuraStrategy;
import com.mygdx.hadal.schmucks.strategies.HitboxOnContactWallDieStrategy;
import com.mygdx.hadal.schmucks.strategies.HitboxStrategy;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;

public class WaveCannon extends RangedWeapon {

	private final static String name = "Wave Cannon";
	private final static int clipSize = 5;
	private final static int ammoSize = 20;
	private final static float shootCd = 0.3f;
	private final static float shootDelay = 0;
	private final static float reloadTime = 1.5f;
	private final static int reloadAmount = 0;
	private final static float baseDamage = 25.0f;
	private final static float recoil = 12.5f;
	private final static float knockback = 28.0f;
	private final static float projectileSpeed = 40.0f;
	private final static Vector2 projectileSize = new Vector2(15, 15);
	private final static float lifespan = 0.6f;
	
	private final static Sprite projSprite = Sprite.ORB_ORANGE;
	private final static Sprite weaponSprite = Sprite.MT_DEFAULT;
	private final static Sprite eventSprite = Sprite.P_DEFAULT;
	
	private final static float amplitude = 0.20f;
	private final static float frequency = 30;

	public WaveCannon(Schmuck user) {
		super(user, name, clipSize, ammoSize, reloadTime, recoil, projectileSpeed, shootCd, shootDelay, reloadAmount, true, weaponSprite, eventSprite, projectileSize.x);
	}
	
	@Override
	public void fire(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity, short filter) {

		Hitbox hbox = new RangedHitbox(state, startPosition, projectileSize, lifespan, startVelocity, filter, true, true, user, projSprite);
		
		hbox.addStrategy(new HitboxDefaultStrategy(state, hbox, user.getBodyData()));
		hbox.addStrategy(new HitboxOnContactWallDieStrategy(state, hbox, user.getBodyData()));
		hbox.addStrategy(new HitboxOnContactUnitLoseDuraStrategy(state, hbox, user.getBodyData()));
		hbox.addStrategy(new HitboxDamageStandardStrategy(state, hbox, user.getBodyData(), this, baseDamage, knockback, DamageTypes.RANGED));
		
		hbox.addStrategy(new HitboxStrategy(state, hbox, user.getBodyData()) {
			
			private float elapsed = 0;
			
			@Override
			public void controller(float delta) {
				elapsed += delta;
				
				//repeatedly apply force to hboxes to make them move in a wave-like shape
				float c = (float)(Math.cos(hbox.getLinearVelocity().angleRad()));
				float s = (float)(Math.sin(hbox.getLinearVelocity().angleRad()));

				float wobble = amplitude * (float)Math.cos(frequency * elapsed) * frequency;

				hbox.setLinearVelocity(c * projectileSpeed - s * wobble, s * projectileSpeed + c * wobble);
			}
		});
		
		Hitbox hbox2 = new RangedHitbox(state, startPosition, projectileSize, lifespan, startVelocity, filter, true, true, user, projSprite);
		
		hbox2.addStrategy(new HitboxDefaultStrategy(state, hbox2, user.getBodyData()));
		hbox2.addStrategy(new HitboxOnContactWallDieStrategy(state, hbox2, user.getBodyData()));
		hbox2.addStrategy(new HitboxDamageStandardStrategy(state, hbox2, user.getBodyData(), this, baseDamage, knockback, DamageTypes.RANGED));
		
		hbox2.addStrategy(new HitboxStrategy(state, hbox2, user.getBodyData()) {
			
			private float elapsed = 0;
			
			@Override
			public void controller(float delta) {
				elapsed += delta;
				
				//repeatedly apply force to hboxes to make them move in a wave-like shape
				float c = (float)(Math.cos(hbox.getLinearVelocity().angleRad()));
				float s = (float)(Math.sin(hbox.getLinearVelocity().angleRad()));

				float wobble = -amplitude * (float)Math.cos(frequency * elapsed) * frequency;

				hbox.setLinearVelocity(c * projectileSpeed - s * wobble, s * projectileSpeed + c * wobble);
			}
		});
	}
}