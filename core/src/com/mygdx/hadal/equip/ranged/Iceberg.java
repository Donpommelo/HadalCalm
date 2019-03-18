package com.mygdx.hadal.equip.ranged;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.Equipable;
import com.mygdx.hadal.equip.RangedWeapon;
import com.mygdx.hadal.schmucks.bodies.ParticleEntity;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.schmucks.bodies.ParticleEntity.particleSyncType;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.bodies.hitboxes.HitboxSprite;
import com.mygdx.hadal.schmucks.strategies.HitboxDamageStandardStrategy;
import com.mygdx.hadal.schmucks.strategies.HitboxStrategy;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;
import com.mygdx.hadal.utils.HitboxFactory;

public class Iceberg extends RangedWeapon {

	private final static String name = "Iceberg";
	private final static int clipSize = 5;
	private final static float shootCd = 0.5f;
	private final static float shootDelay = 0.15f;
	private final static float reloadTime = 2.2f;
	private final static int reloadAmount = 0;
	private final static float baseDamage = 40.0f;
	private final static float recoil = 15.0f;
	private final static float knockback = 30.0f;
	private final static float projectileSpeed = 30.0f;
	private final static int projectileWidth = 132;
	private final static int projectileHeight = 130;
	private final static float lifespan = 4.0f;
	private final static float gravity = 10;
	
	private final static int projDura = 5;
	
	private final static float restitution = 0.0f;

	private final static Sprite projSprite = Sprite.ORB_BLUE;
	private final static Sprite weaponSprite = Sprite.MT_ICEBERG;
	private final static Sprite eventSprite = Sprite.P_ICEBERG;
	
	private final static HitboxFactory onShoot = new HitboxFactory() {

		@Override
		public void makeHitbox(final Schmuck user, PlayState state, Equipable tool, Vector2 startVelocity, float x, float y, short filter) {
			
			Hitbox hbox = new HitboxSprite(state, x, y, projectileWidth, projectileHeight, gravity, lifespan, projDura, restitution, 
					startVelocity, filter, false, true, user, projSprite);
			
			hbox.addStrategy(new HitboxDamageStandardStrategy(state, hbox, user.getBodyData(), tool, baseDamage, knockback, DamageTypes.RANGED));	
			hbox.addStrategy(new HitboxStrategy(state, hbox, user.getBodyData()) {
				
				float controllerCount = 0;
				float lastX = 0;
				
				@Override
				public void create() {
					hbox.getBody().setTransform(hbox.getBody().getPosition(), 0);
				}
				
				@Override
				public void controller(float delta) {
					hbox.setLifeSpan(hbox.getLifeSpan() - delta);
					if (hbox.getLifeSpan() <= 0) {
						hbox.die();
					}
					controllerCount+=delta;
					if (controllerCount >= 1/60f) {
						
						if (hbox.getBody().getLinearVelocity().x == 0) {
							hbox.getBody().setLinearVelocity(-lastX, hbox.getBody().getLinearVelocity().y);
						}
						
						lastX = hbox.getBody().getLinearVelocity().x;
					}
				}
				
				@Override
				public void push(float impulseX, float impulseY) {
					hbox.getBody().applyLinearImpulse(new Vector2(impulseX, impulseY).scl(0.2f), hbox.getBody().getWorldCenter(), true);
				}
				
				@Override
				public void die() {
					hbox.queueDeletion();
				}
			});
			new ParticleEntity(state, hbox, Particle.BUBBLE_TRAIL, 3.0f, 0.0f, true, particleSyncType.CREATESYNC);
			hbox.setFriction(0);	
		}
	};
	
	public Iceberg(Schmuck user) {
		super(user, name, clipSize, reloadTime, recoil, projectileSpeed, shootCd, shootDelay, reloadAmount, true, onShoot, weaponSprite, eventSprite);
	}
}
