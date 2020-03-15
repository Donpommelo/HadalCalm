package com.mygdx.hadal.equip.actives;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.ActiveItem;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.schmucks.bodies.enemies.TurretFlak;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.bodies.hitboxes.RangedHitbox;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Temporary;
import com.mygdx.hadal.strategies.HitboxStrategy;
import com.mygdx.hadal.strategies.hitbox.ControllerDefault;

public class PortableTurret extends ActiveItem {

	private final static float usecd = 0.0f;
	private final static float usedelay = 0.1f;
	private final static float maxCharge = 800.0f;
	
	private final static Vector2 projectileSize = new Vector2(70, 70);
	private final static float lifespan = 3.0f;

	private final static float projectileSpeed = 12.0f;
	private final static float turretLifespan = 20.0f;
	
	private final static Sprite projSprite = Sprite.ORB_BLUE;

	public PortableTurret(Schmuck user) {
		super(user, usecd, usedelay, maxCharge, chargeStyle.byDamageInflict);
	}
	
	@Override
	public void useItem(PlayState state, PlayerBodyData user) {
		
		final boolean faceRight = weaponVelo.x > 0;
		
		Hitbox hbox = new RangedHitbox(state, user.getPlayer().getPixelPosition(), projectileSize, lifespan,  new Vector2(0, -projectileSpeed), user.getPlayer().getHitboxfilter(), 	false, false, user.getPlayer(), projSprite);
		
		hbox.setGravity(3.0f);
		
		hbox.addStrategy(new ControllerDefault(state, hbox, user));
		hbox.addStrategy(new HitboxStrategy(state, hbox, user) {
			
			@Override
			public void controller(float delta) {
				if (hbox.getLinearVelocity().isZero()) {
					hbox.die();
				}
			}
			
			@Override
			public void die() {
				new TurretFlak(state, hbox.getPixelPosition(), faceRight ? 0 : 180, hbox.getFilter(), null) {
					
					@Override
					public void create() {
						super.create();
						getBodyData().addStatus(new Temporary(state, turretLifespan, getBodyData(), getBodyData(), turretLifespan));
					}
				};
			}
		});
		hbox.setFriction(1.0f);
	}
}
