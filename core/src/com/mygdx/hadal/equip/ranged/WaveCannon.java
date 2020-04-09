package com.mygdx.hadal.equip.ranged;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.RangedWeapon;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.bodies.hitboxes.RangedHitbox;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;
import com.mygdx.hadal.strategies.HitboxStrategy;
import com.mygdx.hadal.strategies.hitbox.AdjustAngle;
import com.mygdx.hadal.strategies.hitbox.ContactUnitLoseDurability;
import com.mygdx.hadal.strategies.hitbox.ContactWallDie;
import com.mygdx.hadal.strategies.hitbox.ControllerDefault;
import com.mygdx.hadal.strategies.hitbox.DamageStandard;

public class WaveCannon extends RangedWeapon {

	private final static int clipSize = 5;
	private final static int ammoSize = 25;
	private final static float shootCd = 0.3f;
	private final static float shootDelay = 0;
	private final static float reloadTime = 1.5f;
	private final static int reloadAmount = 0;
	private final static float baseDamage = 35.0f;
	private final static float recoil = 12.5f;
	private final static float knockback = 28.0f;
	private final static float projectileSpeed = 40.0f;
	private final static Vector2 projectileSize = new Vector2(60, 20);
	private final static float lifespan = 0.75f;
	
	private final static Sprite projSprite = Sprite.ORB_ORANGE;
	private final static Sprite weaponSprite = Sprite.MT_DEFAULT;
	private final static Sprite eventSprite = Sprite.P_DEFAULT;
	
	private final static float amplitude = 0.30f;
	private final static float frequency = 30;

	private final static float pushInterval = 1 / 60f;
	
	public WaveCannon(Schmuck user) {
		super(user, clipSize, ammoSize, reloadTime, recoil, projectileSpeed, shootCd, shootDelay, reloadAmount, true, weaponSprite, eventSprite, projectileSize.x);
	}
	
	@Override
	public void fire(PlayState state, Schmuck user, Vector2 startPosition, final Vector2 startVelocity, short filter) {
		SoundEffect.SHOOT1.playUniversal(state, startPosition, 0.6f, false);

		Hitbox hbox = new RangedHitbox(state, startPosition, projectileSize, lifespan, startVelocity, filter, true, true, user, projSprite);
		
		hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));
		hbox.addStrategy(new AdjustAngle(state, hbox, user.getBodyData()));
		hbox.addStrategy(new ContactWallDie(state, hbox, user.getBodyData()));
		hbox.addStrategy(new ContactUnitLoseDurability(state, hbox, user.getBodyData()));
		hbox.addStrategy(new DamageStandard(state, hbox, user.getBodyData(), baseDamage, knockback, DamageTypes.RANGED));

		hbox.addStrategy(new HitboxStrategy(state, hbox, user.getBodyData()) {
			
			private float elapsed = 0;
			private float controllerCount = 0;
			
			@Override
			public void controller(float delta) {
				controllerCount += delta;
				
				while (controllerCount >= pushInterval) {
					controllerCount -= pushInterval;
					elapsed += pushInterval;
					
					//repeatedly apply force to hboxes to make them move in a wave-like shape
					float c = (float)(Math.cos(hbox.getLinearVelocity().angleRad()));
					float s = (float)(Math.sin(hbox.getLinearVelocity().angleRad()));

					float wobble = (float) (amplitude * Math.cos(frequency * elapsed) * frequency);

					hbox.setLinearVelocity(c * projectileSpeed - s * wobble, s * projectileSpeed + c * wobble);
				}
				
			}
		});
		
		Hitbox hbox2 = new RangedHitbox(state, startPosition, projectileSize, lifespan, startVelocity, filter, true, true, user, projSprite);
		
		hbox2.addStrategy(new ControllerDefault(state, hbox2, user.getBodyData()));
		hbox2.addStrategy(new AdjustAngle(state, hbox2, user.getBodyData()));
		hbox2.addStrategy(new ContactWallDie(state, hbox2, user.getBodyData()));
		hbox2.addStrategy(new DamageStandard(state, hbox2, user.getBodyData(), baseDamage, knockback, DamageTypes.RANGED));

		hbox2.addStrategy(new HitboxStrategy(state, hbox2, user.getBodyData()) {
			
			private float elapsed = 0;
			private float controllerCount = 0;
			
			@Override
			public void controller(float delta) {
				controllerCount += delta;
				
				while (controllerCount >= pushInterval) {
					controllerCount -= pushInterval;
					elapsed += pushInterval;
					
					//repeatedly apply force to hboxes to make them move in a wave-like shape
					float c = (float)(Math.cos(hbox.getLinearVelocity().angleRad()));
					float s = (float)(Math.sin(hbox.getLinearVelocity().angleRad()));

					float wobble = (float) (-amplitude * Math.cos(frequency * elapsed) * frequency);

					hbox.setLinearVelocity(c * projectileSpeed - s * wobble, s * projectileSpeed + c * wobble);
				}
			}
		});
	}
}