package com.mygdx.hadal.equip.actives;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.ActiveItem;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.bodies.hitboxes.RangedHitbox;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;
import com.mygdx.hadal.strategies.HitboxStrategy;
import com.mygdx.hadal.strategies.hitbox.*;

public class Fafrotskies extends ActiveItem {

	private static final float usecd = 0.0f;
	private static final float usedelay = 0.1f;
	private static final float maxCharge = 15.0f;
	
	private static final Vector2 projectileSize = new Vector2(200, 50);
	private static final float lifespan = 7.5f;
	
	private static final Vector2 rainSize = new Vector2(30, 20);
	
	private static final float rainDamage = 11.0f;
	private static final float rainKnockback = 2.0f;
	
	private static final float projectileSpeed = 5.0f;
	private static final float rainSpeed = 15.0f;
	
	private static final Sprite projSprite = Sprite.ORB_BLUE;

	public Fafrotskies(Schmuck user) {
		super(user, usecd, usedelay, maxCharge, chargeStyle.byTime);
	}
	
	@Override
	public void useItem(PlayState state, PlayerBodyData user) {
		SoundEffect.THUNDER.playUniversal(state, user.getPlayer().getPixelPosition(), 1.0f, false);

		Hitbox hbox = new RangedHitbox(state, user.getPlayer().getPixelPosition(), projectileSize, lifespan, this.weaponVelo.scl(projectileSpeed), (short) 0, false, false, user.getPlayer(), projSprite);
		
		hbox.setGravity(-0.2f);
		
		hbox.addStrategy(new ControllerDefault(state, hbox, user));
		hbox.addStrategy(new HitboxStrategy(state, hbox, user) {
			
			private float controllerCount = 0;
			private static final float rainInterval = 0.1f;
			
			@Override
			public void controller(float delta) {
				controllerCount += delta;

				while (controllerCount >= rainInterval) {
					controllerCount -= rainInterval;
					
					Hitbox rain = new Hitbox(state, new Vector2(hbox.getPixelPosition()).add((MathUtils.random() -  0.5f) * hbox.getSize().x, 0), rainSize, lifespan, new Vector2(0, -rainSpeed),
							user.getPlayer().getHitboxfilter(), true, false, user.getPlayer(), Sprite.SPIT);
					
					rain.addStrategy(new ControllerDefault(state, rain, user));
					rain.addStrategy(new DamageStandard(state, rain, user, rainDamage, rainKnockback, DamageTypes.WATER));
					rain.addStrategy(new AdjustAngle(state, rain, user));
					rain.addStrategy(new ContactWallParticles(state, rain, user, Particle.BUBBLE_IMPACT));
					rain.addStrategy(new ContactUnitLoseDurability(state, rain, user));
					rain.addStrategy(new ContactWallDie(state, rain, user));
				}
			}
		});
	}
}
