package com.mygdx.hadal.equip.melee;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.effects.Particle;
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
import com.mygdx.hadal.strategies.hitbox.ContactWallDie;
import com.mygdx.hadal.strategies.hitbox.ControllerDefault;
import com.mygdx.hadal.strategies.hitbox.CreateParticles;
import com.mygdx.hadal.strategies.hitbox.DamageStandard;

public class Riftsplitter extends MeleeWeapon {

	private final static float shootCd = 0.5f;
	private final static float shootDelay = 0.3f;
	private final static float baseDamage = 20.0f;
	private final static Vector2 projectileSize = new Vector2(20, 80);
	private final static float projectileSpeed = 30.0f;
	private final static float knockback = 15.0f;
	private final static float lifespan = 0.4f;
	
	private final static Vector2 shockwaveSize = new Vector2(60, 60);
	private final static float shockwaveInterval = 0.1f;
	private final static float shockwaveDamage = 15.0f;
	private final static float shockwaveSpeed = 10.0f;
	private final static float shockwaveLifespan = 0.4f;

	private final static Sprite projSprite = Sprite.ORB_YELLOW;
	private final static Sprite weaponSprite = Sprite.MT_SCRAPRIPPER;
	private final static Sprite eventSprite = Sprite.P_SCRAPRIPPER;

	public Riftsplitter(Schmuck user) {
		super(user, shootCd, shootDelay, weaponSprite, eventSprite);
	}
	
	@Override
	public void mouseClicked(float delta, PlayState state, BodyData shooter, short faction, Vector2 mouseLocation) {
		super.mouseClicked(delta, state, shooter, faction, mouseLocation);
		SoundEffect.WOOSH.playUniversal(state, shooter.getSchmuck().getBody().getPosition(), 1.0f, false);
	}
	
	@Override
	public void fire(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity, short filter) {
		SoundEffect.METAL_IMPACT_1.playUniversal(state, startPosition, 0.5f, false);

		Hitbox hbox = new RangedHitbox(state, user.getProjectileOrigin(weaponVelo, projectileSize.x), projectileSize, lifespan, new Vector2(startVelocity).nor().scl(projectileSpeed), filter, false, true, user, projSprite);
		hbox.setRestitution(1.0f);
		
		hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));
		hbox.addStrategy(new AdjustAngle(state, hbox, user.getBodyData()));
		hbox.addStrategy(new DamageStandard(state, hbox, user.getBodyData(), baseDamage, knockback, DamageTypes.MELEE, DamageTypes.CUTTING));
		
		hbox.addStrategy(new HitboxStrategy(state, hbox, user.getBodyData()) {
			
			private float controllerCount = shockwaveInterval;

			@Override
			public void controller(float delta) {
				controllerCount += delta;

				while (controllerCount >= shockwaveInterval) {
					controllerCount -= shockwaveInterval;
					Hitbox shockwave1 = new RangedHitbox(state, hbox.getPixelPosition(), shockwaveSize, shockwaveLifespan, 
							new Vector2(hbox.getLinearVelocity()).rotate90(0).nor().scl(shockwaveSpeed), filter, true, true, user, Sprite.NOTHING);
					shockwave1.addStrategy(new ControllerDefault(state, shockwave1, user.getBodyData()));
					shockwave1.addStrategy(new AdjustAngle(state, shockwave1, user.getBodyData()));
					shockwave1.addStrategy(new ContactWallDie(state, shockwave1, user.getBodyData()));
					shockwave1.addStrategy(new DamageStandard(state, shockwave1, user.getBodyData(), shockwaveDamage, knockback, DamageTypes.MELEE, DamageTypes.CUTTING));
					shockwave1.addStrategy(new CreateParticles(state, shockwave1, user.getBodyData(), Particle.LIGHTNING_CHARGE, 0.0f, 3.0f));
					
					Hitbox shockwave2 = new RangedHitbox(state, hbox.getPixelPosition(), shockwaveSize, shockwaveLifespan, 
							new Vector2(hbox.getLinearVelocity()).rotate90(-1).nor().scl(shockwaveSpeed), filter, true, true, user, Sprite.NOTHING);
					
					shockwave2.addStrategy(new ControllerDefault(state, shockwave2, user.getBodyData()));
					shockwave2.addStrategy(new AdjustAngle(state, shockwave2, user.getBodyData()));
					shockwave2.addStrategy(new ContactWallDie(state, shockwave2, user.getBodyData()));
					shockwave2.addStrategy(new DamageStandard(state, shockwave2, user.getBodyData(), shockwaveDamage, knockback, DamageTypes.MELEE, DamageTypes.CUTTING));
					shockwave2.addStrategy(new CreateParticles(state, shockwave2, user.getBodyData(), Particle.LIGHTNING_CHARGE, 0.0f, 3.0f));
				}
			}
		});
	}
}
