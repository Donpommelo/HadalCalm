package com.mygdx.hadal.equip.actives;

import static com.mygdx.hadal.utils.Constants.PPM;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.RayCastCallback;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.ActiveItem;
import com.mygdx.hadal.managers.GameStateManager;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;
import com.mygdx.hadal.statuses.Status;
import com.mygdx.hadal.strategies.HitboxStrategy;
import com.mygdx.hadal.strategies.hitbox.ControllerDefault;
import com.mygdx.hadal.strategies.hitbox.CreateParticles;
import com.mygdx.hadal.strategies.hitbox.CreateSound;
import com.mygdx.hadal.strategies.hitbox.DamageStandard;
import com.mygdx.hadal.utils.Constants;

public class MeteorStrike extends ActiveItem {

	private final static float usecd = 0.0f;
	private final static float usedelay = 0.1f;
	private final static float maxCharge = 25.0f;

	private final static Vector2 projectileSize = new Vector2(50, 50);
	private final static float lifespan = 5.0f;
	private final static float projectileSpeed = 50.0f;

	private final static float range = 1800.0f;
	
	private final static float baseDamage = 24.0f;
	private final static float knockback = 6.0f;
	
	private final static float meteorDuration = 3.0f;
	private final static float meteorInterval = 0.1f;
	private final static float spread = 500.0f;
	
	public MeteorStrike(Schmuck user) {
		super(user, usecd, usedelay, maxCharge, chargeStyle.byTime);
	}
	
	private float shortestFraction;
	private Vector2 originPt = new Vector2();
	private Vector2 endPt = new Vector2();
	@Override
	public void useItem(PlayState state, final PlayerBodyData user) {
		originPt.set(this.mouseLocation).scl(1 / PPM);
		endPt.set(originPt).add(0, -range);
		shortestFraction = 1.0f;
		
		if (originPt.x != endPt.x || originPt.y != endPt.y) {

			state.getWorld().rayCast(new RayCastCallback() {

				@Override
				public float reportRayFixture(Fixture fixture, Vector2 point, Vector2 normal, float fraction) {
					if (fixture.getFilterData().categoryBits == (short) Constants.BIT_WALL && fraction < shortestFraction) {
						shortestFraction = fraction;
						return fraction;
				}
				return -1.0f;
				}
			}, originPt, endPt);
		}
		
		endPt.set(originPt).add(0, -range * shortestFraction).scl(PPM);
		originPt.set(endPt).add(0, range);
		
		user.addStatus(new Status(state, meteorDuration, false, user, user) {
			
			private float procCdCount;
			private float meteorCount;
			
			@Override
			public void timePassing(float delta) {
				super.timePassing(delta);
				
				if (procCdCount >= meteorInterval) {
					procCdCount -= meteorInterval;

					meteorCount++;
					
					Hitbox hbox = new Hitbox(state, new Vector2(originPt).add((GameStateManager.generator.nextFloat() -  0.5f) * spread, 0), projectileSize, lifespan, new Vector2(0, -projectileSpeed),
							user.getPlayer().getHitboxfilter(), true, false, user.getPlayer(), Sprite.NOTHING);
					
					hbox.addStrategy(new ControllerDefault(state, hbox, user));
					hbox.addStrategy(new DamageStandard(state, hbox, user, baseDamage, knockback, DamageTypes.FIRE, DamageTypes.MAGIC));
					hbox.addStrategy(new HitboxStrategy(state, hbox, user) {
						
						@Override
						public void controller(float delta) {
							if (hbox.getPixelPosition().y - hbox.getSize().y / 2 <= endPt.y) {
								hbox.setLinearVelocity(0, 0);
								hbox.die();
							}
						}
					});
					
					hbox.addStrategy(new CreateParticles(state, hbox, user, Particle.FIRE, 0.0f, 3.0f));
					
					if (meteorCount % 5 == 0) {
						hbox.addStrategy(new CreateSound(state, hbox, user, SoundEffect.FALLING, 0.5f, false));
					}
				}
				procCdCount += delta;
			}
		});
	}
	
	@Override
	public float getUseDuration() { return lifespan; }
}
