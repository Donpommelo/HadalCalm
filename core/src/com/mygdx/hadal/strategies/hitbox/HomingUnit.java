package com.mygdx.hadal.strategies.hitbox;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.QueryCallback;
import com.badlogic.gdx.physics.box2d.RayCastCallback;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.strategies.HitboxStrategy;
import com.mygdx.hadal.utils.Constants;

/**
 * This strategy makes a hbox home in on enemies
 * @author Zachary Tu
 */
public class HomingUnit extends HitboxStrategy {
	
	//this is the schmuck we are homing towards
	private Schmuck homing;
	
	//these variables are used in raycasting to find a homing target.
	private Schmuck homeAttempt;
	private Fixture closestFixture;
	private float shortestFraction = 1.0f;
	
	//this is the faction filter that describes which units this should home towards.
	private short filter;
	
	//this is the power of the force applied to the hbox when it tries to home.
	private float homePower;
	
	//this is the amount of seconds the hbox will attempt to predict its target's position
	private float maxPredictionTime = 0.5f;
	
	//this is the distance that the hbox will search for a homing target.
	private static final int homeRadius = 2000;
	
	private final static float pushInterval = 1 / 60f;
	private float controllerCount = 0;
	
	public HomingUnit(PlayState state, Hitbox proj, BodyData user, float homePower, short filter) {
		super(state, proj, user);
		this.homePower = homePower;
		this.filter = filter;
	}
	
	@Override
	public void controller(float delta) {
		
		//if we have a target, home towards it. Otherwise search for nearby targets
		if (homing != null && homing.isAlive()) {
			controllerCount += delta;

			while (controllerCount >= pushInterval) {
				controllerCount -= pushInterval;
				homing();
			}
		} else {
			
			//search all nearby enemies and raycast to them to see if there is an unobstructed path.
			hbox.getWorld().QueryAABB(new QueryCallback() {

				@Override
				public boolean reportFixture(Fixture fixture) {
					if (fixture.getUserData() instanceof BodyData) {
						
						homeAttempt = ((BodyData) fixture.getUserData()).getSchmuck();
						shortestFraction = 1.0f;
						
					  	if (hbox.getPosition().x != homeAttempt.getPosition().x || hbox.getPosition().y != homeAttempt.getPosition().y) {
					  		hbox.getWorld().rayCast(new RayCastCallback() {

								@Override
								public float reportRayFixture(Fixture fixture, Vector2 point, Vector2 normal, float fraction) {
									if (fixture.getFilterData().categoryBits == Constants.BIT_WALL) {
										if (fraction < shortestFraction) {
											shortestFraction = fraction;
											closestFixture = fixture;
											return fraction;
										}
									} else if (fixture.getUserData() instanceof BodyData) {
										if (((BodyData) fixture.getUserData()).getSchmuck().getHitboxfilter() != filter) {
											if (fraction < shortestFraction) {
												shortestFraction = fraction;
												closestFixture = fixture;
												return fraction;
											}
										}
									} 
									return -1.0f;
								}
								
							}, hbox.getPosition(), homeAttempt.getPosition());	
							
							if (closestFixture != null) {
								if (closestFixture.getUserData() instanceof BodyData) {
									homing = ((BodyData) closestFixture.getUserData()).getSchmuck();
								}
							}	
						}									
					}
					return true;
				}
			}, 
			hbox.getPosition().x - homeRadius, hbox.getPosition().y - homeRadius, 
			hbox.getPosition().x + homeRadius, hbox.getPosition().y + homeRadius);
		}
	}
	
	private Vector2 homingPush = new Vector2();
	/**
	 * This method pushes a hbox towards its homing target
	 */
	private void homing() {
		
		float squareDistance = homingPush.set(homing.getPosition()).sub(hbox.getPosition()).len2();
		float squareSpeed = hbox.getLinearVelocity().len2();

		//if this hbox is moving , we check its distance to its target
		if (squareSpeed > 0) {

			float squarePredictionTime = squareDistance / squareSpeed;
			
			//if the hbox is close enough to its target, we push it towards its target, accounting for its target's movement
			if (squarePredictionTime < maxPredictionTime * maxPredictionTime) {
				homingPush.set(homing.getPosition()).mulAdd(homing.getLinearVelocity(), (float) Math.sqrt(squarePredictionTime))
				.sub(hbox.getPosition()).nor().scl(homePower * hbox.getMass());
			} else {
				
				//at further distance, we also take into account the hbox's movement to compensate for its current velocity.
				homingPush.set(homing.getPosition()).mulAdd(homing.getLinearVelocity(), maxPredictionTime)
				.sub(hbox.getPosition().mulAdd(hbox.getLinearVelocity(), maxPredictionTime)).nor().scl(homePower * hbox.getMass());
			}
		} else {
			homingPush.set(homing.getPosition()).mulAdd(homing.getLinearVelocity(), maxPredictionTime)
			.sub(hbox.getPosition()).nor().scl(homePower * hbox.getMass());
		}
		hbox.applyForceToCenter(homingPush);
	}
}
