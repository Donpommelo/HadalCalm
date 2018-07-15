package com.mygdx.hadal.equip.ranged;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.equip.Equipable;
import com.mygdx.hadal.equip.RangedWeapon;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.schmucks.bodies.hitboxes.HitboxImage;
import com.mygdx.hadal.schmucks.strategies.HitboxDamageStandardStrategy;
import com.mygdx.hadal.schmucks.strategies.HitboxDefaultStrategy;
import com.mygdx.hadal.schmucks.strategies.HitboxOnContactStandardStrategy;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;
import com.mygdx.hadal.utils.HitboxFactory;

public class Scattergun extends RangedWeapon {

	private final static String name = "CR4P Cannon";
	private final static int clipSize = 2;
	private final static float shootCd = 0.15f;
	private final static float shootDelay = 0.0f;
	private final static float reloadTime = 1.2f;
	private final static int reloadAmount = 0;
	private final static float baseDamage = 10.0f;
	private final static float recoil = 15.0f;
	private final static float knockback = 2.5f;
	private final static float projectileSpeed = 18.0f;
	private final static int projectileWidth = 27;
	private final static int projectileHeight = 27;
	private final static float lifespan = 0.6f;
	private final static float gravity = 0.5f;
	
	private final static int projDura = 2;
	
	private final static int numProj = 11;
	private final static int spread = 10;
	
	private final static String weapSpriteId = "scattergun";
	private final static String weapEventSpriteId = "event_doublebarrel";
	private final static String[] spriteId = {"debris_a", "debris_b", "debris_c", "debris_d"};

	private final static HitboxFactory onShoot = new HitboxFactory() {

		@Override
		public void makeHitbox(final Schmuck user, PlayState state, Equipable tool, Vector2 startVelocity, float x, float y, short filter) {
			
			for (int i = 0; i < numProj; i++) {
				
				float newDegrees = (float) (startVelocity.angle() + (ThreadLocalRandom.current().nextInt(-spread, spread + 1)));
				
				Random generator = new Random();
				int randomIndex = generator.nextInt(spriteId.length);
				String projSprite = spriteId[randomIndex];
				
				Vector2 newVelocity = new Vector2(startVelocity);
				
				HitboxImage hbox = new HitboxImage(state, x, y, projectileWidth, projectileHeight, gravity, lifespan, projDura, 0, newVelocity.setAngle(newDegrees),
						filter, true, true, user, projSprite);
				
				hbox.addStrategy(new HitboxDefaultStrategy(state, hbox, user.getBodyData()));
				hbox.addStrategy(new HitboxOnContactStandardStrategy(state, hbox, user.getBodyData()));
				hbox.addStrategy(new HitboxDamageStandardStrategy(state, hbox, user.getBodyData(), tool, baseDamage, knockback, DamageTypes.RANGED));			}
		}
	};
	
	public Scattergun(Schmuck user) {
		super(user, name, clipSize, reloadTime, recoil, projectileSpeed, shootCd, shootDelay, reloadAmount, onShoot, weapSpriteId, weapEventSpriteId);
	}
}
