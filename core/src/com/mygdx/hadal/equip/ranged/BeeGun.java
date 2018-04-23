package com.mygdx.hadal.equip.ranged;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.equip.Equipable;
import com.mygdx.hadal.equip.RangedWeapon;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.bodies.hitboxes.HitboxAnimated;
import com.mygdx.hadal.schmucks.strategies.HitboxDamageStandardStrategy;
import com.mygdx.hadal.schmucks.strategies.HitboxDefaultStrategy;
import com.mygdx.hadal.schmucks.strategies.HitboxHomingStrategy;
import com.mygdx.hadal.schmucks.strategies.HitboxOnHitDieStrategy;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;
import com.mygdx.hadal.utils.HitboxFactory;

import static com.mygdx.hadal.utils.Constants.PPM;

import java.util.concurrent.ThreadLocalRandom;

public class BeeGun extends RangedWeapon {

	private final static String name = "Bee Gun";
	private final static int clipSize = 24;
	private final static float shootCd = 0.15f;
	private final static float shootDelay = 0;
	private final static float reloadTime = 1.75f;
	private final static int reloadAmount = 0;
	private final static float baseDamage = 12.0f;
	private final static float recoil = 0.0f;
	private final static float knockback = 2.5f;
	private final static float projectileSpeedStart = 3.0f;
	private final static int projectileWidth = 23;
	private final static int projectileHeight = 21;
	private final static float lifespan = 5.0f;
	private final static float gravity = 0;
	private final static float homeRadius = 10;
	
	private final static int projDura = 1;
	
	private final static int spread = 45;
	
	private final static String weapSpriteId = "beegun";
	private final static String projSpriteId = "bee";

	private static final float maxLinSpd = 100;
	private static final float maxLinAcc = 1000;
	private static final float maxAngSpd = 180;
	private static final float maxAngAcc = 90;
	
	private static final int boundingRad = 500;
	private static final int decelerationRadius = 0;

	private final static HitboxFactory onShoot = new HitboxFactory() {

		@Override
		public void makeHitbox(final Schmuck user, PlayState state, Equipable tool, Vector2 startVelocity, float x, float y, short filter) {
			
			float newDegrees = (float) (startVelocity.angle() + (ThreadLocalRandom.current().nextInt(-spread, spread + 1)));
			
			Hitbox hbox = new HitboxAnimated(state, x, y, projectileWidth, projectileHeight, gravity, lifespan, projDura, 0, startVelocity.setAngle(newDegrees),
					filter, false, user, projSpriteId) {
				
				@Override
				public void render(SpriteBatch batch) {
				
					boolean flip = false;
					
					if (body.getAngle() < 0) {
						flip = true;
					}
					
					batch.setProjectionMatrix(state.sprite.combined);

					batch.draw((TextureRegion) projectileSprite.getKeyFrame(animationTime, true), 
							body.getPosition().x * PPM - width / 2, 
							(flip ? height : 0) + body.getPosition().y * PPM - height / 2, 
							width / 2, 
							(flip ? -1 : 1) * height / 2,
							width, (flip ? -1 : 1) * height, 1, 1, 
							(float) Math.toDegrees(body.getAngle()) - 90);

				}
			};
			
			hbox.addStrategy(new HitboxDefaultStrategy(state, hbox, user.getBodyData()));
			hbox.addStrategy(new HitboxOnHitDieStrategy(state, hbox, user.getBodyData()));
			hbox.addStrategy(new HitboxDamageStandardStrategy(state, hbox, user.getBodyData(), tool, baseDamage, knockback, DamageTypes.RANGED));	
			hbox.addStrategy(new HitboxHomingStrategy(state, hbox, user.getBodyData(), maxLinSpd, maxLinAcc, 
					maxAngSpd, maxAngAcc, boundingRad, decelerationRadius, homeRadius, filter));	
		}	
	};
	
	public BeeGun(Schmuck user) {
		super(user, name, clipSize, reloadTime, recoil, projectileSpeedStart, shootCd, shootDelay, reloadAmount, onShoot, weapSpriteId);
	}

}
