package com.mygdx.hadal.equip.actives;

import static com.mygdx.hadal.utils.Constants.PPM;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.ActiveItem;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.schmucks.bodies.enemies.Turret;
import com.mygdx.hadal.schmucks.bodies.enemies.Enemy.enemyType;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.bodies.hitboxes.HitboxSprite;
import com.mygdx.hadal.schmucks.strategies.HitboxDefaultStrategy;
import com.mygdx.hadal.schmucks.strategies.HitboxStrategy;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Temporary;

public class PortableTurret extends ActiveItem {

	private final static String name = "Portable Turret";
	private final static float usecd = 0.0f;
	private final static float usedelay = 0.1f;
	private final static float maxCharge = 600.0f;
	
	private final static int projectileWidth = 70;
	private final static float lifespan = 3.0f;
	private final static float gravity = 3.0f;
	private final static float restitution = 0.0f;
	
	private final static int projDura = 1;

	private final static float projectileSpeed = 12.0f;
	private final static float turretLifespan = 10.0f;
	
	private final static Sprite projSprite = Sprite.ORB_BLUE;

	public PortableTurret(Schmuck user) {
		super(user, name, usecd, usedelay, maxCharge, chargeStyle.byDamage);
	}
	
	@Override
	public void useItem(PlayState state, PlayerBodyData user) {
		
		final boolean faceRight = weaponVelo.x > 0;
		
		Hitbox hbox = new HitboxSprite(state, 
				user.getPlayer().getBody().getPosition().x * PPM, 
				user.getPlayer().getBody().getPosition().y * PPM,
				projectileWidth, projectileWidth, gravity, lifespan, projDura, restitution, 
				new Vector2(0, -projectileSpeed), user.getPlayer().getHitboxfilter(), 
				false, false, user.getPlayer(), projSprite);
		
		hbox.addStrategy(new HitboxDefaultStrategy(state, hbox, user, false));
		hbox.addStrategy(new HitboxStrategy(state, hbox, user) {
			
			@Override
			public void controller(float delta) {
				if (hbox.getBody().getLinearVelocity().isZero()) {
					hbox.die();
				}
			}
			
			@Override
			public void die() {
				new Turret(state, (int)(hbox.getPosition().x * PPM), (int)(hbox.getPosition().y * PPM - projectileWidth / 4),
						enemyType.TURRET_FLAK, faceRight ? 0 : 180, hbox.getFilter()) {
					
					@Override
					public void create() {
						super.create();
						bodyData.addStatus(new Temporary(state, turretLifespan, bodyData, bodyData, turretLifespan));
					}
				};
			}
		});
		hbox.setFriction(1.0f);
	}
}
