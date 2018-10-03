package com.mygdx.hadal.equip.ranged;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.equip.Equipable;
import com.mygdx.hadal.equip.RangedWeapon;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.schmucks.bodies.hitboxes.HitboxImage;
import com.mygdx.hadal.schmucks.strategies.HitboxDamageStandardStrategy;
import com.mygdx.hadal.schmucks.strategies.HitboxDefaultStrategy;
import com.mygdx.hadal.schmucks.strategies.HitboxOnContactChainStrategy;
import com.mygdx.hadal.schmucks.strategies.HitboxOnContactWallDieStrategy;
import com.mygdx.hadal.schmucks.strategies.HitboxOnContactWallParticles;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;
import com.mygdx.hadal.utils.HitboxFactory;

public class ChainLightning extends RangedWeapon {

	private final static String name = "Chain Lightning";
	private final static int clipSize = 3;
	private final static float shootCd = 0.15f;
	private final static float shootDelay = 0;
	private final static float reloadTime = 1.4f;
	private final static int reloadAmount = 0;

	private final static float recoil = 0.0f;
	private final static float baseDamage = 20.0f;
	private final static float knockback = 8.0f;
	private final static float projectileSpeedStart = 30.0f;
	private final static int projectileWidth = 80;
	private final static int projectileHeight = 80;
	private final static float lifespan = 4.0f;
	private final static float gravity = 0;
	
	private final static int projDura = 8;
		
	private final static String weapSpriteId = "tractorbeam";
	private final static String weapEventSpriteId = "event_tractorbeam";
	private final static String projSpriteId = "orb_yellow";
	
	private final static HitboxFactory onShoot = new HitboxFactory() {

		@Override
		public void makeHitbox(final Schmuck user, PlayState state, final Equipable tool, Vector2 startVelocity, float x, float y, short filter) {
			
			HitboxImage hbox = new HitboxImage(state, x, y, projectileWidth, projectileHeight, gravity, lifespan, projDura, 0, startVelocity,
					filter, true, true, user, projSpriteId);
			
			hbox.addStrategy(new HitboxDefaultStrategy(state, hbox, user.getBodyData()));
			hbox.addStrategy(new HitboxOnContactWallParticles(state, hbox, user.getBodyData(), "SPARK_TRAIL"));
			hbox.addStrategy(new HitboxOnContactWallDieStrategy(state, hbox, user.getBodyData()));
			hbox.addStrategy(new HitboxOnContactChainStrategy(state, hbox, user.getBodyData(), projDura, filter));
			hbox.addStrategy(new HitboxDamageStandardStrategy(state, hbox, user.getBodyData(), tool, baseDamage, knockback, DamageTypes.RANGED));
		}
	};
	
	public ChainLightning(Schmuck user) {
		super(user, name, clipSize, reloadTime, recoil, projectileSpeedStart, shootCd, shootDelay, reloadAmount, onShoot, weapSpriteId, weapEventSpriteId);
	}

}
