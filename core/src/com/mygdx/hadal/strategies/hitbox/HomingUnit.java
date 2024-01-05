package com.mygdx.hadal.strategies.hitbox;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.mygdx.hadal.constants.BodyConstants;
import com.mygdx.hadal.schmucks.entities.HadalEntity;
import com.mygdx.hadal.schmucks.entities.Schmuck;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.battle.DamageTag;
import com.mygdx.hadal.strategies.HitboxStrategy;
import com.mygdx.hadal.constants.Constants;
import com.mygdx.hadal.utils.WorldUtil;

import java.util.Arrays;

/**
 * This strategy makes a hbox home in on enemies
 * @author Gogarth Gnornelius
 */
public class HomingUnit extends HitboxStrategy {

	//this is the schmuck we are homing towards
	private Schmuck homing;
	
	//these variables are used in raycasting to find a homing target.
	private Schmuck homeAttempt;
	private Fixture closestFixture;
	private float shortestFraction = 1.0f;

	//when airblasted, should this hbox be disrupted and look for another homing target?
	private boolean disruptable;

	//time delay before the homing kicks into effect
	private float delay;

	//this is the power of the force applied to the hbox when it tries to home.
	private final float homePower;

	//this is the distance that the hbox will search for a homing target.
	private final int homeRadius;
	
	private float controllerCount = 0;

	//this makes the hbox fixed to the user until it finds a homing target (used for vengeful spirits)
	private boolean fixedUntilHome;
	private HadalEntity target;

	public HomingUnit(PlayState state, Hitbox proj, BodyData user, float homePower, int homeRadius) {
		super(state, proj, user);
		this.homePower = homePower;
		this.homeRadius = homeRadius;

		hbox.setSynced(true);
		hbox.setSyncedDelete(true);
	}

	private final Vector2 center = new Vector2();
	private final Vector2 hbLocation = new Vector2();
	@Override
	public void create() {
		if (fixedUntilHome) {
			if (target != null) {
				if (target.isAlive()) {
					center.set(target.getPosition()).sub(hbox.getPosition());
				}
			}
		}
	}
	
	private final Vector2 entityLocation = new Vector2();
	private final Vector2 homeLocation = new Vector2();
	@Override
	public void controller(float delta) {

		if (delay > 0) {
			delay -= delta;
			return;
		}

		//if we have a target, home towards it. Otherwise search for nearby targets
		if (homing != null && homing.isAlive()) {
			controllerCount += delta;

			while (controllerCount >= Constants.INTERVAL) {
				controllerCount -= Constants.INTERVAL;
				homing();
			}
		} else {
			entityLocation.set(hbox.getPosition());
			//search all nearby enemies and raycast to them to see if there is an unobstructed path.
			hbox.getWorld().QueryAABB(fixture -> {
				if (fixture.getUserData() instanceof BodyData bodyData) {

					homeAttempt = bodyData.getSchmuck();
					homeLocation.set(homeAttempt.getPosition());
					shortestFraction = 1.0f;

					if (WorldUtil.preRaycastCheck(entityLocation, homeLocation)) {
						  hbox.getWorld().rayCast((fixture1, point, normal, fraction) -> {
							  if (fixture1.getFilterData().categoryBits == BodyConstants.BIT_WALL) {
								  if (fraction < shortestFraction) {
									  shortestFraction = fraction;
									  closestFixture = fixture1;
									  return fraction;
								  }
							  } else if (fixture1.getUserData() instanceof BodyData targetData) {
								  if (targetData.getSchmuck().getHitboxFilter() != hbox.getFilter()) {
									  if (fraction < shortestFraction) {
										  shortestFraction = fraction;
										  closestFixture = fixture1;
										  return fraction;
									  }
								  }
							  }
							  return -1.0f;
						  }, entityLocation, homeLocation);

						if (closestFixture != null) {
							if (closestFixture.getUserData() instanceof BodyData homingData) {
								homing = homingData.getSchmuck();

								//fixed hboxes arae permantently unfixed upon finding a homing target
								fixedUntilHome = false;
							}
						}
					}
				}
				return true;
			}, entityLocation.x - homeRadius, entityLocation.y - homeRadius,
			entityLocation.x + homeRadius, entityLocation.y + homeRadius);

			//fixed until home makes the hbox follow the target (not home target) until it finds a homing target.
			if (fixedUntilHome) {
				if (target != null) {
					if (target.isAlive()) {
						hbLocation.set(target.getPosition()).add(center);
						hbox.setTransform(hbLocation, hbox.getAngle());
					}
				}
			}
		}
	}

	private static final float disruptDelay = 1.0f;
	@Override
	public void receiveDamage(BodyData perp, float baseDamage, Vector2 knockback, DamageTag... tags) {
		if (Arrays.asList(tags).contains(DamageTag.REFLECT) && disruptable) {
			delay = disruptDelay;
			homing = null;
		}
	}

	private final Vector2 homingPush = new Vector2();
	/**
	 * This method pushes a hbox towards its homing target
	 */
	private void homing() {
		entityLocation.set(hbox.getPosition());
		homeLocation.set(homing.getPosition());
		
		float squareDistance = homingPush.set(homeLocation).sub(entityLocation).len2();
		float squareSpeed = hbox.getLinearVelocity().len2();

		//if this hbox is moving , we check its distance to its target
		//this is the amount of seconds the hbox will attempt to predict its target's position
		float maxPredictionTime = 0.5f;
		if (squareSpeed > 0) {

			float squarePredictionTime = squareDistance / squareSpeed;
			
			//if the hbox is close enough to its target, we push it towards its target, accounting for its target's movement
			if (squarePredictionTime < maxPredictionTime * maxPredictionTime) {
				homingPush.set(homeLocation).mulAdd(homing.getLinearVelocity(), (float) Math.sqrt(squarePredictionTime))
				.sub(entityLocation).nor().scl(homePower * hbox.getMass());
			} else {
				
				//at further distance, we also take into account the hbox's movement to compensate for its current velocity.
				homingPush.set(homeLocation).mulAdd(homing.getLinearVelocity(), maxPredictionTime)
				.sub(entityLocation.mulAdd(hbox.getLinearVelocity(), maxPredictionTime)).nor().scl(homePower * hbox.getMass());
			}
		} else {
			homingPush.set(homeLocation).mulAdd(homing.getLinearVelocity(), maxPredictionTime)
			.sub(entityLocation).nor().scl(homePower * hbox.getMass());
		}
		hbox.applyForceToCenter(homingPush);
	}

	public HomingUnit setDisruptable(boolean disruptable) {
		this.disruptable = disruptable;
		return this;
	}

	public HomingUnit setFixedUntilHome(boolean fixedUntilHome) {
		this.fixedUntilHome = fixedUntilHome;
		return this;
	}

	public HomingUnit setTarget(HadalEntity target) {
		this.target = target;
		return this;
	}
}
