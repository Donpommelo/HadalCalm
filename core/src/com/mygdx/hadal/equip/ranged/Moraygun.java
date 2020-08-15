package com.mygdx.hadal.equip.ranged;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.RangedWeapon;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.bodies.hitboxes.RangedHitbox;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;
import com.mygdx.hadal.strategies.HitboxStrategy;
import com.mygdx.hadal.strategies.hitbox.ContactUnitLoseDurability;
import com.mygdx.hadal.strategies.hitbox.ContactWallDie;
import com.mygdx.hadal.strategies.hitbox.ControllerDefault;
import com.mygdx.hadal.strategies.hitbox.DamageStatic;

import static com.mygdx.hadal.utils.Constants.PPM;

public class Moraygun extends RangedWeapon {

	private final static int clipSize = 7;
	private final static int ammoSize = 42;
	private final static float shootCd = 0.3f;
	private final static float shootDelay = 0;
	private final static float reloadTime = 1.0f;
	private final static int reloadAmount = 0;
	private final static float baseDamage = 15.0f;
	private final static float recoil = 12.0f;
	private final static float knockback = 5.0f;
	private final static float projectileSpeedStart = 150.0f;
	private final static Vector2 projectileSize = new Vector2(30, 30);
	private final static float lifespan = 2.0f;
	
	private final static Sprite projSprite = Sprite.ORB_PINK;
	private final static Sprite weaponSprite = Sprite.MT_CHARGEBEAM;
	private final static Sprite eventSprite = Sprite.P_CHARGEBEAM;

	private final static int numProj = 6;
	private final static float moveInterval = 0.025f;
	
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
			hbox.addStrategy(new DamageStatic(state, hbox, user.getBodyData(), baseDamage, knockback, DamageTypes.ENERGY, DamageTypes.RANGED));
			hbox.addStrategy(new ContactUnitLoseDurability(state, hbox, user.getBodyData()));
			hbox.addStrategy(new ContactWallDie(state, hbox, user.getBodyData()));
			
			hbox.addStrategy(new HitboxStrategy(state, hbox, user.getBodyData()) {
				
				private float controllerCount;
				private float numMoves;
				
				@Override
				public void controller(float delta) {
					controllerCount += delta;

					//Each hbox moves at set intervals. Each movement moves the hbox verticle x times followed by horizontal y times to make a snake-like movement
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
