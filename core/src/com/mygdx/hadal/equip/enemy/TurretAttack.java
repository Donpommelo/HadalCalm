package com.mygdx.hadal.equip.enemy;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.equip.Equipable;
import com.mygdx.hadal.equip.RangedWeapon;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.bodies.hitboxes.HitboxImage;
import com.mygdx.hadal.schmucks.strategies.HitboxDamageStandardStrategy;
import com.mygdx.hadal.schmucks.strategies.HitboxDefaultStrategy;
import com.mygdx.hadal.schmucks.strategies.HitboxOnContactUnitLoseDuraStrategy;
import com.mygdx.hadal.schmucks.strategies.HitboxOnContactWallDieStrategy;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;
import com.mygdx.hadal.utils.HitboxFactory;

public class TurretAttack extends RangedWeapon {

	private final static String name = "Turret Gun";
	private final static int clipSize = 1;
	private final static float shootCd = 0.0f;
	private final static float shootDelay = 0;
	private final static float reloadTime = 0.75f;
	private final static int reloadAmount = 0;
	private final static float baseDamage = 25.0f;
	private final static float recoil = 0.0f;
	private final static float knockback = 5.0f;
	private final static float projectileSpeed = 15.0f;
	private final static int projectileWidth = 192;
	private final static int projectileHeight = 24;
	private final static float lifespan = 1.50f;
	private final static float gravity = 1;
	
	private final static int projDura = 1;
	
	private final static int numProj = 3;
	private final static int spread = 30;

	private final static String projSpriteId = "bullet";
	
	private final static HitboxFactory onShoot = new HitboxFactory() {

		@Override
		public void makeHitbox(final Schmuck user, PlayState state, Equipable tool, Vector2 startVelocity, float x, float y, short filter) {
			
			Vector2 center = new Vector2(startVelocity);
			
			for (int i = -numProj / 2; i <= numProj / 2; i++) {
				Hitbox hbox = new HitboxImage(state, x, y, projectileWidth, projectileHeight, gravity, lifespan, projDura, 0, 
						startVelocity.setAngle(center.angle() + i * spread),
						filter, true, true, user, projSpriteId);
				
				hbox.addStrategy(new HitboxDefaultStrategy(state, hbox, user.getBodyData()));
				hbox.addStrategy(new HitboxOnContactUnitLoseDuraStrategy(state, hbox, user.getBodyData()));
				hbox.addStrategy(new HitboxOnContactWallDieStrategy(state, hbox, user.getBodyData()));
				hbox.addStrategy(new HitboxDamageStandardStrategy(state, hbox, user.getBodyData(), tool, baseDamage, knockback, DamageTypes.RANGED));		
			}
		}
		
	};
	
	public TurretAttack(Schmuck user) {
		super(user, name, clipSize, reloadTime, recoil, projectileSpeed, shootCd, shootDelay, reloadAmount, onShoot);
	}

}
