package com.mygdx.hadal.strategies.hitbox;

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
import com.mygdx.hadal.strategies.HitboxStrategy;
import com.mygdx.hadal.utils.Constants;

/**
 * This strategy makes a hbox that makes contact with an enemy chain to other nearby targets.
 * Chaining makes a projectile move quickly towards another target. This is a change in velocity and can still miss the chained target.
 * @author Zachary Tu
 *
 */
public class ContactChain extends HitboxStrategy {
	
	//the amout of times this hbox can chain.
	private int chains;
	
	//the hbox filter of units that this hbox can hit
	private short filter;
	
	//The schmuck that the hbox attempts to chain to
	private Schmuck chainAttempt;
	
	//variables used for raycasting
	private Fixture closestFixture;
	private float shortestFraction = 1.0f;

	//chain radius
	private static final int radius = 750;

	public ContactChain(PlayState state, Hitbox proj, BodyData user, int chain, short filter) {
		super(state, proj, user);
		this.filter = filter;
		this.chains = chain;
	}
	
	@Override
	public void create() {
		chain(null);
	}
	
	@Override
	public void onHit(final HadalData fixB) {
		if (fixB != null) {
			if (fixB.getType().equals(UserDataTypes.BODY)) {
				chain(fixB);
			}
		}
	}
	
	private void chain(final HadalData fixB) {
		//if we are out of chains, die.
		if (chains <= 0) {
			hbox.die();
		}
		chains--;
		
		//search world for available targets to chain to
		hbox.getWorld().QueryAABB(new QueryCallback() {

			@Override
			public boolean reportFixture(Fixture fixture) {
				if (fixture.getUserData() instanceof BodyData) {
					
					chainAttempt = ((BodyData)fixture.getUserData()).getSchmuck();
					shortestFraction = 1.0f;
					
				  	if (hbox.getPosition().x != chainAttempt.getPosition().x || hbox.getPosition().y != chainAttempt.getPosition().y) {
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
									if (((BodyData)fixture.getUserData()).getSchmuck().getHitboxfilter() != filter && fixB != fixture.getUserData()) {
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
						
				  		//if we find a suitable chain target, set our velocity to make us move towards them.
						if (closestFixture != null) {
							if (closestFixture.getUserData() instanceof BodyData) {
								hbox.setLinearVelocity(closestFixture.getBody().getPosition().sub(hbox.getPosition()).nor().scl(60));
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
