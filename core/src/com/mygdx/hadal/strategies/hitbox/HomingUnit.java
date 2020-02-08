package com.mygdx.hadal.strategies.hitbox;

import com.badlogic.gdx.ai.steer.SteeringAcceleration;
import com.badlogic.gdx.ai.steer.behaviors.Pursue;
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
 *
 */
public class HomingUnit extends HitboxStrategy{
	
	private Schmuck homing;
	private Schmuck homeAttempt;
	private Fixture closestFixture;
	
	private float shortestFraction = 1.0f;
	
	private float radius;
	private short filter;
	
	private static final float maxLinSpd = 8000;
	private static final float maxLinAcc = 8000;
	
	private static final int homeRadius = 2000;
	
	public HomingUnit(PlayState state, Hitbox proj, BodyData user, float maxLinSpd, float maxLinAcc, float radius, short filter) {
		super(state, proj, user);
		this.radius = radius;
		this.filter = filter;
		
		hbox.setMaxLinearSpeed(maxLinSpd);
		hbox.setMaxLinearAcceleration(maxLinAcc);
		
		hbox.setTagged(false);
		hbox.setSteeringOutput(new SteeringAcceleration<Vector2>(new Vector2()));
	}
	
	public HomingUnit(PlayState state, Hitbox proj, BodyData user, short filter) {
		this(state, proj, user, maxLinSpd, maxLinAcc, homeRadius, filter);
	}
	
	@Override
	public void controller(float delta) {		
		if (homing != null && homing.isAlive()) {
			if (hbox.getBehavior() != null) {
				hbox.getBehavior().calculateSteering(hbox.getSteeringOutput());
				hbox.applySteering(delta);
			}
		} else {
			hbox.getWorld().QueryAABB(new QueryCallback() {

				@Override
				public boolean reportFixture(Fixture fixture) {
					if (fixture.getUserData() instanceof BodyData) {
						
						homeAttempt = ((BodyData)fixture.getUserData()).getSchmuck();
						shortestFraction = 1.0f;
						
					  	if (hbox.getPosition().x != homeAttempt.getPosition().x || 
					  			hbox.getPosition().y != homeAttempt.getPosition().y) {
					  		hbox.getWorld().rayCast(new RayCastCallback() {

								@Override
								public float reportRayFixture(Fixture fixture, Vector2 point, Vector2 normal, float fraction) {
									if (fixture.getFilterData().categoryBits == (short)Constants.BIT_WALL) {
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
								
							}, hbox.getPosition(), homeAttempt.getPosition());	
							
							if (closestFixture != null) {
								if (closestFixture.getUserData() instanceof BodyData) {
									
									homing = ((BodyData)closestFixture.getUserData()).getSchmuck();
									Pursue<Vector2> seek = new Pursue<Vector2>(hbox, homing);
									hbox.setBehavior(seek);
								}
							}	
						}									
					}
					return true;
				}
				
			}, 
			hbox.getPosition().x - radius, hbox.getPosition().y - radius, 
			hbox.getPosition().x + radius, hbox.getPosition().y + radius);
		}
	}	
}
