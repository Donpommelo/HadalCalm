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
import com.mygdx.hadal.schmucks.strategies.HitboxDefaultStrategy;
import com.mygdx.hadal.schmucks.strategies.HitboxOnContactUnitLoseDuraStrategy;
import com.mygdx.hadal.schmucks.strategies.HitboxOnContactWallDieStrategy;
import com.mygdx.hadal.schmucks.strategies.HitboxStrategy;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;

public class TrickGun extends RangedWeapon {

	private final static String name = "Trick Gun";
	private final static int clipSize = 5;
	private final static int ammoSize = 28;
	private final static float shootCd = 0.0f;
	private final static float shootDelay = 0.0f;
	private final static float reloadTime = 0.75f;
	private final static int reloadAmount = 0;
	private final static float baseDamage = 55.0f;
	private final static float recoil = 16.0f;
	private final static float knockback = 20.0f;
	private final static float projectileSpeed = 25.0f;
	private final static int projectileWidth = 45;
	private final static int projectileHeight = 45;
	private final static float lifespan = 1.5f;
	private final static float gravity = 0;
	
	private final static int projDura = 1;
	
	private final static Sprite weaponSprite = Sprite.MT_DEFAULT;
	private final static Sprite eventSprite = Sprite.P_DEFAULT;
	
	private final static float projectileSpeedAfter = 55.0f;

	private boolean firstClicked = false;
	private Vector2 pos1 = new Vector2();
	private Vector2 pos2 = new Vector2();
	private Vector2 vel1 = new Vector2();
	private Vector2 vel2 = new Vector2();
	
	public TrickGun(Schmuck user) {
		super(user, name, clipSize, ammoSize, reloadTime, recoil, projectileSpeed, shootCd, shootDelay, reloadAmount, true, weaponSprite, eventSprite, projectileWidth);
	}
	
	@Override
	public void mouseClicked(float delta, PlayState state, BodyData shooter, short faction, int x, int y) {
		super.mouseClicked(delta, state, shooter, faction, x, y);
		if (!firstClicked) {
			pos1.set(x, y);
			firstClicked = true;
		}
	}
	
	@Override
	public void execute(PlayState state, BodyData shooter) {}
	
	@Override
	public void release(PlayState state, BodyData bodyData) {
		if (firstClicked) {
			pos2.set(x, y);
			
			float powerDiv = pos1.dst(pos2) / projectileSpeed;
			
			float xImpulse = -(pos1.x - pos2.x) / powerDiv;
			float yImpulse = -(pos1.y - pos2.y) / powerDiv;
			vel2.set(xImpulse, yImpulse);
			
			powerDiv = user.getPosition().dst(pos1.x, pos1.y) / projectileSpeed;
			
			xImpulse = -(user.getPosition().x - pos1.x) / powerDiv;
			yImpulse = -(user.getPosition().y - pos1.y) / powerDiv;
			vel1.set(xImpulse, yImpulse);
			
			this.setWeaponVelo(vel1);
			
			super.execute(state, bodyData);			
			
			firstClicked = false;
		}
	}
	
	@Override
	public void fire(PlayState state, final Schmuck user, Vector2 startVelocity, float x, float y, final short filter) {
		
		Hitbox hbox = new RangedHitbox(state, x, y, projectileWidth, projectileHeight, gravity, lifespan, projDura, 0, startVelocity, filter, true, true, user);
		
		hbox.addStrategy(new HitboxDefaultStrategy(state, hbox, user.getBodyData()));
		hbox.addStrategy(new HitboxOnContactUnitLoseDuraStrategy(state, hbox, user.getBodyData()));
		hbox.addStrategy(new HitboxOnContactWallDieStrategy(state, hbox, user.getBodyData()));
		hbox.addStrategy(new HitboxDamageStandardStrategy(state, hbox, user.getBodyData(), this, baseDamage, knockback, DamageTypes.RANGED));
		new ParticleEntity(state, hbox, Particle.LASER_PULSE, 2.0f, 0.0f, true, particleSyncType.TICKSYNC);
		
		//This extra check of firstClicked makes sure effects that autofire this gun work (like muddling cup)
		if (firstClicked) {

			hbox.addStrategy(new HitboxStrategy(state, hbox, user.getBodyData()) {
				
				private boolean firstReached = false;
				private Vector2 startLocation = new Vector2();
				private float distance;
				private Vector2 target = new Vector2();
				
				@Override
				public void create() {
					this.startLocation.set(hbox.getPosition());
					this.distance = startLocation.dst(pos1);
				}
				
				@Override
				public void controller(float delta) {
					if (!firstReached) {
						if (startLocation.dst(hbox.getPosition()) >= distance) {
							if (!pos2.equals(pos1)) {
								target.set(pos2).sub(hbox.getPosition());
								hbox.setLinearVelocity(target.nor().scl(projectileSpeedAfter));
							}
							firstReached = true;
						}
					}
				}
			});
		}
	}
}
