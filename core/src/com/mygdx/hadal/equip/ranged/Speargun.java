package com.mygdx.hadal.equip.ranged;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.RangedWeapon;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.bodies.hitboxes.RangedHitbox;
import com.mygdx.hadal.schmucks.strategies.DamageStandard;
import com.mygdx.hadal.schmucks.strategies.ControllerDefault;
import com.mygdx.hadal.schmucks.strategies.AdjustAngle;
import com.mygdx.hadal.schmucks.strategies.ContactUnitLoseDurability;
import com.mygdx.hadal.schmucks.strategies.ContactWallDie;
import com.mygdx.hadal.schmucks.strategies.ContactWallParticles;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;

public class Speargun extends RangedWeapon {

	private final static int clipSize = 7;
	private final static int ammoSize = 77;
	private final static float shootCd = 0.2f;
	private final static float shootDelay = 0;
	private final static float reloadTime = 1.4f;
	private final static int reloadAmount = 0;
	private final static float baseDamage = 30.0f;
	private final static float recoil = 7.5f;
	private final static float knockback = 15.0f;
	private final static float projectileSpeed = 22.5f;
	private final static Vector2 projectileSize = new Vector2(50, 6);
	private final static float lifespan = 1.2f;
	
	private final static Sprite projSprite = Sprite.HARPOON;
	private final static Sprite weaponSprite = Sprite.MT_SPEARGUN;
	private final static Sprite eventSprite = Sprite.P_SPEARGUN;
	
	public Speargun(Schmuck user) {
		super(user, clipSize, ammoSize, reloadTime, recoil, projectileSpeed, shootCd, shootDelay, reloadAmount, true, weaponSprite, eventSprite, projectileSize.x);
	}
	
	@Override
	public void fire(PlayState state, final Schmuck user, Vector2 startPosition, Vector2 startVelocity, final short filter) {
		Hitbox hbox = new RangedHitbox(state, startPosition, projectileSize, lifespan, startVelocity, filter, true, true, user, projSprite);
		hbox.setGravity(1.0f);
		
		hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));
		hbox.addStrategy(new AdjustAngle(state, hbox, user.getBodyData()));
		hbox.addStrategy(new ContactWallParticles(state, hbox, user.getBodyData(), Particle.SPARK_TRAIL));
		hbox.addStrategy(new ContactWallDie(state, hbox, user.getBodyData()));
		hbox.addStrategy(new ContactUnitLoseDurability(state, hbox, user.getBodyData()));
		hbox.addStrategy(new DamageStandard(state, hbox, user.getBodyData(), baseDamage, knockback, DamageTypes.RANGED));
	}
}
