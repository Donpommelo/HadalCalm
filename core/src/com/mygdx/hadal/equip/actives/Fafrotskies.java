package com.mygdx.hadal.equip.actives;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.ActiveItem;
import com.mygdx.hadal.managers.GameStateManager;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.bodies.hitboxes.RangedHitbox;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;
import com.mygdx.hadal.strategies.HitboxStrategy;
import com.mygdx.hadal.strategies.hitbox.AdjustAngle;
import com.mygdx.hadal.strategies.hitbox.ContactUnitLoseDurability;
import com.mygdx.hadal.strategies.hitbox.ContactWallDie;
import com.mygdx.hadal.strategies.hitbox.ContactWallParticles;
import com.mygdx.hadal.strategies.hitbox.ControllerDefault;
import com.mygdx.hadal.strategies.hitbox.DamageStandard;

public class Fafrotskies extends ActiveItem {

	private final static float usecd = 0.0f;
	private final static float usedelay = 0.1f;
	private final static float maxCharge = 15.0f;
	
	private final static Vector2 projectileSize = new Vector2(200, 50);
	private final static float lifespan = 7.5f;
	
	private final static Vector2 rainSize = new Vector2(30, 20);
	
	private final static float rainDamage = 11.0f;
	private final static float rainKnockback = 2.0f;
	
	private final static float projectileSpeed = 5.0f;
	private final static float rainSpeed = 15.0f;
	
	private final static Sprite projSprite = Sprite.ORB_BLUE;

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
			private final static float rainInterval = 0.1f;
			
			@Override
			public void controller(float delta) {
				controllerCount += delta;

				while (controllerCount >= rainInterval) {
					controllerCount -= rainInterval;
					
					Hitbox rain = new Hitbox(state, new Vector2(hbox.getPixelPosition()).add((GameStateManager.generator.nextFloat() -  0.5f) * hbox.getSize().x, 0), rainSize, lifespan, new Vector2(0, -rainSpeed),
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