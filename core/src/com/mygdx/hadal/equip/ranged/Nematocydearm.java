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
import com.mygdx.hadal.schmucks.strategies.HitboxOnContactStickStrategy;
import com.mygdx.hadal.schmucks.strategies.HitboxOnContactUnitDieStrategy;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;
import com.mygdx.hadal.utils.HitboxFactory;

public class Nematocydearm extends RangedWeapon {

	private final static String name = "Nemato Sidearm";
	private final static int clipSize = 6;
	private final static float shootCd = 0.1f;
	private final static float shootDelay = 0.0f;
	private final static float reloadTime = 2.2f;
	private final static int reloadAmount = 0;
	private final static float baseDamage = 20.0f;
	private final static float recoil = 0.0f;
	private final static float knockback = 15.0f;
	private final static float projectileSpeed = 40.0f;
	private final static int projectileWidth = 96;
	private final static int projectileHeight = 12;
	private final static float lifespan = 10.0f;
	private final static float gravity = 0;
	
	private final static int projDura = 1;
		
	private final static Sprite projSprite = Sprite.BULLET;
	private final static Sprite weaponSprite = Sprite.MT_NEMATOCYTEARM;
	private final static Sprite eventSprite = Sprite.P_NEMATOCYTEARM;
	
	private final static HitboxFactory onShoot = new HitboxFactory() {

		@Override
		public void makeHitbox(final Schmuck user, PlayState state, Equipable tool, Vector2 startVelocity, float x, float y, short filter) {
			
			Hitbox hbox = new HitboxSprite(state, x, y, projectileWidth, projectileHeight, gravity, lifespan, projDura, 0, startVelocity,
					filter, true, true, user, projSprite);
			
			hbox.addStrategy(new HitboxDefaultStrategy(state, hbox, user.getBodyData()));
			hbox.addStrategy(new HitboxOnContactUnitDieStrategy(state, hbox, user.getBodyData()));
			hbox.addStrategy(new HitboxOnContactStickStrategy(state, hbox, user.getBodyData(), true, false));
			hbox.addStrategy(new HitboxDamageStandardStrategy(state, hbox, user.getBodyData(), tool, baseDamage, knockback, DamageTypes.RANGED));
		}
	};
	
	public Nematocydearm(Schmuck user) {
		super(user, name, clipSize, reloadTime, recoil, projectileSpeed, shootCd, shootDelay, reloadAmount, true, onShoot, weaponSprite, eventSprite);
	}
}
