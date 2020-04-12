package com.mygdx.hadal.equip.ranged;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.RangedWeapon;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.schmucks.bodies.SoundEntity;
import com.mygdx.hadal.schmucks.bodies.SoundEntity.soundSyncType;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.bodies.hitboxes.RangedHitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;
import com.mygdx.hadal.statuses.Slodged;
import com.mygdx.hadal.strategies.hitbox.AdjustAngle;
import com.mygdx.hadal.strategies.hitbox.ContactUnitLoseDurability;
import com.mygdx.hadal.strategies.hitbox.ContactWallDie;
import com.mygdx.hadal.strategies.hitbox.ContactWallParticles;
import com.mygdx.hadal.strategies.hitbox.ControllerDefault;
import com.mygdx.hadal.strategies.hitbox.DamageStandard;
import com.mygdx.hadal.strategies.hitbox.Spread;

public class Minigun extends RangedWeapon {

	private final static int clipSize = 200;
	private final static int ammoSize = 600;
	private final static float shootCd = 0.08f;
	private final static float shootDelay = 0;
	private final static float reloadTime = 2.0f;
	private final static int reloadAmount = 0;
	private final static float baseDamage = 14.0f;
	private final static float recoil = 0.25f;
	private final static float knockback = 6.0f;
	private final static float projectileSpeed = 35.0f;
	private final static Vector2 projectileSize = new Vector2(40, 10);
	private final static float lifespan = 1.5f;
	
	private final static int spread = 8;

	private final static Sprite projSprite = Sprite.BULLET;
	private final static Sprite weaponSprite = Sprite.MT_DEFAULT;
	private final static Sprite eventSprite = Sprite.P_DEFAULT;
	
	private static final float maxCharge = 0.5f;
	private static final float selfSlowDura = 0.1f;
	private static final float selfSlowMag = 0.75f;
	
	private SoundEntity fireSound;
	
	public Minigun(Schmuck user) {
		super(user, clipSize, ammoSize, reloadTime, recoil, projectileSpeed, shootCd, shootDelay, reloadAmount, true, weaponSprite, eventSprite, projectileSize.x, maxCharge);
	}
	
	@Override
	public void mouseClicked(float delta, PlayState state, BodyData shooter, short faction, Vector2 mouseLocation) {
		super.mouseClicked(delta, state, shooter, faction, mouseLocation);		

		if (reloading || getClipLeft() == 0) {
			if (fireSound != null) {
				fireSound.turnOff();
			}
			return;
		}
		
		charging = true;
		
		if (chargeCd == 0) {
			SoundEffect.MINIGUN_UP.playUniversal(state, user.getPixelPosition(), 0.4f, false);
		}
		
		//while held, build charge until maximum (if not reloading) User is slowed while shooting.
		if (chargeCd < getChargeTime()) {
			chargeCd += (delta + shootCd);
		}
		
		if (chargeCd >= getChargeTime()) {
			if (fireSound == null) {
				fireSound = new SoundEntity(state, user, SoundEffect.MINIGUN_LOOP, 0.4f, true, true, soundSyncType.TICKSYNC);
			} else {
				fireSound.turnOn();
			}
		}
		
		shooter.addStatus(new Slodged(state, selfSlowDura, selfSlowMag, shooter, shooter));
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
		SoundEffect.MINIGUN_DOWN.playUniversal(state, user.getPixelPosition(), 0.6f, false);
		chargeCd = 0;
		charging = false;
		
		if (fireSound != null) {
			fireSound.turnOff();
		}
	}
	
	@Override
	public void fire(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity, short filter) {

		Hitbox hbox = new RangedHitbox(state, startPosition, projectileSize, lifespan, startVelocity, filter, true, true, user, projSprite);
		hbox.setGravity(1.0f);
		
		hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));
		hbox.addStrategy(new AdjustAngle(state, hbox, user.getBodyData()));
		hbox.addStrategy(new ContactWallParticles(state, hbox, user.getBodyData(), Particle.SPARK_TRAIL));
		hbox.addStrategy(new ContactUnitLoseDurability(state, hbox, user.getBodyData()));
		hbox.addStrategy(new ContactWallDie(state, hbox, user.getBodyData()));
		hbox.addStrategy(new DamageStandard(state, hbox, user.getBodyData(), baseDamage, knockback, DamageTypes.BULLET, DamageTypes.RANGED));	
		hbox.addStrategy(new Spread(state, hbox, user.getBodyData(), spread));
	}
	
	@Override
	public void unequip() {
		if (fireSound != null) {
			fireSound.terminate();
			fireSound = null;
		}
	}
}
