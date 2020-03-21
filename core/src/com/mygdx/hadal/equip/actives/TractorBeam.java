package com.mygdx.hadal.equip.actives;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.ActiveItem;
import com.mygdx.hadal.schmucks.UserDataTypes;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.bodies.hitboxes.RangedHitbox;
import com.mygdx.hadal.schmucks.userdata.HadalData;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;
import com.mygdx.hadal.strategies.HitboxStrategy;
import com.mygdx.hadal.strategies.hitbox.ContactWallDie;
import com.mygdx.hadal.strategies.hitbox.ControllerDefault;
import com.mygdx.hadal.strategies.hitbox.HitTrack;
import com.mygdx.hadal.strategies.hitbox.HomingMouse;

public class TractorBeam extends ActiveItem {

	private final static float usecd = 0.0f;
	private final static float usedelay = 0.0f;
	private final static float maxCharge = 10.0f;
	
	private final static float primaryDamage = 12.0f;
	private final static float secondaryDamage = 6.0f;
	private final static float knockback = 35.0f;
	private final static float projectileSpeed = 30.0f;
	
	private final static float homePower = 150.0f;

	private final static Vector2 projectileSize = new Vector2(60, 60);
	
	private final static float lifespan = 4.0f;
	
	private final static Sprite projSprite = Sprite.ORB_PINK;

	public TractorBeam(Schmuck user) {
		super(user,  usecd, usedelay, maxCharge, chargeStyle.byTime);
	}
	
	@Override
	public void useItem(PlayState state, final PlayerBodyData user) {
		
		Hitbox hbox = new RangedHitbox(state, user.getPlayer().getPixelPosition(), projectileSize, lifespan, this.weaponVelo.scl(projectileSpeed),
				user.getPlayer().getHitboxfilter(), false, true, user.getPlayer(), projSprite) {
			
			@Override
			public void create() {
				super.create();
				this.body.getFixtureList().get(1).setUserData(data);	
			}
		};
		hbox.setRestitution(1.0f);
		
		final ContactWallDie start = new ContactWallDie(state, hbox, user);
		final HitTrack track = new HitTrack(state, hbox, user, true);
		
		hbox.addStrategy(new ControllerDefault(state, hbox, user));
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
						hbox.addStrategy(new HomingMouse(state, hbox, user, homePower));
						hbox.addStrategy(new HitboxStrategy(state, hbox, user) {
							
							@Override
							public void onHit(final HadalData fixB) {
								if (track.getTarget() != null) {
									if (track.getTarget().getHadalData() != fixB) {
										
										if (fixB != null && track.getTarget().getBody() != null) {

											if (fixB.getType().equals(UserDataTypes.BODY) || fixB.getType().equals(UserDataTypes.WALL)) {
												track.getTarget().getHadalData().receiveDamage(secondaryDamage, new Vector2(0, 0), creator, true, DamageTypes.CRUSHING);
											}

											if (fixB.getType().equals(UserDataTypes.BODY)) {
												fixB.receiveDamage(primaryDamage, this.hbox.getLinearVelocity().nor().scl(knockback), creator, true, DamageTypes.CRUSHING);
											}
										} else {
											track.getTarget().getHadalData().receiveDamage(secondaryDamage, new Vector2(0, 0), creator, true, DamageTypes.CRUSHING);
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
	
	@Override
	public float getUseDuration() { return lifespan; }
}
