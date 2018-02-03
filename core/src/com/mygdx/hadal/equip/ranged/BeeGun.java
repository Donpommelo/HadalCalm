package com.mygdx.hadal.equip.ranged;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.QueryCallback;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.hadal.equip.RangedWeapon;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.schmucks.bodies.hitboxes.SteeringHitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.schmucks.userdata.HadalData;
import com.mygdx.hadal.schmucks.userdata.HitboxData;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;
import com.mygdx.hadal.utils.HitboxFactory;

import box2dLight.RayHandler;

import static com.mygdx.hadal.utils.Constants.PPM;

import java.util.concurrent.ThreadLocalRandom;

public class BeeGun extends RangedWeapon {

	private final static String name = "Bee Gun";
	private final static int clipSize = 18;
	private final static float shootCd = 0.15f;
	private final static float shootDelay = 0;
	private final static float reloadTime = 1.75f;
	private final static int reloadAmount = 0;
	private final static float baseDamage = 9.0f;
	private final static float recoil = 0.0f;
	private final static float knockback = 0.2f;
	private final static float projectileSpeedStart = 3.0f;
	private final static int projectileWidth = 15;
	private final static int projectileHeight = 14;
	private final static float lifespan = 5.0f;
	private final static float gravity = 0;
	private final static float homeRadius = 10;
	
	private final static int projDura = 1;
	
	private final static int spread = 45;
	
	private final static String weapSpriteId = "beegun";
	private final static String projSpriteId = "bee";

	private static final float maxLinearSpeed = 100;
	private static final float maxLinearAcceleration = 500;
	private static final float maxAngularSpeed = 30;
	private static final float maxAngularAcceleration = 5;
	
	private static final int boundingRadius = 500;
	private static final int decelerationRadius = 0;

	private final static HitboxFactory onShoot = new HitboxFactory() {

		@Override
		public void makeHitbox(final Schmuck user, PlayState state, Vector2 startVelocity, float x, float y, short filter,
				World world, OrthographicCamera camera,
				RayHandler rays) {
			
			float newDegrees = (float) (startVelocity.angle() + (ThreadLocalRandom.current().nextInt(-spread, spread + 1)));
			
			final SteeringHitbox proj = new SteeringHitbox(state, x, y, projectileWidth, projectileHeight, gravity, lifespan, projDura, 0, startVelocity.setAngle(newDegrees),
					filter, false, world, camera, rays, user, projSpriteId,
					maxLinearSpeed, maxLinearAcceleration, maxAngularSpeed, maxAngularAcceleration, boundingRadius, decelerationRadius) {
				
				private Schmuck homing;

				public void controller(float delta) {
					super.controller(delta);
					
					if (homing != null && homing.alive) {
						if (behavior != null) {
							behavior.calculateSteering(steeringOutput);
							applySteering(delta);
						}
					} else {
						world.QueryAABB(new QueryCallback() {

							@Override
							public boolean reportFixture(Fixture fixture) {
								if (fixture.getUserData() instanceof BodyData) {
									if (!(fixture.getUserData() instanceof PlayerBodyData)) {
										homing = ((BodyData)fixture.getUserData()).getSchmuck();
										setTarget(homing);
									}
								}
								return true;
							}
							
						}, 
						body.getPosition().x - homeRadius, body.getPosition().y - homeRadius, 
						body.getPosition().x + homeRadius, body.getPosition().y + homeRadius);
					}
				}
				
				@Override
				public void render(SpriteBatch batch) {
				
					boolean flip = false;
					
					if (body.getAngle() < 0) {
						flip = true;
					}
					
					batch.setProjectionMatrix(state.sprite.combined);

					batch.draw((TextureRegion) projectileSprite.getKeyFrame(animCdCount, true), 
							body.getPosition().x * PPM - width / 2, 
							(flip ? height : 0) + body.getPosition().y * PPM - height / 2, 
							width / 2, 
							(flip ? -1 : 1) * height / 2,
							width, (flip ? -1 : 1) * height, 1, 1, 
							(float) Math.toDegrees(body.getAngle()) - 90);

				}
			};
			
			proj.setUserData(new HitboxData(state, world, proj) {
				
				public void onHit(HadalData fixB) {
					if (fixB != null) {
						fixB.receiveDamage(baseDamage, this.hbox.getBody().getLinearVelocity().nor().scl(knockback), 
								user.getBodyData(), true, DamageTypes.RANGED);
						super.onHit(fixB);
					}
				}
			});		
		}
		
	};
	
	public BeeGun(Schmuck user) {
		super(user, name, clipSize, reloadTime, recoil, projectileSpeedStart, shootCd, shootDelay, reloadAmount, onShoot, weapSpriteId);
	}

}
