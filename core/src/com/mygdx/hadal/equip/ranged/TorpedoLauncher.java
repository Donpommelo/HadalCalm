package com.mygdx.hadal.equip.ranged;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.equip.RangedWeapon;
import com.mygdx.hadal.managers.AssetList;
import com.mygdx.hadal.schmucks.UserDataTypes;
import com.mygdx.hadal.schmucks.bodies.Hitbox;
import com.mygdx.hadal.schmucks.bodies.HitboxImage;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.schmucks.userdata.HadalData;
import com.mygdx.hadal.schmucks.userdata.HitboxData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.utils.HitboxFactory;

import box2dLight.RayHandler;
import static com.mygdx.hadal.utils.Constants.PPM;

public class TorpedoLauncher extends RangedWeapon {

	private final static String name = "Torpedo Launcher";
	private final static int clipSize = 4;
	private final static float shootCd = 0.25f;
	private final static float shootDelay = 0.15f;
	private final static float reloadTime = 0.75f;
	private final static int reloadAmount = 1;
	private final static float baseDamage = 8.0f;
	private final static float recoil = 0.5f;
	private final static float knockback = 0.0f;
	private final static float projectileSpeed = 25.0f;
	private final static int projectileWidth = 75;
	private final static int projectileHeight = 15;
	private final static float lifespan = 3.0f;
	private final static float gravity = 0;
	
	private final static int projDura = 1;
		
	private final static int explosionRadius = 300;
	private final static float explosionDamage = 60.0f;
	private final static float explosionKnockback = 15.0f;

	private final static String spriteId = "torpedo";
	
	// Particle effect information.
	 private static TextureAtlas particleAtlas;

	private final static HitboxFactory onShoot = new HitboxFactory() {

		@Override
		public Hitbox makeHitbox(Schmuck user, PlayState state, Vector2 startVelocity, float x, float y, short filter,
				World world, OrthographicCamera camera,
				RayHandler rays) {
			
			final Schmuck user2 = user;
			final ParticleEffect bubbles = new ParticleEffect();

			HitboxImage proj = new HitboxImage(state, x, y, projectileWidth, projectileHeight, gravity, lifespan, projDura, projectileSpeed, startVelocity,
					filter, false, world, camera, rays, user, spriteId) {
				
				{
					bubbles.load(Gdx.files.internal(AssetList.BUBBLE_TRAIL.toString()), particleAtlas);
					bubbles.start();
				}
				
				@Override
				public void controller(float delta) {
					super.controller(delta);
					bubbles.setPosition(body.getPosition().x * PPM, body.getPosition().y * PPM);
				}
				
				@Override
				public void render(SpriteBatch batch) {
					batch.setProjectionMatrix(state.sprite.combined);
					bubbles.draw(batch, Gdx.graphics.getDeltaTime());
					super.render(batch);
				}
			};
			
			final World world2 = world;
			final OrthographicCamera camera2 = camera;
			final RayHandler rays2 = rays;
			
			proj.setUserData(new HitboxData(state, world, proj) {
				
				public void onHit(HadalData fixB) {
					boolean explode = false;
					if (fixB != null) {
						if (fixB.getType().equals(UserDataTypes.BODY)) {
							((BodyData) fixB).receiveDamage(baseDamage, this.hbox.getBody().getLinearVelocity().nor().scl(knockback));
							explode = true;
						}
					} else {
						explode = true;
					}
					if (explode) {
						explode(state, this.hbox.getBody().getPosition().x * PPM , this.hbox.getBody().getPosition().y * PPM, 
								world2, camera2, rays2, user2);
						hbox.queueDeletion();
					}
					
				}
			});		
			
			return null;
		}
		
		public void explode(PlayState state, float x, float y, World world, OrthographicCamera camera, RayHandler rays, 
				Schmuck user) {
			Hitbox explosion = new Hitbox(state, 
					x, y,	explosionRadius, explosionRadius, 0, .02f, 1, 0, new Vector2(0, 0),
					(short) 0, true, world, camera, rays, user);

				explosion.setUserData(new HitboxData(state, world, explosion){
					public void onHit(HadalData fixB) {
						if (fixB != null) {
							if (fixB.getType().equals(UserDataTypes.BODY)) {
								((BodyData) fixB).receiveDamage(explosionDamage, 
										new Vector2(fixB.getEntity().getBody().getPosition().x - this.hbox.getBody().getPosition().x, 
												fixB.getEntity().getBody().getPosition().y - this.hbox.getBody().getPosition().y).nor().scl(explosionKnockback));
									
							}
						}
					}
				});
		}
	};
	
	public TorpedoLauncher(Schmuck user) {
		super(user, name, clipSize, reloadTime, recoil, projectileSpeed, shootCd, shootDelay, reloadAmount, onShoot);
		particleAtlas = HadalGame.assetManager.get(AssetList.PARTICLE_ATLAS.toString());
	}
}
