package com.mygdx.hadal.equip.ranged;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.Equipable;
import com.mygdx.hadal.equip.RangedWeapon;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.bodies.hitboxes.HitboxSprite;
import com.mygdx.hadal.schmucks.strategies.HitboxDamageStandardStrategy;
import com.mygdx.hadal.schmucks.strategies.HitboxDefaultStrategy;
import com.mygdx.hadal.schmucks.strategies.HitboxOnDiePoisonStrategy;
import com.mygdx.hadal.schmucks.strategies.HitboxOnContactUnitDieStrategy;
import com.mygdx.hadal.schmucks.strategies.HitboxOnContactWallDieStrategy;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;
import com.mygdx.hadal.utils.HitboxFactory;

public class Fugun extends RangedWeapon {

	private final static String name = "Fugun";
	private final static int clipSize = 2;
	private final static float shootCd = 0.25f;
	private final static float shootDelay = 0.0f;
	private final static float reloadTime = 1.5f;
	private final static int reloadAmount = 1;
	private final static float baseDamage = 30.0f;
	private final static float recoil = 0.0f;
	private final static float knockback = 12.5f;
	private final static float projectileSpeed = 25.0f;
	private final static int projectileWidth = 80;
	private final static int projectileHeight = 80;
	private final static float lifespan = 1.5f;
	private final static float gravity = 0;
	
	private final static int projDura = 1;
		
	private final static int poisonRadius = 250;
	private final static float poisonDamage = 30/60f;
	private final static float poisonDuration = 4.5f;

	private final static Sprite projSprite = Sprite.SCRAP_C;
	private final static Sprite weaponSprite = Sprite.MT_DEFAULT;
	private final static Sprite eventSprite = Sprite.P_DEFAULT;
	
	private final static HitboxFactory onShoot = new HitboxFactory() {

		@Override
		public void makeHitbox(final Schmuck user, PlayState state, Equipable tool, Vector2 startVelocity, float x, float y, short filter) {
			
			Hitbox hbox = new HitboxSprite(state, x, y, projectileWidth, projectileHeight, gravity, lifespan, projDura, 0, startVelocity,
					filter, true, true, user, projSprite);
			
			hbox.addStrategy(new HitboxDefaultStrategy(state, hbox, user.getBodyData()));
			hbox.addStrategy(new HitboxOnContactUnitDieStrategy(state, hbox, user.getBodyData()));
			hbox.addStrategy(new HitboxOnContactWallDieStrategy(state, hbox, user.getBodyData()));
			hbox.addStrategy(new HitboxDamageStandardStrategy(state, hbox, user.getBodyData(), tool, baseDamage, knockback, DamageTypes.RANGED));
			hbox.addStrategy(new HitboxOnDiePoisonStrategy(state, hbox, user.getBodyData(), poisonRadius, poisonDamage, poisonDuration, (short)0));
		}
	};
	
	public Fugun(Schmuck user) {
		super(user, name, clipSize, reloadTime, recoil, projectileSpeed, shootCd, shootDelay, reloadAmount, true, onShoot, weaponSprite, eventSprite);
	}
}
