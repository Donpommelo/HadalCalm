package com.mygdx.hadal.equip.ranged;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.equip.Equipable;
import com.mygdx.hadal.equip.RangedWeapon;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.bodies.hitboxes.HitboxImage;
import com.mygdx.hadal.schmucks.strategies.HitboxDamageStandardStrategy;
import com.mygdx.hadal.schmucks.strategies.HitboxDefaultStrategy;
import com.mygdx.hadal.schmucks.strategies.HitboxOnDiePoisonStrategy;
import com.mygdx.hadal.schmucks.strategies.HitboxOnContactDieStrategy;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;
import com.mygdx.hadal.utils.HitboxFactory;

public class Nematocydearm extends RangedWeapon {

	private final static String name = "Nematocydearm";
	private final static int clipSize = 3;
	private final static float shootCd = 0.45f;
	private final static float shootDelay = 0.0f;
	private final static float reloadTime = 0.75f;
	private final static int reloadAmount = 1;
	private final static float baseDamage = 40.0f;
	private final static float recoil = 0.0f;
	private final static float knockback = 12.5f;
	private final static float projectileSpeed = 25.0f;
	private final static int projectileWidth = 40;
	private final static int projectileHeight = 40;
	private final static float lifespan = 3.0f;
	private final static float gravity = 0;
	
	private final static int projDura = 1;
		
	private final static int poisonRadius = 200;
	private final static float poisonDamage = 40/60f;
	private final static float poisonDuration = 4.0f;

	private final static String weapSpriteId = "nematocytearm";
	private final static String projSpriteId = "debris_c";
	
	private final static HitboxFactory onShoot = new HitboxFactory() {

		@Override
		public void makeHitbox(final Schmuck user, PlayState state, Equipable tool, Vector2 startVelocity, float x, float y, short filter) {
			
			Hitbox hbox = new HitboxImage(state, x, y, projectileWidth, projectileHeight, gravity, lifespan, projDura, 0, startVelocity,
					filter, true, true, user, projSpriteId);
			
			hbox.addStrategy(new HitboxDefaultStrategy(state, hbox, user.getBodyData()));
			hbox.addStrategy(new HitboxOnContactDieStrategy(state, hbox, user.getBodyData()));
			hbox.addStrategy(new HitboxDamageStandardStrategy(state, hbox, user.getBodyData(), tool, baseDamage, knockback, DamageTypes.RANGED));
			hbox.addStrategy(new HitboxOnDiePoisonStrategy(state, hbox, user.getBodyData(), poisonRadius, poisonDamage, poisonDuration, (short)0));
		}
	};
	
	public Nematocydearm(Schmuck user) {
		super(user, name, clipSize, reloadTime, recoil, projectileSpeed, shootCd, shootDelay, reloadAmount, onShoot, weapSpriteId);
	}
}
