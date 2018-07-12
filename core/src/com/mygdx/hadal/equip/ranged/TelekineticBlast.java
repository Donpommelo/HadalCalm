package com.mygdx.hadal.equip.ranged;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.equip.Equipable;
import com.mygdx.hadal.equip.RangedWeapon;
import com.mygdx.hadal.schmucks.UserDataTypes;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.bodies.hitboxes.HitboxImage;
import com.mygdx.hadal.schmucks.strategies.HitboxDefaultStrategy;
import com.mygdx.hadal.schmucks.strategies.HitboxMouseStrategy;
import com.mygdx.hadal.schmucks.strategies.HitboxOnContactWallDieStrategy;
import com.mygdx.hadal.schmucks.strategies.HitboxOnHitTrackStrategy;
import com.mygdx.hadal.schmucks.strategies.HitboxStrategy;
import com.mygdx.hadal.schmucks.userdata.HadalData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;
import com.mygdx.hadal.utils.HitboxFactory;

public class TelekineticBlast extends RangedWeapon {

	private final static String name = "Telekinetic Blast";
	private final static int clipSize = 1;
	private final static float shootCd = 0.15f;
	private final static float shootDelay = 0;
	private final static float reloadTime = 1.4f;
	private final static int reloadAmount = 0;

	private final static float recoil = 0.0f;
	private final static float baseDamage = 15.0f;
	private final static float knockback = 35.0f;
	private final static float projectileSpeedStart = 30.0f;
	private final static int projectileWidth = 120;
	private final static int projectileHeight = 120;
	private final static float lifespan = 4.0f;
	private final static float gravity = 0;
	
	private final static int projDura = 1;
		
	private final static String weapSpriteId = "tractorbeam";
	private final static String projSpriteId = "orb_pink";

	private static final float maxLinSpd = 600;
	private static final float maxLinAcc = 3000;
	private static final float maxAngSpd = 1080;
	private static final float maxAngAcc = 7200;
	
	private static final int boundingRad = 500;
	private static final int decelerationRadius = 0;
	
	private final static HitboxFactory onShoot = new HitboxFactory() {

		@Override
		public void makeHitbox(final Schmuck user, PlayState state, final Equipable tool, Vector2 startVelocity, float x, float y, short filter) {
			
			Hitbox hbox = new HitboxImage(state, x, y, projectileWidth, projectileHeight, gravity, lifespan, projDura, 1, startVelocity,
					filter, false, true, user, projSpriteId) {
				
				@Override
				public void create() {
					super.create();
					this.body.getFixtureList().get(1).setUserData(data);	
				}
			};
			
			final HitboxOnContactWallDieStrategy start = new HitboxOnContactWallDieStrategy(state, hbox, user.getBodyData());
			final HitboxOnHitTrackStrategy track = new HitboxOnHitTrackStrategy(state, hbox, user.getBodyData(), true);
			
			hbox.addStrategy(new HitboxDefaultStrategy(state, hbox, user.getBodyData()));
			hbox.addStrategy(track);
			hbox.addStrategy(start);
			hbox.addStrategy(new HitboxStrategy(state, hbox, user.getBodyData()) {
				
				private boolean oneTime = true;
				
				@Override
				public void onHit(HadalData fixB) {						
						
					if (fixB != null) {
						if (fixB.getType().equals(UserDataTypes.BODY) && oneTime) {
							oneTime = false;
							hbox.removeStrategy(start);
							hbox.removeStrategy(this);
							hbox.addStrategy(new HitboxMouseStrategy(state, hbox, user.getBodyData(), maxLinSpd, maxLinAcc, maxAngSpd, maxAngAcc, boundingRad, decelerationRadius));
							hbox.addStrategy(new HitboxStrategy(state, hbox, user.getBodyData()) {
								
								@Override
								public void onHit(final HadalData fixB) {
									if (track.getTarget() != null) {
										if (track.getTarget().getHadalData() != fixB) {
											
											if (fixB != null && track.getTarget().getBody() != null) {

												if (fixB.getType().equals(UserDataTypes.BODY) || fixB.getType().equals(UserDataTypes.WALL)) {
													track.getTarget().getHadalData().receiveDamage(baseDamage, new Vector2(0, 0),
															creator, tool, true, DamageTypes.RANGED);
												}

												if (fixB.getType().equals(UserDataTypes.BODY)) {
													fixB.receiveDamage(baseDamage, this.hbox.getBody().getLinearVelocity().nor().scl(knockback),
															creator, tool, true, DamageTypes.RANGED);
												}
											} else {
												track.getTarget().getHadalData().receiveDamage(baseDamage, new Vector2(0, 0),
														creator, tool, true, DamageTypes.RANGED);
											}
										}
									}
								}
							});
						}	
					}
				}
			});
		}
	};
	
	public TelekineticBlast(Schmuck user) {
		super(user, name, clipSize, reloadTime, recoil, projectileSpeedStart, shootCd, shootDelay, reloadAmount, onShoot, weapSpriteId);
	}

}
