package com.mygdx.hadal.equip.ranged;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.RangedWeapon;
import com.mygdx.hadal.schmucks.bodies.ParticleEntity;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.schmucks.bodies.ParticleEntity.particleSyncType;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.bodies.hitboxes.RangedHitbox;
import com.mygdx.hadal.schmucks.strategies.HitboxDamageStandardStrategy;
import com.mygdx.hadal.schmucks.strategies.HitboxStrategy;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;

public class Iceberg extends RangedWeapon {

	private final static String name = "Iceberg";
	private final static int clipSize = 5;
	private final static int ammoSize = 20;
	private final static float shootCd = 0.5f;
	private final static float shootDelay = 0.15f;
	private final static float reloadTime = 1.4f;
	private final static int reloadAmount = 0;
	private final static float baseDamage = 40.0f;
	private final static float recoil = 15.0f;
	private final static float knockback = 30.0f;
	private final static float projectileSpeed = 33.0f;
	private final static Vector2 projectileSize = new Vector2(50, 50);
	private final static float lifespan = 3.0f;

	private final static Sprite projSprite = Sprite.ORB_BLUE;
	private final static Sprite weaponSprite = Sprite.MT_ICEBERG;
	private final static Sprite eventSprite = Sprite.P_ICEBERG;
	
	public Iceberg(Schmuck user) {
		super(user, name, clipSize, ammoSize, reloadTime, recoil, projectileSpeed, shootCd, shootDelay, reloadAmount, true, weaponSprite, eventSprite, projectileSize.x);
	}
	
	@Override
	public void fire(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity, short filter) {
		Hitbox hbox = new RangedHitbox(state, startPosition, projectileSize, lifespan, startVelocity, filter, false, true, user, projSprite);
		hbox.setGravity(10);
		
		hbox.addStrategy(new HitboxDamageStandardStrategy(state, hbox, user.getBodyData(), this, baseDamage, knockback, DamageTypes.RANGED));	
		hbox.addStrategy(new HitboxStrategy(state, hbox, user.getBodyData()) {
			
			float controllerCount = 0;
			float lastX = 0;
			
			@Override
			public void create() {
				hbox.setTransform(hbox.getPosition(), 0);
			}
			
			@Override
			public void controller(float delta) {
				hbox.setLifeSpan(hbox.getLifeSpan() - delta);
				if (hbox.getLifeSpan() <= 0) {
					hbox.die();
				}
				controllerCount+=delta;
				if (controllerCount >= 1/60f) {
					
					if (hbox.getLinearVelocity().x == 0) {
						hbox.setLinearVelocity(-lastX, hbox.getLinearVelocity().y);
					}
					
					lastX = hbox.getLinearVelocity().x;
				}
			}
			
			private Vector2 impulse = new Vector2();
			
			@Override
			public void push(Vector2 push) {
				hbox.applyLinearImpulse(impulse.set(push).scl(0.2f));
			}
			
			@Override
			public void die() {
				hbox.queueDeletion();
			}
		});
		new ParticleEntity(state, hbox, Particle.BUBBLE_TRAIL, 3.0f, 0.0f, true, particleSyncType.CREATESYNC);
	}
}
