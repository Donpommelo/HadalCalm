package com.mygdx.hadal.equip.actives;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.ActiveItem;
import com.mygdx.hadal.schmucks.entities.Schmuck;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.entities.hitboxes.RangedHitbox;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.battle.DamageTag;
import com.mygdx.hadal.strategies.HitboxStrategy;
import com.mygdx.hadal.strategies.hitbox.*;

/**
 * @author Midgestein Midgegette
 */
public class Fafrotskies extends ActiveItem {

	private static final float MAX_CHARGE = 15.0f;
	
	private static final Vector2 PROJECTILE_SIZE = new Vector2(200, 50);
	private static final Vector2 RAIN_SIZE = new Vector2(30, 20);
	private static final float LIFESPAN = 7.5f;
	private static final float RAIN_DAMAGE = 11.0f;
	private static final float RAIN_KNOCKBACK = 2.0f;
	private static final float PROJECTILE_SPEED = 5.0f;
	private static final float RAIN_SPEED = 15.0f;
	private static final float RAIN_INTERVAL = 0.1f;

	private static final Sprite PROJ_SPRITE = Sprite.ORB_BLUE;

	public Fafrotskies(Schmuck user) {
		super(user, MAX_CHARGE);
	}
	
	@Override
	public void useItem(PlayState state, PlayerBodyData user) {
		SoundEffect.THUNDER.playUniversal(state, user.getPlayer().getPixelPosition(), 1.0f, false);

		Hitbox hbox = new RangedHitbox(state, user.getPlayer().getPixelPosition(), PROJECTILE_SIZE, LIFESPAN, this.weaponVelo.scl(PROJECTILE_SPEED), (short) 0, false, false, user.getPlayer(), PROJ_SPRITE);
		
		hbox.setGravity(-0.2f);
		
		hbox.addStrategy(new ControllerDefault(state, hbox, user));
		hbox.addStrategy(new HitboxStrategy(state, hbox, user) {
			
			private float controllerCount;
			@Override
			public void controller(float delta) {
				controllerCount += delta;

				while (controllerCount >= RAIN_INTERVAL) {
					controllerCount -= RAIN_INTERVAL;
					
					Hitbox rain = new Hitbox(state, new Vector2(hbox.getPixelPosition()).add((MathUtils.random() -  0.5f) * hbox.getSize().x, 0), RAIN_SIZE, LIFESPAN, new Vector2(0, -RAIN_SPEED),
							user.getPlayer().getHitboxfilter(), true, false, user.getPlayer(), Sprite.SPIT);
					
					rain.addStrategy(new ControllerDefault(state, rain, user));
					rain.addStrategy(new DamageStandard(state, rain, user, RAIN_DAMAGE, RAIN_KNOCKBACK, DamageSource.FAFROTSKIES,
							DamageTag.WATER));
					rain.addStrategy(new AdjustAngle(state, rain, user));
					rain.addStrategy(new ContactWallParticles(state, rain, user, Particle.BUBBLE_IMPACT));
					rain.addStrategy(new ContactUnitLoseDurability(state, rain, user));
					rain.addStrategy(new ContactWallDie(state, rain, user));
				}
			}
		});
	}

	@Override
	public String[] getDescFields() {
		return new String[] {
				String.valueOf((int) MAX_CHARGE),
				String.valueOf((int) RAIN_DAMAGE),
				String.valueOf(LIFESPAN),
				String.valueOf(RAIN_INTERVAL)};
	}
}
