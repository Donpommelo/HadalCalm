package com.mygdx.hadal.equip.actives;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.ActiveItem;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.bodies.hitboxes.RangedHitbox;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;
import com.mygdx.hadal.statuses.Status;
import com.mygdx.hadal.strategies.hitbox.AdjustAngle;
import com.mygdx.hadal.strategies.hitbox.ContactUnitDie;
import com.mygdx.hadal.strategies.hitbox.ContactWallDie;
import com.mygdx.hadal.strategies.hitbox.ControllerDefault;
import com.mygdx.hadal.strategies.hitbox.Curve;
import com.mygdx.hadal.strategies.hitbox.DamageStandard;

public class BendingBeams extends ActiveItem {

	private final static float usecd = 0.0f;
	private final static float usedelay = 0.0f;
	private final static float maxCharge = 6.0f;
	
	private final static Vector2 projectileSize = new Vector2(60, 20);
	private final static float lifespan = 5.0f;
	private final static float projectileSpeed = 50.0f;
	
	private final static float duration = 0.5f;
	
	private static final float procCd = 0.05f;
	private static final float damage = 11.0f;
	private final static float knockback = 20.0f;

	private final static Sprite projSprite = Sprite.ORB_ORANGE;

	public BendingBeams(Schmuck user) {
		super(user, usecd, usedelay, maxCharge, chargeStyle.byTime);
	}
	
	@Override
	public void useItem(PlayState state, PlayerBodyData user) {
		SoundEffect.LASERHARPOON.playUniversal(state, user.getPlayer().getPixelPosition(), 1.0f, false);
		
		user.addStatus(new Status(state, duration, false, user, user) {
			
			private float procCdCount;
			private Vector2 startVelo = new Vector2();
			@Override
			public void timePassing(float delta) {
				super.timePassing(delta);
				if (procCdCount >= procCd) {
					procCdCount -= procCd;
					
					startVelo.set(weaponVelo).nor().scl(projectileSpeed);
					
					Hitbox hbox = new RangedHitbox(state, user.getPlayer().getPixelPosition(), projectileSize, lifespan, startVelo, user.getPlayer().getHitboxfilter(), true, false, user.getPlayer(), projSprite);
					hbox.addStrategy(new ControllerDefault(state, hbox, user));
					hbox.addStrategy(new DamageStandard(state, hbox, user, damage, knockback, DamageTypes.ENERGY));
					hbox.addStrategy(new ContactUnitDie(state, hbox, user));
					hbox.addStrategy(new ContactWallDie(state, hbox, user));
					hbox.addStrategy(new AdjustAngle(state, hbox, user));
					hbox.addStrategy(new Curve(state, hbox, user, 30, 45, user.getPlayer().getMouse().getPixelPosition(), projectileSpeed, 0.1f));
				}
				procCdCount += delta;
			}
		});
	}
	
	@Override
	public float getUseDuration() { return duration; }
}
