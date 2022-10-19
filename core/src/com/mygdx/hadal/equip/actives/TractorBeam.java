package com.mygdx.hadal.equip.actives;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.ActiveItem;
import com.mygdx.hadal.constants.UserDataType;
import com.mygdx.hadal.schmucks.entities.Schmuck;
import com.mygdx.hadal.schmucks.entities.enemies.Enemy;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.entities.hitboxes.RangedHitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.schmucks.userdata.HadalData;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.battle.DamageTag;
import com.mygdx.hadal.strategies.HitboxStrategy;
import com.mygdx.hadal.strategies.hitbox.AdjustAngle;
import com.mygdx.hadal.strategies.hitbox.ContactWallDie;
import com.mygdx.hadal.strategies.hitbox.ControllerDefault;
import com.mygdx.hadal.strategies.hitbox.HomingMouse;

/**
 * @author Bringerbread Brugdanoff
 */
public class TractorBeam extends ActiveItem {

	private static final float USECD = 0.0f;
	private static final float USEDELAY = 0.0f;
	private static final float MAX_CHARGE = 6.0f;

	private static final Vector2 PROJECTILE_SIZE = new Vector2(80, 60);
	private static final float PRIMARY_DAMAGE = 40.0f;
	private static final float SECONDARY_DAMAGE = 30.0f;
	private static final float KNOCKBACK = 35.0f;
	private static final float PROJECTILE_SPEED = 40.0f;
	private static final float HOME_POWER = 300.0f;
	private static final float LIFESPAN = 4.0f;
	
	private static final Sprite PROJ_SPRITE = Sprite.OPEN_HAND;
	private static final Sprite PROJ_SPRITE_2 = Sprite.CLOSED_HAND;

	public TractorBeam(Schmuck user) { super(user, USECD, USEDELAY, MAX_CHARGE); }
	
	@Override
	public void useItem(PlayState state, PlayerBodyData user) {
		SoundEffect.LASERHARPOON.playUniversal(state, user.getPlayer().getPixelPosition(), 0.75f, false);

		Hitbox hbox = new RangedHitbox(state, user.getPlayer().getProjectileOrigin(weaponVelo, PROJECTILE_SIZE.x), PROJECTILE_SIZE,
				LIFESPAN, new Vector2(weaponVelo).nor().scl(PROJECTILE_SPEED), user.getPlayer().getHitboxfilter(),
				true, true, user.getPlayer(), PROJ_SPRITE);
		
		hbox.addStrategy(new ControllerDefault(state, hbox, user));
		hbox.addStrategy(new AdjustAngle(state, hbox, user));
		hbox.addStrategy(new ContactWallDie(state, hbox, user));
		hbox.addStrategy(new HitboxStrategy(state, hbox, user) {
			
			@Override
			public void onHit(HadalData fixB) {						
					
				if (fixB != null) {
					if (UserDataType.BODY.equals(fixB.getType())) {
						
						final BodyData track = (BodyData) fixB;
						if (track.getSchmuck() instanceof Enemy enemy) {
							if (enemy.isBoss()) {
								return;
							}
						}
						
						Hitbox grab = new RangedHitbox(state, hbox.getPixelPosition(), PROJECTILE_SIZE, LIFESPAN, new Vector2(),
								user.getPlayer().getHitboxfilter(), false, true, user.getPlayer(), PROJ_SPRITE_2);
						grab.setRestitution(1.0f);
						grab.setSyncDefault(false);
						grab.setSyncInstant(true);
						
						grab.addStrategy(new ControllerDefault(state, grab, user));
						grab.addStrategy(new AdjustAngle(state, grab, user));
						grab.addStrategy(new HomingMouse(state, grab, user, HOME_POWER));

						grab.addStrategy(new HitboxStrategy(state, grab, user) {
							
							@Override
							public void onHit(final HadalData fixB) {
								if (track != fixB) {

									if (fixB != null && track.getSchmuck().getBody() != null) {

										if (UserDataType.BODY.equals(fixB.getType()) || UserDataType.WALL.equals(fixB.getType())) {
											track.receiveDamage(SECONDARY_DAMAGE, new Vector2(0, 0), creator, true, grab,
													DamageSource.TRACTOR_BEAM, DamageTag.WHACKING);
										}

										if (UserDataType.BODY.equals(fixB.getType())) {
											fixB.receiveDamage(PRIMARY_DAMAGE, hbox.getLinearVelocity().nor().scl(KNOCKBACK),
													creator, true, grab, DamageSource.TRACTOR_BEAM, DamageTag.WHACKING);
										}
									} else {
										track.receiveDamage(SECONDARY_DAMAGE, new Vector2(0, 0), creator, true, grab,
												DamageSource.TRACTOR_BEAM, DamageTag.WHACKING);
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
	public float getUseDuration() { return LIFESPAN; }

	@Override
	public String[] getDescFields() {
		return new String[] {
				String.valueOf((int) MAX_CHARGE),
				String.valueOf((int) LIFESPAN),
				String.valueOf((int) PRIMARY_DAMAGE),
				String.valueOf((int) SECONDARY_DAMAGE)};
	}
}
