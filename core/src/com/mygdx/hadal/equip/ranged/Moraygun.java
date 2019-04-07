package com.mygdx.hadal.equip.ranged;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.RangedWeapon;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.bodies.hitboxes.HitboxSprite;
import com.mygdx.hadal.schmucks.strategies.HitboxDamageStandardStrategy;
import com.mygdx.hadal.schmucks.strategies.HitboxDefaultStrategy;
import com.mygdx.hadal.schmucks.strategies.HitboxOnContactWallDieStrategy;
import com.mygdx.hadal.schmucks.strategies.HitboxStrategy;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;
import static com.mygdx.hadal.utils.Constants.PPM;

public class Moraygun extends RangedWeapon {

	private final static String name = "Moraygun";
	private final static int clipSize = 7;
	private final static int ammoSize = 21;
	private final static float shootCd = 0.3f;
	private final static float shootDelay = 0;
	private final static float reloadTime = 1.0f;
	private final static int reloadAmount = 0;
	private final static float baseDamage = 6.0f;
	private final static float recoil = 15.0f;
	private final static float knockback = 4.5f;
	private final static float projectileSpeedStart = 300.0f;
	private final static int projectileWidth = 45;
	private final static int projectileHeight = 45;
	private final static float lifespan = 4.5f;
	private final static float gravity = 0.0f;
	
	private final static int projDura = 1;
	
	private final static Sprite projSprite = Sprite.ORB_PINK;
	private final static Sprite weaponSprite = Sprite.MT_DEFAULT;
	private final static Sprite eventSprite = Sprite.P_DEFAULT;

	private final static int numProj = 6;
	private final static float moveInterval = 0.04f;
	
	public Moraygun(Schmuck user) {
		super(user, name, clipSize, ammoSize, reloadTime, recoil, projectileSpeedStart, shootCd, shootDelay, reloadAmount, true, weaponSprite, eventSprite);
	}

	@Override
	public void fire(PlayState state, final Schmuck user, Vector2 startVelocity, float x, float y, short filter) {
		
		final int numX = (int) (startVelocity.x / projectileWidth);
		final int numY = (int) (startVelocity.y / projectileHeight);
		
		for (int i = 0; i < numProj; i++) {
			Hitbox hbox = new HitboxSprite(state, x, y, projectileWidth, projectileHeight, gravity, lifespan, projDura, 0, new Vector2(0, 0),
					filter, true, true, user, projSprite);
			
			final int num = i;
			
			hbox.addStrategy(new HitboxDefaultStrategy(state, hbox, user.getBodyData()));
			hbox.addStrategy(new HitboxDamageStandardStrategy(state, hbox, user.getBodyData(), this, baseDamage, knockback, DamageTypes.RANGED));
			hbox.addStrategy(new HitboxOnContactWallDieStrategy(state, hbox, user.getBodyData()));
			hbox.addStrategy(new HitboxStrategy(state, hbox, user.getBodyData()) {
				
				private float controllerCount = 0;
				private float numMoves = 0;
				
				@Override
				public void controller(float delta) {
					controllerCount+=delta;

					if (controllerCount >= moveInterval) {
						controllerCount -= moveInterval;
						if (numMoves >= num) {
							if ((numMoves - num) % (Math.abs(numX) + Math.abs(numY)) < Math.abs(numX)) {
								hbox.setTransform(hbox.getPosition()
										.add(projectileWidth / PPM / 2 * Math.signum(numX), 0), 0);
							} else {
								hbox.setTransform(hbox.getPosition()
										.add(0, projectileHeight / PPM / 2 * Math.signum(numY)), 0);
							}
						}
						numMoves++;
					}
				}
			});
		}
	}
}
