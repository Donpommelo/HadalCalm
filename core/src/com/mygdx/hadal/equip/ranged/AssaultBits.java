package com.mygdx.hadal.equip.ranged;

import java.util.ArrayList;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.RangedWeapon;
import com.mygdx.hadal.schmucks.bodies.Player;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.schmucks.bodies.enemies.Enemy;
import com.mygdx.hadal.schmucks.bodies.enemies.KBKBit;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.bodies.hitboxes.RangedHitbox;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;
import com.mygdx.hadal.strategies.hitbox.ContactUnitLoseDurability;
import com.mygdx.hadal.strategies.hitbox.ContactWallDie;
import com.mygdx.hadal.strategies.hitbox.ControllerDefault;
import com.mygdx.hadal.strategies.hitbox.DamageStandard;
import com.mygdx.hadal.strategies.hitbox.DieParticles;

public class AssaultBits extends RangedWeapon {

	private final static int clipSize = 40;
	private final static int ammoSize = 200;
	private final static float shootCd = 0.3f;
	private final static float shootDelay = 0.0f;
	private final static float reloadTime = 1.25f;
	private final static int reloadAmount = 0;
	private final static float recoil = 0.0f;
	private final static float projectileSpeed = 30.0f;
	private final static Vector2 projectileSize = new Vector2(20, 20);
	private final static float lifespan = 1.0f;
	
	private final static float summonShootCd = 1.0f;
	private final static float baseDamage = 18.0f;
	private final static float knockback = 14.0f;

	private final static Sprite projSprite = Sprite.ORB_PINK;
	private final static Sprite weaponSprite = Sprite.MT_STICKYBOMB;
	private final static Sprite eventSprite = Sprite.P_STICKYBOMB;
	
	//list of bits created
	private ArrayList<Enemy> bits = new ArrayList<Enemy>();
	private ArrayList<Enemy> bitsToRemove = new ArrayList<Enemy>();

	public AssaultBits(Schmuck user) {
		super(user, clipSize, ammoSize, reloadTime, recoil, projectileSpeed, shootCd, shootDelay, reloadAmount, true, weaponSprite, eventSprite, projectileSize.x);
	}
	
	private Vector2 bitVelo = new Vector2(0, projectileSpeed);
	@Override
	public void fire(PlayState state, final Schmuck user, Vector2 startPosition, Vector2 startVelocity, final short filter) {
		
		if (bits.size() < 3) {
			KBKBit bit = new KBKBit(state, startPosition, 0.0f, filter, null) {
				
				@Override
				public boolean queueDeletion() {
					bits.remove(this);
					return super.queueDeletion();
				}
			};
			bit.setMoveTarget(user);
			bit.setAttackTarget(((Player) user).getMouse());
			bits.add(bit);
			
			user.setShootCdCount(summonShootCd);
		} else {
			
			for (Enemy bit: bits) {
				
				bitVelo.setAngleRad(bit.getAngle());
				Hitbox hbox = new RangedHitbox(state, bit.getProjectileOrigin(bitVelo, projectileSize.x), projectileSize, lifespan, new Vector2(bitVelo), filter, true, true, user, projSprite);
				
				hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));
				hbox.addStrategy(new DieParticles(state, hbox, user.getBodyData(), Particle.SPARKS));
				hbox.addStrategy(new ContactWallDie(state, hbox, user.getBodyData()));
				hbox.addStrategy(new ContactUnitLoseDurability(state, hbox, user.getBodyData()));
				hbox.addStrategy(new DamageStandard(state, hbox, user.getBodyData(), baseDamage, knockback, DamageTypes.BULLET, DamageTypes.RANGED));
			}
		}
	}
	
	@Override
	public void unequip(PlayState state) {
		
		for (Enemy bit: bits) {
			bitsToRemove.add(bit);
		}
		
		for (Enemy bit: bitsToRemove) {
			bit.getBodyData().die(bit.getBodyData());
		}
		
		bitsToRemove.clear();
	}
}
