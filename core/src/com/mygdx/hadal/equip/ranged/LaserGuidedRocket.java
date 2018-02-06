package com.mygdx.hadal.equip.ranged;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.equip.RangedWeapon;
import com.mygdx.hadal.equip.WeaponUtils;
import com.mygdx.hadal.managers.AssetList;
import com.mygdx.hadal.schmucks.UserDataTypes;
import com.mygdx.hadal.schmucks.bodies.ParticleEntity;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.schmucks.bodies.hitboxes.HitboxImage;
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
	private final static float recoil = 0.5f;
	private final static float knockback = 0.0f;
	private final static float projectileSpeed = 0.0f;
	private final static float projectileSpeed2 = 20.0f;
	private final static int projectileWidth = 100;
	private final static int projectileHeight = 20;
	private final static float lifespan = 12.0f;
	private final static float gravity = 0;
	
	private final static int projDura = 1;
		
	private final static int explosionRadius = 300;
	private final static float explosionDamage = 60.0f;
	private final static float explosionKnockback = 10.0f;

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
			
			HitboxImage proj = new HitboxImage(state, x, y, projectileWidth, projectileHeight, gravity, lifespan, projDura, 0, startVelocity,
					filter, false, world, camera, rays, user, projSpriteId) {
				
				float controllerCount = 0;
				
				@Override
				public void controller(float delta) {
					controllerCount+=delta;
					if (controllerCount >= 1/60f) {
						if (lifeSpan <= 0) {
							WeaponUtils.explode(state, this.body.getPosition().x * PPM , this.body.getPosition().y * PPM, 
									world2, camera2, rays2, user, explosionRadius, explosionDamage, explosionKnockback, (short) 0);
						}
						
						Vector3 bodyPosition = new Vector3(body.getPosition().x, body.getPosition().y, 0);
						camera.project(bodyPosition);
						
						Vector2 diff = new Vector2(Gdx.input.getX() - bodyPosition.x, 
								Gdx.graphics.getHeight() - Gdx.input.getY() - bodyPosition.y);
						
						body.applyForceToCenter(diff.nor().scl(projectileSpeed2 * body.getMass()), true);
					}
					super.controller(delta);
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
