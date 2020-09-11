package com.mygdx.hadal.equip.ranged;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.ParticleColor;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.RangedWeapon;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.schmucks.bodies.hitboxes.RangedHitbox;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;
import com.mygdx.hadal.strategies.HitboxStrategy;
import com.mygdx.hadal.strategies.hitbox.AdjustAngle;
import com.mygdx.hadal.strategies.hitbox.ContactUnitLoseDurability;
import com.mygdx.hadal.strategies.hitbox.ContactUnitParticles;
import com.mygdx.hadal.strategies.hitbox.ContactUnitSound;
import com.mygdx.hadal.strategies.hitbox.ContactWallDie;
import com.mygdx.hadal.strategies.hitbox.ContactWallParticles;
import com.mygdx.hadal.strategies.hitbox.ControllerDefault;
import com.mygdx.hadal.strategies.hitbox.DamageStandard;

public class Peppergrinder extends RangedWeapon {

	private final static int clipSize = 60;
	private final static int ammoSize = 360;
	private final static float shootCd = 0.06f;
	private final static float shootDelay = 0;
	private final static float reloadTime = 1.5f;
	private final static int reloadAmount = 0;
	private final static float baseDamage = 15.0f;
	private final static float recoil = 2.5f;
	private final static float knockback = 10.0f;
	private final static float projectileSpeed = 20.0f;
	private final static Vector2 projectileSize = new Vector2(40, 20);
	private final static float lifespan = 4.0f;
	
	private final static Sprite projSprite = Sprite.LASER_GREEN;
	private final static Sprite weaponSprite = Sprite.MT_BOILER;
	private final static Sprite eventSprite = Sprite.P_BOILER;
	
	private final static int maxSpread = 24;
	private final static int spreadChange = 8;

	public Peppergrinder(Schmuck user) {
		super(user, clipSize, ammoSize, reloadTime, recoil, projectileSpeed, shootCd, shootDelay, reloadAmount, true, weaponSprite, eventSprite, projectileSize.x);
	}
	
	private int spread;
	private boolean sweepingUp;
	@Override
	public void fire(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity, short filter) {
		SoundEffect.LASER2.playUniversal(state, user.getPixelPosition(), 0.25f, false);

		RangedHitbox hbox = new RangedHitbox(state, startPosition, projectileSize, lifespan, startVelocity, filter, true, true, user, projSprite);
		
		hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));
		hbox.addStrategy(new AdjustAngle(state, hbox, user.getBodyData()));
		hbox.addStrategy(new DamageStandard(state, hbox, user.getBodyData(), baseDamage, knockback, DamageTypes.ENERGY, DamageTypes.RANGED));
		hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));
		hbox.addStrategy(new ContactWallParticles(state, hbox, user.getBodyData(), Particle.LASER_IMPACT).setOffset(true).setParticleColor(ParticleColor.PALE_GREEN));
		hbox.addStrategy(new ContactUnitParticles(state, hbox, user.getBodyData(), Particle.LASER_IMPACT).setOffset(true).setParticleColor(ParticleColor.PALE_GREEN));
		hbox.addStrategy(new ContactWallDie(state, hbox, user.getBodyData()));
		hbox.addStrategy(new ContactUnitSound(state, hbox, user.getBodyData(), SoundEffect.MAGIC0_DAMAGE, 0.25f, true));
		hbox.addStrategy(new ContactUnitLoseDurability(state, hbox, user.getBodyData()));
		hbox.addStrategy(new HitboxStrategy(state, hbox, user.getBodyData()) {
			
			@Override
			public void create() {
				float newDegrees = (float) (hbox.getStartVelo().angle() + spread);
				hbox.setLinearVelocity(hbox.getLinearVelocity().setAngle(newDegrees));
			}
		});
		
		if (sweepingUp) {
			spread += spreadChange;
			if (spread >= maxSpread) {
				sweepingUp = false;
			}
		} else {
			spread -= spreadChange;
			if (spread <= -maxSpread) {
				sweepingUp = true;
			}
		}
	}
}
