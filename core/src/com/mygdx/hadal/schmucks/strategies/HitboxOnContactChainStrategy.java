package com.mygdx.hadal.schmucks.strategies;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.QueryCallback;
import com.badlogic.gdx.physics.box2d.RayCastCallback;
import com.mygdx.hadal.schmucks.UserDataTypes;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.schmucks.userdata.HadalData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.utils.Constants;

public class HitboxOnContactChainStrategy extends HitboxStrategy{
	
	private int chains;
	private short filter;
	
	private Schmuck chainAttempt;
	private Fixture closestFixture;
	private float shortestFraction = 1.0f;

	private static final int radius = 1000;

	public HitboxOnContactChainStrategy(PlayState state, Hitbox proj, BodyData user, int chain, short filter) {
		super(state, proj, user);
		this.filter = filter;
		this.chains = chain;

	}
	
	@Override
	public void onHit(final HadalData fixB) {
		if (fixB != null) {
			if (fixB.getType().equals(UserDataTypes.BODY)) {
				
				if (chains <= 0) {
					hbox.die();
				}
				chains--;
				hbox.getWorld().QueryAABB(new QueryCallback() {

					@Override
					public boolean reportFixture(Fixture fixture) {
						if (fixture.getUserData() instanceof BodyData) {
							
							chainAttempt = ((BodyData)fixture.getUserData()).getSchmuck();
							shortestFraction = 1.0f;
							
						  	if (hbox.getPosition().x != chainAttempt.getPosition().x || 
						  			hbox.getPosition().y != chainAttempt.getPosition().y) {
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
											if (((BodyData)fixture.getUserData()).getSchmuck().getHitboxfilter() != filter &&
													fixB != fixture.getUserData()) {
												if (fraction < shortestFraction) {
													shortestFraction = fraction;
													closestFixture = fixture;
													return fraction;
												}
											}
										} 
										return -1.0f;
									}
									
								}, hbox.getPosition(), chainAttempt.getPosition());	
								
								if (closestFixture != null) {
									if (closestFixture.getUserData() instanceof BodyData) {
										hbox.setLinearVelocity(closestFixture.getBody().getPosition()
												.sub(hbox.getPosition())
														.nor().scl(60));
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
}
