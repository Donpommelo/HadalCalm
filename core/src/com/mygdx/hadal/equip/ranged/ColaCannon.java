package com.mygdx.hadal.equip.ranged;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.RangedWeapon;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.bodies.hitboxes.RangedHitbox;
import com.mygdx.hadal.schmucks.strategies.HitboxDamageStandardStrategy;
import com.mygdx.hadal.schmucks.strategies.HitboxDefaultStrategy;
import com.mygdx.hadal.schmucks.strategies.HitboxOnContactUnitLoseDuraStrategy;
import com.mygdx.hadal.schmucks.strategies.HitboxOnContactWallDieStrategy;
import com.mygdx.hadal.schmucks.strategies.HitboxOnDieParticles;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;
import com.mygdx.hadal.statuses.FiringWeapon;

public class ColaCannon extends RangedWeapon {

	private final static String name = "Cola Cannon";
	private final static int clipSize = 1;
	private final static int ammoSize = 12;
	private final static float shootCd = 0.0f;
	private final static float shootDelay = 0.0f;
	private final static float reloadTime = 1.8f;
	private final static int reloadAmount = 0;
	private final static float baseDamage = 4.5f;
	private final static float recoil = 18.0f;
	private final static float knockback = 5.5f;
	private final static float projectileSpeed = 45.0f;
	private final static Vector2 projectileSize = new Vector2(30, 30);
	private final static float lifespan = 1.0f;

	private final static float procCd = .05f;
	private final static float fireDuration = 1.6f;
	private final static float veloDeprec = 1.0f;
	private final static float minVelo = 9.0f;
	private final static float minDuration = 0.5f;

	private final static Sprite projSprite = Sprite.SPIT;
	private final static Sprite weaponSprite = Sprite.MT_DEFAULT;
	private final static Sprite eventSprite = Sprite.P_DEFAULT;
	
	private final static float maxCharge = 6400.0f;

	private Vector2 lastMouse = new Vector2();
	
	public ColaCannon(Schmuck user) {
		super(user, name, clipSize, ammoSize, reloadTime, recoil, projectileSpeed, shootCd, shootDelay, reloadAmount, true, weaponSprite, eventSprite, projectileSize.x, maxCharge);
	}
	
	@Override
	public void mouseClicked(float delta, PlayState state, BodyData shooter, short faction, Vector2 mouseLocation) {
		charging = true;
		if (chargeCd < getChargeTime() && !reloading) {
			chargeCd += lastMouse.dst(mouseLocation);
			if (chargeCd >= getChargeTime()) {
				chargeCd = getChargeTime();
			}
		}
		
		lastMouse.set(mouseLocation);
		super.mouseClicked(delta, state, shooter, faction, mouseLocation);
	}
	
	@Override
	public void execute(PlayState state, BodyData shooter) {}
	
	@Override
	public void release(PlayState state, BodyData bodyData) {
		if (processClip(state, bodyData)) {
			final float duration = fireDuration * chargeCd / getChargeTime() + minDuration;
			final float velocity = projectileSpeed * chargeCd / getChargeTime() + minVelo;
			
			bodyData.addStatus(new FiringWeapon(state, duration, bodyData, bodyData, velocity, minVelo, veloDeprec, projectileSize.x, procCd, this));
			
			charging = false;
			chargeCd = 0;
		}
	}
	
	@Override
	public void fire(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity, short filter) {
		
		Hitbox hbox = new RangedHitbox(state, startPosition, projectileSize, lifespan, startVelocity, filter, true, true, user, projSprite);
		hbox.setGravity(1.0f);
		
		hbox.addStrategy(new HitboxDefaultStrategy(state, hbox, user.getBodyData()));
		hbox.addStrategy(new HitboxOnContactWallDieStrategy(state, hbox, user.getBodyData()));
		hbox.addStrategy(new HitboxOnContactUnitLoseDuraStrategy(state, hbox, user.getBodyData()));
		hbox.addStrategy(new HitboxOnDieParticles(state, hbox, user.getBodyData(), Particle.BUBBLE_IMPACT));
		hbox.addStrategy(new HitboxDamageStandardStrategy(state, hbox, user.getBodyData(), this, baseDamage, knockback, DamageTypes.RANGED));
	}
}
