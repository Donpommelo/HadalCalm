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

import static com.mygdx.hadal.utils.Constants.PPM;

public class Moraygun extends RangedWeapon {

	private static final int clipSize = 7;
	private static final int ammoSize = 42;
	private static final float shootCd = 0.3f;
	private static final float shootDelay = 0;
	private static final float reloadTime = 1.0f;
	private static final int reloadAmount = 0;
	private static final float baseDamage = 13.0f;
	private static final float recoil = 12.0f;
	private static final float knockback = 5.0f;
	private static final float projectileSpeedStart = 150.0f;
	private static final Vector2 projectileSize = new Vector2(30, 30);
	private static final float lifespan = 2.0f;
	
	private static final Sprite projSprite = Sprite.ORB_PINK;
	private static final Sprite weaponSprite = Sprite.MT_CHARGEBEAM;
	private static final Sprite eventSprite = Sprite.P_CHARGEBEAM;

	private static final int numProj = 6;
	private static final float moveInterval = 0.023f;
	
	public Moraygun(Schmuck user) {
		super(user, clipSize, ammoSize, reloadTime, recoil, projectileSpeedStart, shootCd, shootDelay, reloadAmount, true, weaponSprite, eventSprite, projectileSize.x);
	}

	@Override
	public void fire(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity, short filter) {
		SoundEffect.LASERSHOT.playUniversal(state, startPosition, 0.9f, false);

		final int numX = (int) (startVelocity.x / projectileSize.x);
		final int numY = (int) (startVelocity.y / projectileSize.y);
		
		//create a set number of hboxes that die when hitting enemies or walls.
		for (int i = 0; i < numProj; i++) {
			Hitbox hbox = new RangedHitbox(state, startPosition, projectileSize, lifespan, new Vector2(), filter, true, true, user, projSprite);
			hbox.setSyncDefault(false);
			hbox.setSyncInstant(true);
			
			final int num = i;
			
			hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));
			hbox.addStrategy(new DamageStandard(state, hbox, user.getBodyData(), baseDamage, knockback, DamageTypes.ENERGY, DamageTypes.RANGED).setStaticKnockback(true));
			hbox.addStrategy(new ContactUnitLoseDurability(state, hbox, user.getBodyData()));
			hbox.addStrategy(new ContactWallDie(state, hbox, user.getBodyData()));
			hbox.addStrategy(new ContactUnitSound(state, hbox, user.getBodyData(), SoundEffect.MAGIC0_DAMAGE, 0.3f, true));
			hbox.addStrategy(new DieParticles(state, hbox, user.getBodyData(), Particle.ORB_SWIRL));

			hbox.addStrategy(new HitboxStrategy(state, hbox, user.getBodyData()) {
				
				private float controllerCount;
				private float numMoves;
				
				@Override
				public void controller(float delta) {
					controllerCount += delta;

					//Each hbox moves at set intervals. Each movement moves the hbox vertical x times followed by horizontal y times to make a snake-like movement
					while (controllerCount >= moveInterval) {
						controllerCount -= moveInterval;
						
						if (numMoves >= num) {
							if ((numMoves - num) % (Math.abs(numX) + Math.abs(numY)) < Math.abs(numX)) {
								hbox.setTransform(hbox.getPosition().add(projectileSize.x / PPM * Math.signum(numX), 0), 0);
							} else {
								hbox.setTransform(hbox.getPosition().add(0, projectileSize.y / PPM * Math.signum(numY)), 0);
							}
						}
						numMoves++;
					}
				}
			});
		}
	}
}
