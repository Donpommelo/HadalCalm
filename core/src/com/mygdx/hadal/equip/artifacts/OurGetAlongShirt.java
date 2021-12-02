package com.mygdx.hadal.equip.artifacts;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Status;
import com.mygdx.hadal.strategies.HitboxStrategy;
import com.mygdx.hadal.strategies.hitbox.ControllerDefault;
import com.mygdx.hadal.utils.Constants;

public class OurGetAlongShirt extends Artifact {

	private static final int slotCost = 1;
	
	private static final float radius = 10.0f;
	private static final float procCd = 2.0f;
	private static final Vector2 chainSize = new Vector2(20, 20);
	private static final Sprite chainSprite = Sprite.ORB_BLUE;
	
	private static final float chainLength = 1.2f;
	
	public OurGetAlongShirt() {
		super(slotCost);
	}

	@Override
	public void loadEnchantments(PlayState state, PlayerBodyData p) {
		enchantment = new Status(state, p) {
			
			private float procCdCount = procCd;

			//these variables are used in raycasting to find a homing target.
			private Schmuck homeAttempt;
			private Fixture closestFixture;
			private float shortestFraction = 1.0f;
			
			private boolean attached;
			private Schmuck partner;
			
			private final Hitbox[] links = new Hitbox[6];
			private final Vector2 entityLocation = new Vector2();
			private final Vector2 homeLocation = new Vector2();
			@Override
			public void timePassing(float delta) {
				if (state.getMode().isHub()) { return; }

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
						
						entityLocation.set(p.getSchmuck().getPosition());
						state.getWorld().QueryAABB(fixture -> {
							if (fixture.getUserData() instanceof BodyData bodyData) {

								homeAttempt = bodyData.getSchmuck();
								homeLocation.set(homeAttempt.getPosition());
								shortestFraction = 1.0f;

								  if (entityLocation.x != homeLocation.x || entityLocation.y != homeLocation.y) {
									  state.getWorld().rayCast((fixture1, point, normal, fraction) -> {
										  if (fixture1.getFilterData().categoryBits == Constants.BIT_WALL) {
											  if (fraction < shortestFraction) {
												  shortestFraction = fraction;
												  closestFixture = fixture1;
												  return fraction;
											  }
										  } else if (fixture1.getUserData() instanceof BodyData) {
											  if (fraction < shortestFraction) {
												  shortestFraction = fraction;
												  closestFixture = fixture1;
												  return fraction;
											  }
										  }
										  return -1.0f;
									  }, p.getSchmuck().getPosition(), homeLocation);

									  if (closestFixture != null) {
										if (closestFixture.getUserData() instanceof BodyData closestData) {
											attach(closestData.getSchmuck());
										}
									}
								  }
							}
							return true;
						}, entityLocation.x - radius, entityLocation.y - radius, entityLocation.x + radius, entityLocation.y + radius);
					}
				}
			}
			
			@Override
			public void onDeath(BodyData perp) {
				deattach();
			}
			
			@Override
			public void onRemove() {
				deattach();
			}
			
			private void attach(Schmuck partner) {
				
				if (!attached) {
					attached = true;
					this.partner = partner;
					
					for (int i = 0; i < links.length; i++) {
						final int currentI = i;
						links[i] = new Hitbox(state, p.getSchmuck().getPixelPosition(), chainSize, 0, new Vector2(),
								p.getSchmuck().getHitboxfilter(), true, false, p.getSchmuck(), chainSprite);
						
						links[i].setPassability((short) (Constants.BIT_PROJECTILE | Constants.BIT_WALL | Constants.BIT_PLAYER | Constants.BIT_ENEMY));

						links[i].setDensity(1.0f);
						links[i].makeUnreflectable();
						
						links[i].addStrategy(new HitboxStrategy(state, links[i], p) {
							
							private boolean linked;
							@Override
							public void controller(float delta) {
								
								if (!linked) {
									if (currentI == 0) { 
										if (p.getSchmuck().getBody() != null && hbox.getBody() != null) {
											linked = true;
											RevoluteJointDef joint1 = new RevoluteJointDef();
											joint1.bodyA = p.getSchmuck().getBody();
											joint1.bodyB = hbox.getBody();
											joint1.collideConnected = false;
											
											joint1.localAnchorA.set(0, 0);
											joint1.localAnchorB.set(chainLength, 0);
											
											state.getWorld().createJoint(joint1);
										}
									} else {
										if (links[currentI - 1].getBody() != null && hbox.getBody() != null) {
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
										if (partner.getBody() != null && hbox.getBody() != null) {
											linked = true;
											
											RevoluteJointDef joint1 = new RevoluteJointDef();
											joint1.bodyA = hbox.getBody();
											joint1.bodyB = partner.getBody();
											joint1.collideConnected = false;
											joint1.localAnchorA.set(-chainLength, 0);
											joint1.localAnchorB.set(0, 0);
											
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
			}
			
			private void deattach() {
				attached = false;
				for (final Hitbox link : links) {
					if (link != null) {
						link.setLifeSpan(2.0f);
						link.addStrategy(new ControllerDefault(state, link, p));
					}
				}
			}
		};
	}
}
