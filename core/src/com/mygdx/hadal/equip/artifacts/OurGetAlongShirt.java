package com.mygdx.hadal.equip.artifacts;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.QueryCallback;
import com.badlogic.gdx.physics.box2d.RayCastCallback;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Status;
import com.mygdx.hadal.strategies.HitboxStrategy;
import com.mygdx.hadal.strategies.hitbox.ControllerDefault;
import com.mygdx.hadal.utils.Constants;

public class OurGetAlongShirt extends Artifact {

	private final static int statusNum = 1;
	private final static int slotCost = 1;
	
	private final static float radius = 10.0f;
	
	private final static float procCd = 2.0f;

	private final static Vector2 chainSize = new Vector2(20, 20);
	private final static Sprite chainSprite = Sprite.ORB_BLUE;
	
	private final static float chainLength = 1.2f;
	
	public OurGetAlongShirt() {
		super(slotCost, statusNum);
	}

	@Override
	public Status[] loadEnchantments(PlayState state, BodyData b) {
		enchantment[0] = new Status(state, b) {
			
			private float procCdCount = procCd;

			//these variables are used in raycasting to find a homing target.
			private Schmuck homeAttempt;
			private Fixture closestFixture;
			private float shortestFraction = 1.0f;
			
			private boolean attached;
			private Schmuck partner;
			
			private Hitbox[] links = new Hitbox[6];
			
			@Override
			public void timePassing(float delta) {
				
				if (attached) {
					if (partner != null) {
						if (!partner.isAlive()) {
							deattach();
						}
					}
				} else {
					if (procCdCount < procCd) {
						procCdCount += delta;
					}
					if (procCdCount >= procCd) {
						procCdCount -= procCd;
						
						state.getWorld().QueryAABB(new QueryCallback() {

							@Override
							public boolean reportFixture(Fixture fixture) {
								if (fixture.getUserData() instanceof BodyData) {
									
									homeAttempt = ((BodyData) fixture.getUserData()).getSchmuck();
									shortestFraction = 1.0f;
									
								  	if (inflicted.getSchmuck().getPosition().x != homeAttempt.getPosition().x || inflicted.getSchmuck().getPosition().y != homeAttempt.getPosition().y) {
								  		
								  		state.getWorld().rayCast(new RayCastCallback() {

											@Override
											public float reportRayFixture(Fixture fixture, Vector2 point, Vector2 normal, float fraction) {
												if (fixture.getFilterData().categoryBits == Constants.BIT_WALL) {
													if (fraction < shortestFraction) {
														shortestFraction = fraction;
														closestFixture = fixture;
														return fraction;
													}
												} else if (fixture.getUserData() instanceof BodyData) {
													if (fraction < shortestFraction) {
														shortestFraction = fraction;
														closestFixture = fixture;
														return fraction;
													}
												} 
												return -1.0f;
											}
										}, inflicted.getSchmuck().getPosition(), homeAttempt.getPosition());	
								  		
								  		if (closestFixture != null) {
											if (closestFixture.getUserData() instanceof BodyData) {
												attach(((BodyData) closestFixture.getUserData()).getSchmuck());
											}
										}
								  	}
								}
								return true;
							}
						}, 
						inflicted.getSchmuck().getPosition().x - radius, inflicted.getSchmuck().getPosition().y - radius, 
						inflicted.getSchmuck().getPosition().x + radius, inflicted.getSchmuck().getPosition().y + radius);		
					}
				}
			}
			
			@Override
			public void onDeath(BodyData perp) {
				deattach();
			}
			
			private void attach(Schmuck partner) {
				attached = true;
				this.partner = partner;
				
				for (int i = 0; i < links.length; i++) {
					final int currentI = i;
					links[i] = new Hitbox(state, inflicted.getSchmuck().getPixelPosition(), chainSize, 0, new Vector2(0, 0), inflicted.getSchmuck().getHitboxfilter(), true, false, inflicted.getSchmuck(), chainSprite);
					
					links[i].setPassability((short) (Constants.BIT_PROJECTILE | Constants.BIT_WALL | Constants.BIT_PLAYER | Constants.BIT_ENEMY));

					links[i].setDensity(1.0f);
					links[i].makeUnreflectable();
					
					links[i].addStrategy(new HitboxStrategy(state, links[i], inflicted) {
						
						private boolean linked = false;
						
						@Override
						public void controller(float delta) {
							
							if (!linked) {
								if (currentI == 0) { 
									if (inflicted.getSchmuck().getBody() != null) {
										linked = true;
										RevoluteJointDef joint1 = new RevoluteJointDef();
										joint1.bodyA = inflicted.getSchmuck().getBody();
										joint1.bodyB = hbox.getBody();
										joint1.collideConnected = false;
										
										joint1.localAnchorA.set(-chainLength, 0);
										joint1.localAnchorB.set(chainLength, 0);
										
										state.getWorld().createJoint(joint1);
									}
								} else {
									if (links[currentI - 1].getBody() != null) {
										linked = true;
										
										RevoluteJointDef joint1 = new RevoluteJointDef();
										joint1.bodyA = links[currentI - 1].getBody();
										joint1.bodyB = hbox.getBody();
										joint1.collideConnected = false;
										joint1.localAnchorA.set(-chainLength, 0);
										joint1.localAnchorB.set(chainLength, 0);
										
										state.getWorld().createJoint(joint1);
									}
								}
								
								if (currentI == links.length - 1) {
									if (partner.getBody() != null) {
										linked = true;
										
										RevoluteJointDef joint1 = new RevoluteJointDef();
										joint1.bodyA = hbox.getBody();
										joint1.bodyB = partner.getBody();
										joint1.collideConnected = false;
										joint1.localAnchorA.set(-chainLength, 0);
										joint1.localAnchorB.set(chainLength, 0);
										
										state.getWorld().createJoint(joint1);
									}
								}
							}
						}
						
						@Override
						public void die() {
							hbox.queueDeletion();
						}
					});
				}
			}
			
			private void deattach() {
				attached = false;
				
				for (int i = 0; i < links.length; i++) {
					if (links[i] != null) {
						links[i].setLifeSpan(2.0f);
						links[i].addStrategy(new ControllerDefault(state, links[i], inflicted));
					}
				}
			}
		};
		return enchantment;
	}
}
