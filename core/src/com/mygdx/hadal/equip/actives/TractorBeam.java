package com.mygdx.hadal.equip.actives;


import static com.mygdx.hadal.utils.Constants.PPM;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.ActiveItem;
import com.mygdx.hadal.equip.Equipable;
import com.mygdx.hadal.schmucks.UserDataTypes;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.bodies.hitboxes.HitboxSprite;
import com.mygdx.hadal.schmucks.strategies.HitboxDefaultStrategy;
import com.mygdx.hadal.schmucks.strategies.HitboxMouseStrategy;
import com.mygdx.hadal.schmucks.strategies.HitboxOnContactWallDieStrategy;
import com.mygdx.hadal.schmucks.strategies.HitboxOnHitTrackStrategy;
import com.mygdx.hadal.schmucks.strategies.HitboxStrategy;
import com.mygdx.hadal.schmucks.userdata.HadalData;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;

public class TractorBeam extends ActiveItem {

	private final static String name = "Tractor Beam";
	private final static float usecd = 0.0f;
	private final static float usedelay = 0.0f;
	private final static float maxCharge = 10.0f;
	
	private final static float baseDamage = 25.0f;
	private final static float knockback = 35.0f;
	private final static float projectileSpeed = 30.0f;
	private final static int projectileWidth = 120;
	private final static int projectileHeight = 120;
	private final static float lifespan = 4.0f;
	private final static float gravity = 0;
	
	private final static int projDura = 1;
	
	private static final float maxLinSpd = 600;
	private static final float maxLinAcc = 3000;
	private static final float maxAngSpd = 1080;
	private static final float maxAngAcc = 7200;
	
	private static final int boundingRad = 500;
	private static final int decelerationRadius = 0;
	
	private final static Sprite projSprite = Sprite.ORB_PINK;

	public TractorBeam(Schmuck user) {
		super(user, name, usecd, usedelay, maxCharge, chargeStyle.byTime);
	}
	
	@Override
	public void useItem(PlayState state, final PlayerBodyData user) {
		
		final Equipable tool = this;
		
		Hitbox hbox = new HitboxSprite(state, 
				user.getPlayer().getPosition().x * PPM, 
				user.getPlayer().getPosition().y * PPM,
				projectileWidth, projectileHeight, gravity, lifespan, projDura, 1, this.weaponVelo.scl(projectileSpeed),
				user.getPlayer().getHitboxfilter(), false, true, user.getPlayer(), projSprite) {
			
			@Override
			public void create() {
				super.create();
				this.body.getFixtureList().get(1).setUserData(data);	
			}
		};
		
		final HitboxOnContactWallDieStrategy start = new HitboxOnContactWallDieStrategy(state, hbox, user);
		final HitboxOnHitTrackStrategy track = new HitboxOnHitTrackStrategy(state, hbox, user, true);
		
		hbox.addStrategy(new HitboxDefaultStrategy(state, hbox, user));
		hbox.addStrategy(track);
		hbox.addStrategy(start);
		hbox.addStrategy(new HitboxStrategy(state, hbox, user) {
			
			private boolean oneTime = true;
			
			@Override
			public void onHit(HadalData fixB) {						
					
				if (fixB != null) {
					if (fixB.getType().equals(UserDataTypes.BODY) && oneTime) {
						oneTime = false;
						hbox.removeStrategy(start);
						hbox.removeStrategy(this);
						hbox.addStrategy(new HitboxMouseStrategy(state, hbox, user, maxLinSpd, maxLinAcc, maxAngSpd, maxAngAcc, boundingRad, decelerationRadius));
						hbox.addStrategy(new HitboxStrategy(state, hbox, user) {
							
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
												fixB.receiveDamage(baseDamage, this.hbox.getLinearVelocity().nor().scl(knockback),
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

}
