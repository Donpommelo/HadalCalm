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

/**
 * @author Flodzilla Fuzekiel
 */
public class BendyBeams extends ActiveItem {

	private static final float usecd = 0.0f;
	private static final float usedelay = 0.0f;
	private static final float maxCharge = 8.0f;
	
	private static final Vector2 projectileSize = new Vector2(60, 20);
	private static final float lifespan = 5.0f;
	private static final float projectileSpeed = 45.0f;
	
	private static final float duration = 1.0f;
	
	private static final float procCd = 0.1f;
	private static final float damage = 14.0f;
	private static final float knockback = 20.0f;

	private static final Sprite projSprite = Sprite.ORB_ORANGE;

	public BendyBeams(Schmuck user) {
		super(user, usecd, usedelay, maxCharge, chargeStyle.byTime);
	}
	
	@Override
	public void useItem(PlayState state, PlayerBodyData user) {
		SoundEffect.LASERHARPOON.playUniversal(state, user.getPlayer().getPixelPosition(), 0.8f, false);
		
		user.addStatus(new Status(state, duration, false, user, user) {
			
			private float procCdCount;
			private final Vector2 startVelo = new Vector2();
			@Override
			public void timePassing(float delta) {
				super.timePassing(delta);
				if (procCdCount >= procCd) {
					procCdCount -= procCd;
					
					startVelo.set(weaponVelo).nor().scl(projectileSpeed);
					
					Hitbox hbox = new RangedHitbox(state, user.getPlayer().getPixelPosition(), projectileSize, lifespan, new Vector2(startVelo), user.getPlayer().getHitboxfilter(), true, false, user.getPlayer(), projSprite);
					hbox.addStrategy(new ControllerDefault(state, hbox, user));
					hbox.addStrategy(new DamageStandard(state, hbox, user, damage, knockback, DamageTypes.ENERGY));
					hbox.addStrategy(new ContactUnitDie(state, hbox, user));
					hbox.addStrategy(new ContactWallDie(state, hbox, user));
					hbox.addStrategy(new AdjustAngle(state, hbox, user));
					hbox.addStrategy(new Curve(state, hbox, user, 90, 180, user.getPlayer().getMouse().getPixelPosition(), projectileSpeed, 0.1f));
				}
				procCdCount += delta;
			}
		});
	}
	
	@Override
	public float getUseDuration() { return duration; }
}
