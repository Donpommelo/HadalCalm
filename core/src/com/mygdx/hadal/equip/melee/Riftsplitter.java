package com.mygdx.hadal.equip.melee;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.ParticleColor;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.MeleeWeapon;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.bodies.hitboxes.RangedHitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;
import com.mygdx.hadal.strategies.HitboxStrategy;
import com.mygdx.hadal.strategies.hitbox.AdjustAngle;
import com.mygdx.hadal.strategies.hitbox.ContactUnitParticles;
import com.mygdx.hadal.strategies.hitbox.ContactWallDie;
import com.mygdx.hadal.strategies.hitbox.ContactWallParticles;
import com.mygdx.hadal.strategies.hitbox.ControllerDefault;
import com.mygdx.hadal.strategies.hitbox.DamageStandard;

public class Riftsplitter extends MeleeWeapon {

	private final static float shootCd = 0.5f;
	private final static float shootDelay = 0.3f;
	private final static float baseDamage = 20.0f;
	private final static Vector2 projectileSize = new Vector2(30, 120);
	private final static float projectileSpeed = 30.0f;
	private final static float knockback = 15.0f;
	private final static float lifespan = 0.4f;
	
	private final static Vector2 shockwaveSize = new Vector2(56, 64);
	private final static float shockwaveInterval = 0.1f;
	private final static float shockwaveDamage = 15.0f;
	private final static float shockwaveSpeed = 15.0f;
	private final static float shockwaveLifespan = 0.4f;

	private final static Sprite projSprite = Sprite.SPLITTER_A;
	private final static Sprite weaponSprite = Sprite.MT_SCRAPRIPPER;
	private final static Sprite eventSprite = Sprite.P_SCRAPRIPPER;

	public Riftsplitter(Schmuck user) {
		super(user, shootCd, shootDelay, weaponSprite, eventSprite);
	}
	
	@Override
	public void mouseClicked(float delta, PlayState state, BodyData shooter, short faction, Vector2 mouseLocation) {
		super.mouseClicked(delta, state, shooter, faction, mouseLocation);
		SoundEffect.WOOSH.playUniversal(state, shooter.getSchmuck().getPixelPosition(), 1.0f, false);
	}
	
	private Vector2 startVelo = new Vector2();
	@Override
	public void fire(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity, short filter) {
		SoundEffect.METAL_IMPACT_1.playUniversal(state, startPosition, 0.4f, false);

		Hitbox hbox = new RangedHitbox(state, user.getProjectileOrigin(weaponVelo, projectileSize.x), projectileSize, lifespan, startVelo.set(startVelocity).nor().scl(projectileSpeed), filter, false, true, user, projSprite);
		hbox.setRestitution(1.0f);
		
		hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));
		hbox.addStrategy(new AdjustAngle(state, hbox, user.getBodyData()));
		hbox.addStrategy(new DamageStandard(state, hbox, user.getBodyData(), baseDamage, knockback, DamageTypes.MELEE, DamageTypes.CUTTING));
		hbox.addStrategy(new ContactWallParticles(state, hbox, user.getBodyData(), Particle.LASER_IMPACT).setOffset(true).setParticleColor(ParticleColor.TURQOISE));
		hbox.addStrategy(new ContactUnitParticles(state, hbox, user.getBodyData(), Particle.LASER_IMPACT).setOffset(true).setParticleColor(ParticleColor.TURQOISE));
		
		hbox.addStrategy(new HitboxStrategy(state, hbox, user.getBodyData()) {
			
			private float controllerCount = shockwaveInterval;

			@Override
			public void controller(float delta) {
				controllerCount += delta;

				//projectile repeatedly creates perpdicular projectiles as it moves in a straight line
				while (controllerCount >= shockwaveInterval) {
					controllerCount -= shockwaveInterval;
					createShockwave(0);
					createShockwave(-1);
				}
			}
			
			private void createShockwave(int rotate) {
				Hitbox shockwave = new RangedHitbox(state, hbox.getPixelPosition(), shockwaveSize, shockwaveLifespan, 
						new Vector2(hbox.getLinearVelocity()).rotate90(rotate).nor().scl(shockwaveSpeed), filter, true, true, user, Sprite.SPLITTER_B);
				shockwave.addStrategy(new ControllerDefault(state, shockwave, user.getBodyData()));
				shockwave.addStrategy(new AdjustAngle(state, shockwave, user.getBodyData()));
				shockwave.addStrategy(new ContactWallDie(state, shockwave, user.getBodyData()));
				shockwave.addStrategy(new DamageStandard(state, shockwave, user.getBodyData(), shockwaveDamage, knockback, DamageTypes.MELEE, DamageTypes.CUTTING));
				shockwave.addStrategy(new ContactWallParticles(state, shockwave, user.getBodyData(), Particle.LASER_IMPACT).setOffset(true).setParticleColor(ParticleColor.TURQOISE));
				shockwave.addStrategy(new ContactUnitParticles(state, shockwave, user.getBodyData(), Particle.LASER_IMPACT).setOffset(true).setParticleColor(ParticleColor.TURQOISE));
			}
		});
	}
}
