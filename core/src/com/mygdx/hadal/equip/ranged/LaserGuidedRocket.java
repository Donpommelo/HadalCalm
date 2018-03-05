package com.mygdx.hadal.equip.ranged;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.equip.RangedWeapon;
import com.mygdx.hadal.equip.WeaponUtils;
import com.mygdx.hadal.managers.AssetList;
import com.mygdx.hadal.schmucks.UserDataTypes;
import com.mygdx.hadal.schmucks.bodies.ParticleEntity;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.schmucks.bodies.hitboxes.SteeringHitbox;
import com.mygdx.hadal.schmucks.userdata.HadalData;
import com.mygdx.hadal.schmucks.userdata.HitboxData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;
import com.mygdx.hadal.utils.HitboxFactory;

import box2dLight.RayHandler;
import static com.mygdx.hadal.utils.Constants.PPM;

public class LaserGuidedRocket extends RangedWeapon {

	private final static String name = "Laser-Guided Rocket";
	private final static int clipSize = 1;
	private final static float shootCd = 0.0f;
	private final static float shootDelay = 0.0f;
	private final static float reloadTime = 1.5f;
	private final static int reloadAmount = 1;
	private final static float baseDamage = 8.0f;
	private final static float recoil = 2.5f;
	private final static float knockback = 0.0f;
	private final static float projectileSpeed = 0.0f;
	private final static int projectileWidth = 20;
	private final static int projectileHeight = 100;
	private final static float lifespan = 12.0f;
	private final static float gravity = 0;
	
	private final static int projDura = 1;
		
	private final static int explosionRadius = 300;
	private final static float explosionDamage = 60.0f;
	private final static float explosionKnockback = 25.0f;

	private static final float maxLinearSpeed = 100;
	private static final float maxLinearAcceleration = 500;
	private static final float maxAngularSpeed = 135;
	private static final float maxAngularAcceleration = 45;
	
	private static final int boundingRadius = 500;
	private static final int decelerationRadius = 0;
	
	private final static String weapSpriteId = "torpedolauncher";
	private final static String projSpriteId = "torpedo";
	
	// Particle effect information.
	 private static TextureAtlas particleAtlas;

	private final static HitboxFactory onShoot = new HitboxFactory() {

		@Override
		public void makeHitbox(final Schmuck user, PlayState state, Vector2 startVelocity, float x, float y, short filter,
				World world, OrthographicCamera camera,
				RayHandler rays) {
			
			final World world2 = world;
			final OrthographicCamera camera2 = camera;
			final RayHandler rays2 = rays;
			
			final SteeringHitbox proj = new SteeringHitbox(state, x, y, projectileWidth, projectileHeight, gravity, lifespan, projDura, 0, startVelocity,
					filter, true, world, camera, rays, user, projSpriteId,	maxLinearSpeed, maxLinearAcceleration,
					maxAngularSpeed, maxAngularAcceleration, boundingRadius, decelerationRadius) {
				
				float controllerCount = 0;
				{
					setTarget(state.getMouse());
				}
				@Override
				public void controller(float delta) {
					controllerCount+=delta;
					if (controllerCount >= 1/60f) {
						if (lifeSpan <= 0) {
							WeaponUtils.explode(state, this.body.getPosition().x * PPM , this.body.getPosition().y * PPM, 
									world2, camera2, rays2, user, explosionRadius, explosionDamage, explosionKnockback, (short) 0);
						}
						
						if (behavior != null) {
							behavior.calculateSteering(steeringOutput);
							applySteering(delta);
						}

					}
					super.controller(delta);
				}
				
				@Override
				public void render(SpriteBatch batch) {
				
					boolean flip = false;
					
					if (body.getAngle() < 0) {
						flip = true;
					}
					
					batch.setProjectionMatrix(state.sprite.combined);

					batch.draw((TextureRegion) projectileSprite.getKeyFrame(animCdCount, true), 
							body.getPosition().x * PPM - height / 2, 
							(flip ? width : 0) + body.getPosition().y * PPM - width / 2, 
							height / 2, 
							(flip ? -1 : 1) * width / 2,
							height, (flip ? -1 : 1) * width, 1, 1, 
							(float) Math.toDegrees(body.getAngle()) - 90);
				}
			};
			
			final ParticleEffect bubbles = new ParticleEffect();
			bubbles.load(Gdx.files.internal(AssetList.BUBBLE_TRAIL.toString()), particleAtlas);
			new ParticleEntity(state, world, camera, rays, proj, bubbles, 3.0f);			
			
			proj.setUserData(new HitboxData(state, world, proj) {
				
				@Override
				public void onHit(HadalData fixB) {
					boolean explode = false;
					if (fixB != null) {
						if (fixB.getType().equals(UserDataTypes.BODY) || fixB.getType().equals(UserDataTypes.WALL)) {
							explode = true;
						}
						fixB.receiveDamage(baseDamage, this.hbox.getBody().getLinearVelocity().nor().scl(knockback), 
								user.getBodyData(), true, DamageTypes.RANGED);
					} else {
						explode = true;
					}
					if (explode) {
						WeaponUtils.explode(state, this.hbox.getBody().getPosition().x * PPM , this.hbox.getBody().getPosition().y * PPM, 
								world2, camera2, rays2, user, explosionRadius, explosionDamage, explosionKnockback, (short)0);
						hbox.queueDeletion();
					}
					
				}
			});		
		}
	};
	
	public LaserGuidedRocket(Schmuck user) {
		super(user, name, clipSize, reloadTime, recoil, projectileSpeed, shootCd, shootDelay, reloadAmount, onShoot, weapSpriteId);
		particleAtlas = HadalGame.assetManager.get(AssetList.PARTICLE_ATLAS.toString());
	}
}
