package com.mygdx.hadal.equip.ranged;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.ParticleColor;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.RangedWeapon;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.bodies.hitboxes.RangedHitbox;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;
import com.mygdx.hadal.strategies.HitboxStrategy;
import com.mygdx.hadal.strategies.hitbox.ContactUnitLoseDurability;
import com.mygdx.hadal.strategies.hitbox.ContactUnitParticles;
import com.mygdx.hadal.strategies.hitbox.ContactWallDie;
import com.mygdx.hadal.strategies.hitbox.ContactWallParticles;
import com.mygdx.hadal.strategies.hitbox.ControllerDefault;
import com.mygdx.hadal.strategies.hitbox.DamageStandard;

public class WaveBeam extends RangedWeapon {

	private final static int clipSize = 5;
	private final static int ammoSize = 25;
	private final static float shootCd = 0.3f;
	private final static float shootDelay = 0;
	private final static float reloadTime = 1.5f;
	private final static int reloadAmount = 0;
	private final static float baseDamage = 35.0f;
	private final static float recoil = 12.5f;
	private final static float knockback = 28.0f;
	private final static float projectileSpeed = 40.0f;
	private final static Vector2 projectileSize = new Vector2(60, 30);
	private final static float lifespan = 0.75f;
	
	private final static Sprite projSprite = Sprite.LASER_BLUE;
	private final static Sprite weaponSprite = Sprite.MT_DEFAULT;
	private final static Sprite eventSprite = Sprite.P_DEFAULT;
	
	private final static float amplitude = 1.0f;
	private final static float frequency = 25.0f;
	
	public WaveBeam(Schmuck user) {
		super(user, clipSize, ammoSize, reloadTime, recoil, projectileSpeed, shootCd, shootDelay, reloadAmount, true, weaponSprite, eventSprite, projectileSize.x);
	}
	
	@Override
	public void fire(PlayState state, Schmuck user, Vector2 startPosition, final Vector2 startVelocity, short filter) {
		SoundEffect.SHOOT1.playUniversal(state, startPosition, 0.6f, false);

		//we create an invisible hitbox that moves in a straight line.
		Hitbox center = new RangedHitbox(state, startPosition, projectileSize, lifespan, startVelocity, filter, true, true, user, Sprite.NOTHING);
		center.setSyncDefault(false);
		center.setEffectsHit(false);
		center.setEffectsVisual(false);

		center.addStrategy(new ControllerDefault(state, center, user.getBodyData()));
		center.addStrategy(new ContactWallDie(state, center, user.getBodyData()));
		center.addStrategy(new HitboxStrategy(state, center, user.getBodyData()) {
			
			
			@Override
			public void create() {
				
				Hitbox hbox = new RangedHitbox(state, startPosition, projectileSize, lifespan, startVelocity, filter, true, true, user, projSprite);
				hbox.setSyncDefault(false);
				hbox.setSyncInstant(true);
				hbox.setEffectsMovement(false);

				hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));
				hbox.addStrategy(new ContactWallDie(state, hbox, user.getBodyData()));
				hbox.addStrategy(new ContactUnitLoseDurability(state, hbox, user.getBodyData()));
				hbox.addStrategy(new DamageStandard(state, hbox, user.getBodyData(), baseDamage, knockback, DamageTypes.ENERGY, DamageTypes.RANGED));
				hbox.addStrategy(new ContactWallParticles(state, hbox, user.getBodyData(), Particle.LASER_IMPACT).setOffset(true).setParticleColor(ParticleColor.BLUE));
				hbox.addStrategy(new ContactUnitParticles(state, hbox, user.getBodyData(), Particle.LASER_IMPACT).setOffset(true).setParticleColor(ParticleColor.BLUE));
				
				hbox.addStrategy(new HitboxStrategy(state, hbox, user.getBodyData()) {
					
					private float timer;
					private Vector2 lastPos = new Vector2();
					private Vector2 centerPos = new Vector2();
					private Vector2 offset = new Vector2();
					
					@Override
					public void controller(float delta) {
						if (center.getBody() != null && center.isAlive()) {
							timer += delta;
							offset.set(0, (float) (amplitude * Math.sin(timer * frequency))).setAngle(hbox.getLinearVelocity().angle() + 90);
							
							centerPos.set(center.getPosition()).add(offset);
							hbox.setTransform(centerPos, lastPos.sub(centerPos).angleRad());
							lastPos.set(centerPos);
						} else {
							hbox.die();
						}
					}
				});
				
				Hitbox hbox2 = new RangedHitbox(state, startPosition, projectileSize, lifespan, startVelocity, filter, true, true, user, projSprite);
				hbox2.setSyncDefault(false);
				hbox2.setSyncInstant(true);
				hbox2.setEffectsMovement(false);
				
				hbox2.addStrategy(new ControllerDefault(state, hbox2, user.getBodyData()));
				hbox2.addStrategy(new ContactWallDie(state, hbox2, user.getBodyData()));
				hbox2.addStrategy(new ContactUnitLoseDurability(state, hbox2, user.getBodyData()));
				hbox2.addStrategy(new DamageStandard(state, hbox2, user.getBodyData(), baseDamage, knockback, DamageTypes.ENERGY, DamageTypes.RANGED));
				hbox2.addStrategy(new ContactWallParticles(state, hbox2, user.getBodyData(), Particle.LASER_IMPACT).setOffset(true).setParticleColor(ParticleColor.BLUE));
				hbox2.addStrategy(new ContactUnitParticles(state, hbox2, user.getBodyData(), Particle.LASER_IMPACT).setOffset(true).setParticleColor(ParticleColor.BLUE));
				
				hbox2.addStrategy(new HitboxStrategy(state, hbox2, user.getBodyData()) {
					
					private float timer;
					private Vector2 lastPos = new Vector2();
					private Vector2 centerPos = new Vector2();
					private Vector2 offset = new Vector2();
					
					@Override
					public void controller(float delta) {
						if (center.getBody() != null && center.isAlive()) {
							timer += delta;
							offset.set(0, (float) (amplitude * Math.sin(timer * frequency))).setAngle(hbox.getLinearVelocity().angle() - 90);
							
							centerPos.set(center.getPosition()).add(offset);
							hbox.setTransform(centerPos, lastPos.sub(centerPos).angleRad());
							lastPos.set(centerPos);
						} else {
							hbox.die();
						}
					}
				});
			}
		});
	}
}