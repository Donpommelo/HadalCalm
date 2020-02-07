package com.mygdx.hadal.equip.actives;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.ActiveItem;
import com.mygdx.hadal.event.Currents;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.bodies.hitboxes.RangedHitbox;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;
import com.mygdx.hadal.strategies.HitboxStrategy;
import com.mygdx.hadal.strategies.hitbox.ContactWallDie;
import com.mygdx.hadal.strategies.hitbox.ControllerDefault;
import com.mygdx.hadal.strategies.hitbox.DamageStandard;

public class MeridianMaker extends ActiveItem {

	private final static float usecd = 0.0f;
	private final static float usedelay = 0.1f;
	private final static float maxCharge = 11.0f;
	
	private final static float baseDamage = 25.0f;
	private final static float knockback = 0.0f;
	private final static Vector2 projectileSize = new Vector2(30, 30);
	private final static float lifespan = 4.0f;
	
	private final static float projectileSpeed = 10.0f;
	
	private final static Sprite projSprite = Sprite.ORB_BLUE;

	private final static int currentRadius = 100;
	private final static float currentForce = 0.75f;
	
	public MeridianMaker(Schmuck user) {
		super(user, usecd, usedelay, maxCharge, chargeStyle.byTime);
	}
	
	@Override
	public void useItem(PlayState state, PlayerBodyData user) {
		
		final Vector2 currentVec = new Vector2(weaponVelo).nor().scl(currentForce);
		
		Hitbox hbox = new RangedHitbox(state, user.getPlayer().getPixelPosition(), projectileSize, lifespan, this.weaponVelo.scl(projectileSpeed), user.getPlayer().getHitboxfilter(), 
				false, false, user.getPlayer(), projSprite);
		
		hbox.addStrategy(new ControllerDefault(state, hbox, user));
		hbox.addStrategy(new DamageStandard(state, hbox, user, baseDamage, knockback, DamageTypes.RANGED));
		hbox.addStrategy(new ContactWallDie(state, hbox, user));
		hbox.addStrategy(new HitboxStrategy(state, hbox, user) {
			
			private Vector2 lastPosition = new Vector2(hbox.getStartPos());
			
			@Override
			public void controller(float delta) {
				if (lastPosition.dst(hbox.getPixelPosition()) > currentRadius) {
					new Currents(state, lastPosition, new Vector2(currentRadius, currentRadius), currentVec, lifespan);
					lastPosition.set(hbox.getPixelPosition());
				}
			}
		}); 
	}
}
