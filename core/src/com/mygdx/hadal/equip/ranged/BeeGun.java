package com.mygdx.hadal.equip.ranged;

import com.badlogic.gdx.ai.steer.SteeringAcceleration;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.QueryCallback;
import com.badlogic.gdx.physics.box2d.RayCastCallback;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.hadal.equip.RangedWeapon;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.schmucks.bodies.hitboxes.HitboxAnimated;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.schmucks.userdata.HadalData;
import com.mygdx.hadal.schmucks.userdata.HitboxData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;
import com.mygdx.hadal.utils.HitboxFactory;

import box2dLight.RayHandler;

import static com.mygdx.hadal.utils.Constants.PPM;

import java.util.concurrent.ThreadLocalRandom;

public class BeeGun extends RangedWeapon {

	private final static String name = "Bee Gun";
	private final static int clipSize = 24;
	private final static float shootCd = 0.15f;
	private final static float shootDelay = 0;
	private final static float reloadTime = 1.75f;
	private final static int reloadAmount = 0;
	private final static float baseDamage = 12.0f;
	private final static float recoil = 0.0f;
	private final static float knockback = 2.5f;
	private final static float projectileSpeedStart = 3.0f;
	private final static int projectileWidth = 23;
	private final static int projectileHeight = 21;
	private final static float lifespan = 5.0f;
	private final static float gravity = 0;
	private final static float homeRadius = 10;
	
	private final static int projDura = 1;
	
	private final static int spread = 45;
	
	private final static String weapSpriteId = "beegun";
	private final static String projSpriteId = "bee";

	private static final float maxLinSpd = 100;
	private static final float maxLinAcc = 1000;
	private static final float maxAngSpd = 180;
	private static final float maxAngAcc = 90;
	
	private static final int boundingRad = 500;
	private static final int decelerationRadius = 0;

	private final static HitboxFactory onShoot = new HitboxFactory() {

		@Override
		public void makeHitbox(final Schmuck user, PlayState state, Vector2 startVelocity, float x, float y, short filter,
				World world, OrthographicCamera camera,
				RayHandler rays) {
			
			float newDegrees = (float) (startVelocity.angle() + (ThreadLocalRandom.current().nextInt(-spread, spread + 1)));
			
			final HitboxAnimated proj = new HitboxAnimated(state, x, y, projectileWidth, projectileHeight, gravity, lifespan, projDura, 0, startVelocity.setAngle(newDegrees),
					filter, false, world, camera, rays, user, projSpriteId) {
				
				private Schmuck homing;
				private Schmuck homeAttempt;
				private Fixture closestFixture;
				
				private float shortestFraction = 1.0f;
			  	
				{
					this.maxLinearSpeed = maxLinSpd;
					this.maxLinearAcceleration = maxLinAcc;
					this.maxAngularSpeed = maxAngSpd;
					this.maxAngularAcceleration = maxAngAcc;
					
					this.boundingRadius = boundingRad;
					this.decelerationRad = decelerationRadius;
					
					this.tagged = false;
					this.steeringOutput = new SteeringAcceleration<Vector2>(new Vector2());
				}
				
				@Override
				public void controller(float delta) {
					super.controller(delta);
					
					if (homing != null && homing.isAlive()) {
						if (behavior != null) {
							behavior.calculateSteering(steeringOutput);
							applySteering(delta);
						}
					} else {
						world.QueryAABB(new QueryCallback() {

							@Override
							public boolean reportFixture(Fixture fixture) {
								if (fixture.getUserData() instanceof BodyData) {
									
									homeAttempt = ((BodyData)fixture.getUserData()).getSchmuck();
									shortestFraction = 1.0f;
									
								  	if (body.getPosition().x != homeAttempt.getPosition().x || 
								  			body.getPosition().y != homeAttempt.getPosition().y) {
										world.rayCast(new RayCastCallback() {

											@Override
											public float reportRayFixture(Fixture fixture, Vector2 point, Vector2 normal, float fraction) {
												if (fixture.getUserData() == null) {
													if (fraction < shortestFraction) {
														shortestFraction = fraction;
														closestFixture = fixture;
														return fraction;
													}
												} else if (fixture.getUserData() instanceof BodyData) {
													if (((BodyData)fixture.getUserData()).getSchmuck().getHitboxfilter() != filter) {
														if (fraction < shortestFraction) {
															shortestFraction = fraction;
															closestFixture = fixture;
															return fraction;
														}
													}
												} 
												return -1.0f;
											}
											
										}, getBody().getPosition(), homeAttempt.getPosition());	
										
										if (closestFixture != null) {
											if (closestFixture.getUserData() instanceof BodyData) {
												homing = ((BodyData)closestFixture.getUserData()).getSchmuck();
												setTarget(homing);
											}
										}	
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

					batch.draw((TextureRegion) projectileSprite.getKeyFrame(animationTime, true), 
							body.getPosition().x * PPM - width / 2, 
							(flip ? height : 0) + body.getPosition().y * PPM - height / 2, 
							width / 2, 
							(flip ? -1 : 1) * height / 2,
							width, (flip ? -1 : 1) * height, 1, 1, 
							(float) Math.toDegrees(body.getAngle()) - 90);

				}
			};
			
			proj.setUserData(new HitboxData(state, world, proj) {
				
				@Override
				public void onHit(HadalData fixB) {
					if (fixB != null) {
						fixB.receiveDamage(baseDamage, hbox.getBody().getLinearVelocity().nor().scl(knockback), 
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
