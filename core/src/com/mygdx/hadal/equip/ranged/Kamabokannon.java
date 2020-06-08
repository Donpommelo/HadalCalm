package com.mygdx.hadal.equip.ranged;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.RangedWeapon;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.schmucks.bodies.hitboxes.RangedHitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;
import com.mygdx.hadal.strategies.hitbox.AdjustAngle;
import com.mygdx.hadal.strategies.hitbox.ContactUnitLoseDurability;
import com.mygdx.hadal.strategies.hitbox.ContactWallDie;
import com.mygdx.hadal.strategies.hitbox.ControllerDefault;
import com.mygdx.hadal.strategies.hitbox.CreateParticles;
import com.mygdx.hadal.strategies.hitbox.DamageStandard;
import com.mygdx.hadal.strategies.hitbox.DieParticles;

public class Kamabokannon extends RangedWeapon {

	private final static int clipSize = 100;
	private final static int ammoSize = 500;
	private final static float shootCd = 0.08f;
	private final static float shootDelay = 0;
	private final static float reloadTime = 1.8f;
	private final static int reloadAmount = 0;
	private final static float baseDamage = 13.0f;
	private final static float recoil = 4.0f;
	private final static float knockback = 6.0f;
	private final static float projectileSpeed = 20.0f;
	private final static Vector2 projectileSize = new Vector2(100, 50);
	private final static float lifespan = 1.6f;
	
	private final static Sprite weaponSprite = Sprite.MT_DEFAULT;
	private final static Sprite eventSprite = Sprite.P_DEFAULT;
	
	private static final float maxCharge = 0.3f;
	private final static float lerpSpeed = 0.2f;

	public Kamabokannon(Schmuck user) {
		super(user, clipSize, ammoSize, reloadTime, recoil, projectileSpeed, shootCd, shootDelay, reloadAmount, true, weaponSprite, eventSprite, projectileSize.x, maxCharge);
	}
	
	private float controllerCount = 0;
	private final static float pushInterval = 1 / 60f;
	private Vector2 aimPointer = new Vector2();
	@Override
	public void mouseClicked(float delta, PlayState state, BodyData shooter, short faction, Vector2 mouseLocation) {
		
		controllerCount += delta;

		//while held, lerp towards mouse pointer
		while (controllerCount >= pushInterval) {
			controllerCount -= pushInterval;
			
			super.mouseClicked(delta, state, shooter, faction, mouseLocation);
			weaponVelo.setAngle(MathUtils.lerpAngleDeg(aimPointer.angle(), weaponVelo.angle(), lerpSpeed));
			aimPointer.set(weaponVelo);
		}
		
		charging = true;
		
		//while held, build charge until maximum (if not reloading)
		if (chargeCd < getChargeTime()) {
			chargeCd += (delta + shootCd);
			
			if (chargeCd >= getChargeTime()) {
				super.mouseClicked(delta, state, shooter, faction, mouseLocation);
				aimPointer.set(weaponVelo);
			}
		}
	}
	
	@Override
	public void execute(PlayState state, BodyData shooter) {
		if (chargeCd >= getChargeTime()) {
			chargeCd = getChargeTime();
			super.execute(state, shooter);
		}
	}
	
	@Override
	public void release(PlayState state, BodyData bodyData) {
		chargeCd = 0;
		charging = false;
	}
	
	@Override
	public void fire(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity, short filter) {
		RangedHitbox hbox = new RangedHitbox(state, startPosition, projectileSize, lifespan, startVelocity, filter, true, true, user, Sprite.NOTHING);
		hbox.setGravity(1.0f);
		
		hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));
		hbox.addStrategy(new AdjustAngle(state, hbox, user.getBodyData()));
		hbox.addStrategy(new ContactUnitLoseDurability(state, hbox, user.getBodyData()));
		hbox.addStrategy(new ContactWallDie(state, hbox, user.getBodyData()));
		hbox.addStrategy(new DamageStandard(state, hbox, user.getBodyData(), baseDamage, knockback, DamageTypes.RANGED));
		hbox.addStrategy(new CreateParticles(state, hbox, user.getBodyData(), Particle.KAMABOKO_SHOWER, 0.0f, 3.0f));
		hbox.addStrategy(new DieParticles(state, hbox, user.getBodyData(), Particle.KAMABOKO_IMPACT));
	}
}
