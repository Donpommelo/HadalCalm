package com.mygdx.hadal.equip.ranged;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.RangedWeapon;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.bodies.hitboxes.RangedHitbox;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;
import com.mygdx.hadal.strategies.HitboxStrategy;
import com.mygdx.hadal.strategies.hitbox.*;

import java.util.concurrent.ThreadLocalRandom;

public class Popper extends RangedWeapon {

	private static final int clipSize = 1;
	private static final int ammoSize = 22;
	private static final float shootCd = 0.1f;
	private static final float shootDelay = 0.0f;
	private static final float reloadTime = 0.75f;
	private static final int reloadAmount = 0;
	private static final float baseDamage = 55.0f;
	private static final float recoil = 12.0f;
	private static final float knockback = 30.0f;
	private static final float projectileSpeed = 120.0f;
	private static final Vector2 projectileSize = new Vector2(50, 50);
	private static final float lifespan = 0.3f;
	
	private static final int numProj = 8;
	private static final int spread = 30;
	private static final float fragSpeed = 40.0f;
	private static final Vector2 fragSize = new Vector2(15, 15);
	private static final float fragLifespan = 1.2f;
	private static final float fragDamage = 18.0f;
	private static final float fragKnockback = 2.0f;

	private static final float projDampen = 9.0f;
	private static final float fragDampen = 3.0f;
	
	private static final Sprite projSprite = Sprite.POPPER;
	private static final Sprite fragSprite = Sprite.ORB_PINK;
	private static final Sprite weaponSprite = Sprite.MT_BOOMERANG;
	private static final Sprite eventSprite = Sprite.P_BOOMERANG;
	
	public Popper(Schmuck user) {
		super(user, clipSize, ammoSize, reloadTime, recoil, projectileSpeed, shootCd, shootDelay, reloadAmount, true, weaponSprite, eventSprite, projectileSize.x);
	}
	
	@Override
	public void fire(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity, short filter) {
		SoundEffect.CRACKER1.playUniversal(state, startPosition, 1.0f, false);

		Hitbox hbox = new RangedHitbox(state, startPosition, projectileSize, lifespan, startVelocity, filter, false, true, user, projSprite);
		hbox.setGravity(5.0f);
		
		hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));
		hbox.addStrategy(new DamageStandard(state, hbox, user.getBodyData(), baseDamage, knockback, DamageTypes.PARTY, DamageTypes.RANGED));
		hbox.addStrategy(new DieSound(state, hbox, user.getBodyData(), SoundEffect.CRACKER2, 0.4f));
		hbox.addStrategy(new DieSound(state, hbox, user.getBodyData(), SoundEffect.NOISEMAKER, 0.4f));
		hbox.addStrategy(new DieParticles(state, hbox, user.getBodyData(), Particle.PARTY));

		hbox.addStrategy(new HitboxStrategy(state, hbox, user.getBodyData()) {
			
			@Override
			public void create() {
				super.create();
				hbox.getBody().setLinearDamping(projDampen);
			}
			
			@Override
			public void die() {
				Vector2 newVelocity = new Vector2();
				for (int i = 0; i < numProj; i++) {
					float newDegrees = new Vector2(0, 1).angle() + (ThreadLocalRandom.current().nextInt(-spread, spread + 1));
					newVelocity.set(0, 1).nor().scl(fragSpeed);
					
					Hitbox frag = new RangedHitbox(state, hbox.getPixelPosition(), fragSize, fragLifespan, newVelocity.setAngle(newDegrees), filter, false, true, user, fragSprite) {
						
						@Override
						public void create() {
							super.create();
							getBody().setLinearDamping(fragDampen);
						}
					};
					frag.setGravity(7.5f);
					frag.setDurability(3);
					
					frag.addStrategy(new ControllerDefault(state, frag, user.getBodyData()));
					frag.addStrategy(new ContactUnitLoseDurability(state, frag, user.getBodyData()));
					frag.addStrategy(new DamageStandard(state, frag, user.getBodyData(), fragDamage, fragKnockback, DamageTypes.RANGED));
				}
			}
		});
	}
}
