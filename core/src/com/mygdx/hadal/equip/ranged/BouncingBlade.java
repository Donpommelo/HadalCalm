package com.mygdx.hadal.equip.ranged;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.RangedWeapon;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.bodies.hitboxes.RangedHitbox;
import com.mygdx.hadal.schmucks.strategies.HitboxDamageStandardStrategy;
import com.mygdx.hadal.schmucks.strategies.HitboxDefaultStrategy;
import com.mygdx.hadal.schmucks.strategies.HitboxOnContactWallLoseDuraStrategy;
import com.mygdx.hadal.schmucks.strategies.HitboxOnContactWallParticles;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;

public class BouncingBlade extends RangedWeapon {

	private final static String name = "Bouncing Blades";
	private final static int clipSize = 5;
	private final static int ammoSize = 30;
	private final static float shootCd = 0.3f;
	private final static float shootDelay = 0;
	private final static float reloadTime = 1.4f;
	private final static int reloadAmount = 0;
	private final static float baseDamage = 25.0f;
	private final static float recoil = 6.0f;
	private final static float knockback = 18.0f;
	private final static float projectileSpeed = 30.0f;
	private final static int projectileWidth = 40;
	private final static int projectileHeight = 40;
	private final static float lifespan = 3.5f;
	
	private final static Sprite projSprite = Sprite.BUZZSAW;
	private final static Sprite weaponSprite = Sprite.MT_BLADEGUN;
	private final static Sprite eventSprite = Sprite.P_BLADEGUN;
	
	public BouncingBlade(Schmuck user) {
		super(user, name, clipSize, ammoSize, reloadTime, recoil, projectileSpeed, shootCd, shootDelay, reloadAmount, true, weaponSprite, eventSprite, projectileWidth);
	}
	
	@Override
	public void fire(PlayState state, Schmuck user, Vector2 startVelocity, float x, float y, short filter) {
		Hitbox hbox = new RangedHitbox(state, x, y, projectileWidth, projectileHeight, lifespan, startVelocity, filter, false, true, user, projSprite);
		hbox.setDurability(5);
		hbox.setRestitution(1.0f);
		
		hbox.addStrategy(new HitboxDefaultStrategy(state, hbox, user.getBodyData()));
		hbox.addStrategy(new HitboxOnContactWallParticles(state, hbox, user.getBodyData(), Particle.SPARK_TRAIL));
		hbox.addStrategy(new HitboxOnContactWallLoseDuraStrategy(state, hbox, user.getBodyData()));
		hbox.addStrategy(new HitboxDamageStandardStrategy(state, hbox, user.getBodyData(), this, baseDamage, knockback, DamageTypes.RANGED));
	}
}
