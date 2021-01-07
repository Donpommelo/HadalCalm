package com.mygdx.hadal.equip.ranged;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.RangedWeapon;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.schmucks.bodies.SoundEntity;
import com.mygdx.hadal.schmucks.bodies.SoundEntity.soundSyncType;
import com.mygdx.hadal.schmucks.bodies.hitboxes.RangedHitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;
import com.mygdx.hadal.strategies.hitbox.AdjustAngle;
import com.mygdx.hadal.strategies.hitbox.ContactUnitBurn;
import com.mygdx.hadal.strategies.hitbox.ControllerDefault;
import com.mygdx.hadal.strategies.hitbox.CreateParticles;
import com.mygdx.hadal.strategies.hitbox.DamageStandard;

public class Boiler extends RangedWeapon {

	private static final int clipSize = 90;
	private static final int ammoSize = 270;
	private static final float shootCd = 0.04f;
	private static final float shootDelay = 0;
	private static final float reloadTime = 1.5f;
	private static final int reloadAmount = 0;
	private static final float baseDamage = 5.5f;
	private static final float recoil = 1.5f;
	private static final float knockback = 2.0f;
	private static final float projectileSpeed = 28.0f;
	private static final Vector2 projectileSize = new Vector2(100, 50);
	private static final float lifespan = 0.5f;
	
	private static final float fireDuration = 5.0f;
	private static final float fireDamage = 3.0f;
	
	private static final Sprite weaponSprite = Sprite.MT_BOILER;
	private static final Sprite eventSprite = Sprite.P_BOILER;
	
	private SoundEntity fireSound;
	
	public Boiler(Schmuck user) {
		super(user, clipSize, ammoSize, reloadTime, recoil, projectileSpeed, shootCd, shootDelay, reloadAmount, true, weaponSprite, eventSprite, projectileSize.x);
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
		
		if (fireSound == null) {
			fireSound = new SoundEntity(state, user, SoundEffect.FLAMETHROWER, 0.8f, 1.0f, true, true, soundSyncType.TICKSYNC);
		} else {
			fireSound.turnOn();
		}
	}
	
	@Override
	public void fire(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity, short filter) {
		RangedHitbox hbox = new RangedHitbox(state, startPosition, projectileSize, lifespan, startVelocity, filter, false, true, user, Sprite.NOTHING);
		hbox.setDurability(3);
		
		hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));
		hbox.addStrategy(new AdjustAngle(state, hbox, user.getBodyData()));
		hbox.addStrategy(new ContactUnitBurn(state, hbox, user.getBodyData(), fireDuration, fireDamage));
		hbox.addStrategy(new DamageStandard(state, hbox, user.getBodyData(), baseDamage, knockback, DamageTypes.FIRE, DamageTypes.RANGED));
		hbox.addStrategy(new CreateParticles(state, hbox, user.getBodyData(), Particle.FIRE, 0.0f, 1.0f));
	}
	
	@Override
	public void release(PlayState state, BodyData bodyData) {
		if (fireSound != null) {
			fireSound.turnOff();
		}
	}
	
	@Override
	public void unequip(PlayState state) {
		if (fireSound != null) {
			fireSound.terminate();
			fireSound = null;
		}
	}
}
