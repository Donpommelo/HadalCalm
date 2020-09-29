package com.mygdx.hadal.equip.ranged;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.RangedWeapon;
import com.mygdx.hadal.equip.WeaponUtils;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.bodies.hitboxes.RangedHitbox;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.strategies.HitboxStrategy;
import com.mygdx.hadal.strategies.hitbox.ContactWallDie;
import com.mygdx.hadal.strategies.hitbox.ControllerDefault;

public class ReticleStrike extends RangedWeapon {

	private final static int clipSize = 1;
	private final static int ammoSize = 25;
	private final static float shootCd = 0.1f;
	private final static float shootDelay = 0.0f;
	private final static float reloadTime = 0.9f;
	private final static int reloadAmount = 1;
	private final static float recoil = 12.0f;
	private final static float projectileSpeed = 30.0f;
	private final static Vector2 projectileSize = new Vector2(50, 50);
	private final static float lifespan = 2.0f;

	private final static Sprite projSprite = Sprite.NOTHING;
	private final static Sprite weaponSprite = Sprite.MT_IRONBALL;
	private final static Sprite eventSprite = Sprite.P_IRONBALL;
	
	private final static float reticleSize = 80.0f;
	private final static float reticleSizeSquared = 6500.0f;
	private final static float reticleLifespan = 0.5f;
	private final static int explosionRadius = 100;
	private final static float explosionDamage = 40.0f;
	private final static float explosionKnockback = 20.0f;
	
	public ReticleStrike(Schmuck user) {
		super(user, clipSize, ammoSize, reloadTime, recoil, projectileSpeed, shootCd, shootDelay, reloadAmount, true, weaponSprite, eventSprite, projectileSize.x);
	}
	
	@Override
	public void fire(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity, short filter) {
		SoundEffect.LOCKANDLOAD.playUniversal(state, startPosition, 0.8f, false);

		Hitbox hbox = new RangedHitbox(state, startPosition, projectileSize, lifespan, startVelocity, filter, true, true, user, projSprite);
		
		hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));
		hbox.addStrategy(new ContactWallDie(state, hbox, user.getBodyData()));
		hbox.addStrategy(new HitboxStrategy(state, hbox, user.getBodyData()) {
			
			private Vector2 lastPosition = new Vector2(startPosition);
			@Override
			public void controller(float delta) {
				if (lastPosition.dst2(hbox.getPixelPosition()) > reticleSizeSquared) {
					lastPosition.set(hbox.getPixelPosition());
					WeaponUtils.createExplodingReticle(state, hbox.getPixelPosition(), creator.getSchmuck(), reticleSize, reticleLifespan, explosionDamage, explosionKnockback, explosionRadius);
				}
			}
		});
	}
}
