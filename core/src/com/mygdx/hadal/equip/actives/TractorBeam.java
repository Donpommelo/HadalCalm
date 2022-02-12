package com.mygdx.hadal.equip.actives;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.ActiveItem;
import com.mygdx.hadal.schmucks.UserDataType;
import com.mygdx.hadal.schmucks.entities.Schmuck;
import com.mygdx.hadal.schmucks.entities.enemies.Enemy;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.entities.hitboxes.RangedHitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.schmucks.userdata.HadalData;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;
import com.mygdx.hadal.strategies.HitboxStrategy;
import com.mygdx.hadal.strategies.hitbox.AdjustAngle;
import com.mygdx.hadal.strategies.hitbox.ContactWallDie;
import com.mygdx.hadal.strategies.hitbox.ControllerDefault;
import com.mygdx.hadal.strategies.hitbox.HomingMouse;

/**
 * @author Bringerbread Brugdanoff
 */
public class TractorBeam extends ActiveItem {

	private static final float usecd = 0.0f;
	private static final float usedelay = 0.0f;
	private static final float maxCharge = 6.0f;
	
	private static final float primaryDamage = 40.0f;
	private static final float secondaryDamage = 30.0f;
	private static final float knockback = 35.0f;
	private static final float projectileSpeed = 40.0f;
	
	private static final float homePower = 300.0f;

	private static final Vector2 projectileSize = new Vector2(80, 60);
	
	private static final float lifespan = 4.0f;
	
	private static final Sprite projSprite = Sprite.OPEN_HAND;
	private static final Sprite projSprite2 = Sprite.CLOSED_HAND;

	public TractorBeam(Schmuck user) {
		super(user,  usecd, usedelay, maxCharge, chargeStyle.byTime);
	}
	
	@Override
	public void useItem(PlayState state, PlayerBodyData user) {
		SoundEffect.LASERHARPOON.playUniversal(state, user.getPlayer().getPixelPosition(), 0.75f, false);

		Hitbox hbox = new RangedHitbox(state, user.getPlayer().getProjectileOrigin(weaponVelo, projectileSize.x), projectileSize, lifespan, new Vector2(weaponVelo).nor().scl(projectileSpeed),
				user.getPlayer().getHitboxfilter(), true, true, user.getPlayer(), projSprite);
		
		hbox.addStrategy(new ControllerDefault(state, hbox, user));
		hbox.addStrategy(new AdjustAngle(state, hbox, user));
		hbox.addStrategy(new ContactWallDie(state, hbox, user));
		hbox.addStrategy(new HitboxStrategy(state, hbox, user) {
			
			@Override
			public void onHit(HadalData fixB) {						
					
				if (fixB != null) {
					if (fixB.getType().equals(UserDataType.BODY)) {
						
						final BodyData track = (BodyData) fixB;
						
						if (track.getSchmuck() instanceof Enemy) {
							if (((Enemy) track.getSchmuck()).isBoss()) {
								return;
							}
						}
						
						Hitbox grab = new RangedHitbox(state, hbox.getPixelPosition(), projectileSize, lifespan, new Vector2(), user.getPlayer().getHitboxfilter(), false, true, user.getPlayer(), projSprite2);
						grab.setRestitution(1.0f);
						grab.setSyncDefault(false);
						grab.setSyncInstant(true);
						
						grab.addStrategy(new ControllerDefault(state, grab, user));
						grab.addStrategy(new AdjustAngle(state, grab, user));
						grab.addStrategy(new HomingMouse(state, grab, user, homePower));

						grab.addStrategy(new HitboxStrategy(state, grab, user) {
							
							@Override
							public void onHit(final HadalData fixB) {
								if (track != fixB) {

									if (fixB != null && track.getSchmuck().getBody() != null) {

										if (fixB.getType().equals(UserDataType.BODY) || fixB.getType().equals(UserDataType.WALL)) {
											track.receiveDamage(secondaryDamage, new Vector2(0, 0), creator, true, grab, DamageTypes.WHACKING);
										}

										if (fixB.getType().equals(UserDataType.BODY)) {
											fixB.receiveDamage(primaryDamage, hbox.getLinearVelocity().nor().scl(knockback), creator, true, grab, DamageTypes.WHACKING);
										}
									} else {
										track.receiveDamage(secondaryDamage, new Vector2(0, 0), creator, true, grab, DamageTypes.WHACKING);
									}
								}
							}
							
							@Override
							public void controller(float delta) {
								if (creator.getSchmuck().getBody() == null || !creator.getSchmuck().isAlive() || !track.getSchmuck().isAlive()) {
									hbox.die();
								}
								if (track.getSchmuck().isAlive() && track.getSchmuck().getBody() != null) {
									track.getSchmuck().setTransform(hbox.getPosition(), 0);
								}
							}
						});
						
						hbox.die();
					}	
				}
			}
		});
	}
	
	@Override
	public float getUseDuration() { return lifespan; }
}
