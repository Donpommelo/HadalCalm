package com.mygdx.hadal.equip.artifacts;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.mygdx.hadal.battle.SyncedAttack;
import com.mygdx.hadal.constants.Constants;
import com.mygdx.hadal.schmucks.entities.Player;
import com.mygdx.hadal.schmucks.entities.Schmuck;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Status;
import com.mygdx.hadal.utils.WorldUtil;

public class OurGetAlongShirt extends Artifact {

	private static final int SLOT_COST = 1;
	
	private static final float RADIUS = 10.0f;
	private static final float PROC_CD = 2.0f;

	public OurGetAlongShirt() {
		super(SLOT_COST);
	}

	@Override
	public void loadEnchantments(PlayState state, PlayerBodyData p) {
		enchantment = new Status(state, p) {
			
			private float procCdCount = PROC_CD;

			//these variables are used in raycasting to find a homing target.
			private Schmuck homeAttempt;
			private Fixture closestFixture;
			private float shortestFraction = 1.0f;
			
			private boolean attached;

			private final Vector2 entityLocation = new Vector2();
			private final Vector2 homeLocation = new Vector2();
			private Hitbox[] links;
			@Override
			public void timePassing(float delta) {
//				if (state.getMode().isHub()) { return; }

				if (null != links && links.length > 0) {
					if (!links[0].isAlive()) {
						attached = false;
					}
				}

				if (!attached) {
					if (procCdCount < PROC_CD) {
						procCdCount += delta;
					}
					if (procCdCount >= PROC_CD) {
						procCdCount -= PROC_CD;
						
						entityLocation.set(p.getSchmuck().getPosition());
						state.getWorld().QueryAABB(fixture -> {
							if (fixture.getUserData() instanceof PlayerBodyData bodyData) {

								homeAttempt = bodyData.getSchmuck();
								homeLocation.set(homeAttempt.getPosition());
								shortestFraction = 1.0f;

								if (WorldUtil.preRaycastCheck(entityLocation, homeLocation)) {
									  state.getWorld().rayCast((fixture1, point, normal, fraction) -> {
										  if (fixture1.getFilterData().categoryBits == Constants.BIT_WALL) {
											  if (fraction < shortestFraction) {
												  shortestFraction = fraction;
												  closestFixture = fixture1;
												  return fraction;
											  }
										  } else if (fixture1.getUserData() instanceof PlayerBodyData) {
											  if (fraction < shortestFraction) {
												  shortestFraction = fraction;
												  closestFixture = fixture1;
												  return fraction;
											  }
										  }
										  return -1.0f;
									  }, entityLocation, homeLocation);

									  if (closestFixture != null) {
										if (closestFixture.getUserData() instanceof PlayerBodyData closestData) {
											attach(closestData.getPlayer());
										}
									}
								  }
							}
							return true;
						}, entityLocation.x - RADIUS, entityLocation.y - RADIUS, entityLocation.x + RADIUS, entityLocation.y + RADIUS);
					}
				}
			}
			
			private void attach(Player partner) {
				
				if (!attached) {
					attached = true;
					links = SyncedAttack.OUR_GET_ALONG_SHIRT.initiateSyncedAttackMulti(state, p.getSchmuck(), new Vector2(),
							new Vector2[] {}, new Vector2[] {}, partner.getConnID());
				}
			}
		}.setServerOnly(true);
	}
}
