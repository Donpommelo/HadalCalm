package com.mygdx.hadal.equip.ranged;

import java.util.ArrayList;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.ParticleColor;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.RangedWeapon;
import com.mygdx.hadal.schmucks.bodies.Player;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.schmucks.bodies.enemies.Enemy;
import com.mygdx.hadal.schmucks.bodies.enemies.DroneBit;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.bodies.hitboxes.RangedHitbox;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;
import com.mygdx.hadal.statuses.Temporary;
import com.mygdx.hadal.strategies.hitbox.AdjustAngle;
import com.mygdx.hadal.strategies.hitbox.ContactUnitLoseDurability;
import com.mygdx.hadal.strategies.hitbox.ContactUnitParticles;
import com.mygdx.hadal.strategies.hitbox.ContactWallDie;
import com.mygdx.hadal.strategies.hitbox.ContactWallParticles;
import com.mygdx.hadal.strategies.hitbox.ControllerDefault;
import com.mygdx.hadal.strategies.hitbox.DamageStandard;

public class AssaultBits extends RangedWeapon {

	private final static int clipSize = 40;
	private final static int ammoSize = 200;
	private final static float shootCd = 0.3f;
	private final static float shootDelay = 0.0f;
	private final static float reloadTime = 1.25f;
	private final static int reloadAmount = 0;
	private final static float recoil = 0.0f;
	private final static float projectileSpeed = 45.0f;
	private final static Vector2 projectileSize = new Vector2(40, 20);
	private final static float lifespan = 1.0f;
	
	private final static float summonShootCd = 1.0f;
	private final static float baseDamage = 15.0f;
	private final static float knockback = 14.0f;

	private final static Sprite projSprite = Sprite.LASER_PURPLE;
	private final static Sprite weaponSprite = Sprite.MT_DEFAULT;
	private final static Sprite eventSprite = Sprite.P_DEFAULT;
	
	//list of bits created
	private ArrayList<Enemy> bits = new ArrayList<Enemy>();
	private ArrayList<Enemy> bitsToRemove = new ArrayList<Enemy>();

	public AssaultBits(Schmuck user) {
		super(user, clipSize, ammoSize, reloadTime, recoil, projectileSpeed, shootCd, shootDelay, reloadAmount, true, weaponSprite, eventSprite, projectileSize.x);
	}
	
	private Vector2 bitVelo = new Vector2(0, projectileSpeed);
	@Override
	public void fire(PlayState state, final Schmuck user, Vector2 startPosition, Vector2 startVelocity, final short filter) {

		//if there are fewer than 3 bits, summon a bit and go on cooldown for longer
		if (bits.size() < 3) {
			SoundEffect.CYBER2.playUniversal(state, startPosition, 0.4f, false);
			
			//bits are removed fro mthe list upon death
			DroneBit bit = new DroneBit(state, startPosition, 0.0f, filter, null) {
				
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
			
			//when 3 bits are active, all 3 fire shots at the mouse
			SoundEffect.SHOOT2.playUniversal(state, startPosition, 0.8f, false);

			for (Enemy bit: bits) {
				
				bitVelo.setAngleRad(bit.getAngle());
				Hitbox hbox = new RangedHitbox(state, bit.getProjectileOrigin(bitVelo, projectileSize.x), projectileSize, lifespan, new Vector2(bitVelo), filter, true, true, user, projSprite);
				
				hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));
				hbox.addStrategy(new AdjustAngle(state, hbox, user.getBodyData()));
				hbox.addStrategy(new ContactWallParticles(state, hbox, user.getBodyData(), Particle.LASER_IMPACT).setOffset(true).setParticleColor(ParticleColor.PURPLE));
				hbox.addStrategy(new ContactUnitParticles(state, hbox, user.getBodyData(), Particle.LASER_IMPACT).setOffset(true).setParticleColor(ParticleColor.PURPLE));
				hbox.addStrategy(new ContactWallDie(state, hbox, user.getBodyData()));
				hbox.addStrategy(new ContactUnitLoseDurability(state, hbox, user.getBodyData()));
				hbox.addStrategy(new DamageStandard(state, hbox, user.getBodyData(), baseDamage, knockback, DamageTypes.BULLET, DamageTypes.RANGED));
			}
		}
	}
	
	@Override
	public void unequip(PlayState state) {
		
		//all bits are destroyed when weapon is unequipped
		for (Enemy bit: bits) {
			bit.getBodyData().addStatus(new Temporary(state, 10.0f, bit.getBodyData(), bit.getBodyData(), 0.25f));
		}
		bitsToRemove.clear();
	}
}
