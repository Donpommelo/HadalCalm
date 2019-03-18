package com.mygdx.hadal.equip.ranged;

import java.util.concurrent.ThreadLocalRandom;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.Equipable;
import com.mygdx.hadal.equip.RangedWeapon;
import com.mygdx.hadal.managers.GameStateManager;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.bodies.hitboxes.HitboxSprite;
import com.mygdx.hadal.schmucks.strategies.HitboxDamageStandardStrategy;
import com.mygdx.hadal.schmucks.strategies.HitboxDefaultStrategy;
import com.mygdx.hadal.schmucks.strategies.HitboxOnContactUnitLoseDuraStrategy;
import com.mygdx.hadal.schmucks.strategies.HitboxOnContactWallDieStrategy;
import com.mygdx.hadal.schmucks.strategies.HitboxOnContactWallParticles;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;
import com.mygdx.hadal.utils.HitboxFactory;

public class Scattergun extends RangedWeapon {

	private final static String name = "CR4P Cannon";
	private final static int clipSize = 2;
	private final static float shootCd = 0.15f;
	private final static float shootDelay = 0.0f;
	private final static float reloadTime = 1.5f;
	private final static int reloadAmount = 0;
	private final static float baseDamage = 7.0f;
	private final static float recoil = 15.0f;
	private final static float knockback = 2.5f;
	private final static float projectileSpeed = 20.0f;
	private final static int projectileWidth = 27;
	private final static int projectileHeight = 27;
	private final static float lifespan = 0.7f;
	private final static float gravity = 0.5f;
	
	private final static int projDura = 2;
	
	private final static int numProj = 11;
	private final static int spread = 10;
	
	private final static Sprite[] projSprites = {Sprite.SCRAP_A, Sprite.SCRAP_B, Sprite.SCRAP_C, Sprite.SCRAP_D};
	private final static Sprite weaponSprite = Sprite.MT_SHOTGUN;
	private final static Sprite eventSprite = Sprite.P_SHOTGUN;

	private final static HitboxFactory onShoot = new HitboxFactory() {

		@Override
		public void makeHitbox(final Schmuck user, PlayState state, Equipable tool, Vector2 startVelocity, float x, float y, short filter) {
			
			for (int i = 0; i < numProj; i++) {
				
				float newDegrees = (float) (startVelocity.angle() + (ThreadLocalRandom.current().nextInt(-spread, spread + 1)));
				
				int randomIndex = GameStateManager.generator.nextInt(projSprites.length);
				Sprite projSprite = projSprites[randomIndex];
				
				Vector2 newVelocity = new Vector2(startVelocity);
				
				Hitbox hbox = new HitboxSprite(state, x, y, projectileWidth, projectileHeight, gravity, lifespan, projDura, 0, newVelocity.setAngle(newDegrees),
						filter, true, true, user, projSprite);
				
				hbox.addStrategy(new HitboxDefaultStrategy(state, hbox, user.getBodyData()));
				hbox.addStrategy(new HitboxOnContactWallParticles(state, hbox, user.getBodyData(), Particle.SPARK_TRAIL));
				hbox.addStrategy(new HitboxOnContactUnitLoseDuraStrategy(state, hbox, user.getBodyData()));
				hbox.addStrategy(new HitboxOnContactWallDieStrategy(state, hbox, user.getBodyData()));
				hbox.addStrategy(new HitboxDamageStandardStrategy(state, hbox, user.getBodyData(), tool, baseDamage, knockback, DamageTypes.RANGED));			}
		}
	};
	
	public Scattergun(Schmuck user) {
		super(user, name, clipSize, reloadTime, recoil, projectileSpeed, shootCd, shootDelay, reloadAmount, true, onShoot, weaponSprite, eventSprite);
	}
}
