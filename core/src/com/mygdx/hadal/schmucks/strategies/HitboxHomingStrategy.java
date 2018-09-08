package com.mygdx.hadal.schmucks.strategies;

import com.badlogic.gdx.ai.steer.SteeringAcceleration;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.QueryCallback;
import com.badlogic.gdx.physics.box2d.RayCastCallback;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;

public class HitboxHomingStrategy extends HitboxStrategy{
	
	private Schmuck homing;
	private Schmuck homeAttempt;
	private Fixture closestFixture;
	
	private float shortestFraction = 1.0f;
	
	private float radius;
	private short filter;
	
	private static final float maxLinSpd = 150;
	private static final float maxLinAcc = 1000;
	private static final float maxAngSpd = 1080;
	private static final float maxAngAcc = 2160;
	
	private static final int boundingRad = 100;
	private static final int decelerationRadius = 0;
	private static final int homeRadius = 2000;
	
	public HitboxHomingStrategy(PlayState state, Hitbox proj, BodyData user, float maxLinSpd, float maxLinAcc, float maxAngSpd,
			float maxAngAcc, int boundingRad, int decelerationRadius, float radius, short filter) {
		super(state, proj, user);
		this.radius = radius;
		this.filter = filter;
		
		hbox.setMaxLinearSpeed(maxLinSpd);
		hbox.setMaxLinearAcceleration(maxLinAcc);
		hbox.setMaxAngularSpeed(maxAngSpd);
		hbox.setMaxAngularAcceleration(maxAngAcc);
		
		hbox.setBoundingRadius(boundingRad);
		hbox.setDecelerationRad(decelerationRadius);
		
		hbox.setTagged(false);
		hbox.setSteeringOutput(new SteeringAcceleration<Vector2>(new Vector2()));
	}
	
	public HitboxHomingStrategy(PlayState state, Hitbox proj, BodyData user, short filter) {
		this(state, proj, user, maxLinSpd, maxLinAcc, maxAngSpd, maxAngAcc, boundingRad, decelerationRadius, homeRadius, filter);
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
						
					  	if (hbox.getBody().getPosition().x != homeAttempt.getPosition().x || 
					  			hbox.getBody().getPosition().y != homeAttempt.getPosition().y) {
					  		hbox.getWorld().rayCast(new RayCastCallback() {

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
								
							}, hbox.getBody().getPosition(), homeAttempt.getPosition());	
							
							if (closestFixture != null) {
								if (closestFixture.getUserData() instanceof BodyData) {
									homing = ((BodyData)closestFixture.getUserData()).getSchmuck();
									hbox.setTarget(homing);
								}
							}	
						}									
					}
					return true;
				}
				
			}, 
			hbox.getBody().getPosition().x - radius, hbox.getBody().getPosition().y - radius, 
			hbox.getBody().getPosition().x + radius, hbox.getBody().getPosition().y + radius);
		}
	}	
}
