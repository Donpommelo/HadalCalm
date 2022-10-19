package com.mygdx.hadal.equip.ranged;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.effects.HadalColor;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.RangedWeapon;
import com.mygdx.hadal.constants.SyncType;
import com.mygdx.hadal.schmucks.entities.Player;
import com.mygdx.hadal.schmucks.entities.Schmuck;
import com.mygdx.hadal.schmucks.entities.enemies.DroneBit;
import com.mygdx.hadal.schmucks.entities.enemies.Enemy;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.entities.hitboxes.RangedHitbox;
import com.mygdx.hadal.battle.SyncedAttack;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.battle.DamageTag;
import com.mygdx.hadal.statuses.Temporary;
import com.mygdx.hadal.strategies.hitbox.*;

public class AssaultBits extends RangedWeapon {

	private static final int clipSize = 40;
	private static final int ammoSize = 200;
	private static final float shootCd = 0.3f;
	private static final float shootDelay = 0.0f;
	private static final float reloadTime = 1.1f;
	private static final int reloadAmount = 0;
	private static final float projectileSpeed = 45.0f;
	private static final Vector2 projectileSize = new Vector2(40, 20);
	private static final float lifespan = 1.0f;
	
	private static final float summonShootCd = 1.0f;
	private static final float baseDamage = 15.0f;
	private static final float knockback = 14.0f;

	private static final Sprite projSprite = Sprite.LASER_PURPLE;
	private static final Sprite weaponSprite = Sprite.MT_CHAINLIGHTNING;
	private static final Sprite eventSprite = Sprite.P_CHAINLIGHTNING;
	
	//list of bits created
	private final Array<Enemy> bits = new Array<>();

	public AssaultBits(Schmuck user) {
		super(user, clipSize, ammoSize, reloadTime, projectileSpeed, shootCd, shootDelay, reloadAmount,
				true, weaponSprite, eventSprite, lifespan, projectileSize.x, summonShootCd);
	}
	
	private final Vector2 realWeaponVelo = new Vector2();
	@Override
	public void mouseClicked(float delta, PlayState state, BodyData shooter, short faction, Vector2 mousePosition) {
		super.mouseClicked(delta, state, shooter, faction, mousePosition);
		realWeaponVelo.set(weaponVelo);
	}
	
	private final Vector2 bitVelo = new Vector2(0, projectileSpeed);
	@Override
	public void fire(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity, short filter) {

		if (bits.isEmpty()) { return; }

		//each bit fires at the mouse
		Vector2[] positions = new Vector2[bits.size];
		Vector2[] velocities = new Vector2[bits.size];
		for (int i = 0; i < bits.size; i++) {
			positions[i] = bits.get(i).getProjectileOrigin(bitVelo, projectileSize.x);
			bitVelo.setAngleRad(bits.get(i).getAngle() + startVelocity.angleRad() - realWeaponVelo.angleRad());
			velocities[i] = new Vector2(bitVelo);
		}
		SyncedAttack.ASSAULT_BITS_BEAM.initiateSyncedAttackMulti(state, user, startVelocity, positions, velocities);
	}

	@Override
	public void update(PlayState state, float delta) {
		if (bits.size< 3) {
			setCharging(true);

			if (chargeCd < getChargeTime()) {
				chargeCd += delta;

				if (chargeCd >= getChargeTime()) {
					chargeCd = 0.0f;

					SoundEffect.CYBER2.playUniversal(state, user.getPixelPosition(), 0.4f, false);

					//bits are removed from the list upon death
					DroneBit bit = new DroneBit(state, user.getPixelPosition(), 0.0f, user.getHitboxfilter()) {

						@Override
						public boolean queueDeletion() {
							bits.removeValue(this, false);
							return super.queueDeletion();
						}
					};
					bit.setMoveTarget(user);
					bit.setOwner((Player) user);
					bit.setAttackTarget(((Player) user).getMouse());
					bits.add(bit);

					if (bits.size >= 3) {
						setCharging(false);
					}
				}
			}
		}
	}

	@Override
	public void unequip(PlayState state) {
		
		//all bits are destroyed when weapon is unequipped
		for (Enemy bit: bits) {
			if (bit.getBodyData() != null) {
				bit.getBodyData().addStatus(new Temporary(state, 10.0f, bit.getBodyData(), bit.getBodyData(), 0.25f));
			}
		}
	}

	public static Hitbox[] createAssaultBitsBeam(PlayState state, Schmuck user, Vector2[] startPosition, Vector2[] startVelocity) {
		Hitbox[] hboxes = new Hitbox[startVelocity.length];

		if (startPosition.length != 0) {
			SoundEffect.SHOOT2.playSourced(state, startPosition[0], 0.6f);
			for (int i = 0; i < startPosition.length; i++) {
				Hitbox hbox = new RangedHitbox(state, startPosition[i], projectileSize, lifespan, startVelocity[i],
						user.getHitboxfilter(), true,true, user, projSprite);

				hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));
				hbox.addStrategy(new AdjustAngle(state, hbox, user.getBodyData()));
				hbox.addStrategy(new DamageStandard(state, hbox, user.getBodyData(), baseDamage, knockback,
						DamageSource.ASSAULT_BITS, DamageTag.BULLET, DamageTag.RANGED));
				hbox.addStrategy(new ContactWallParticles(state, hbox, user.getBodyData(), Particle.LASER_IMPACT).setOffset(true).setParticleColor(
						HadalColor.VIOLET).setSyncType(SyncType.NOSYNC));
				hbox.addStrategy(new ContactUnitParticles(state, hbox, user.getBodyData(), Particle.LASER_IMPACT).setOffset(true).setParticleColor(
						HadalColor.VIOLET).setSyncType(SyncType.NOSYNC));
				hbox.addStrategy(new ContactWallDie(state, hbox, user.getBodyData()));
				hbox.addStrategy(new ContactUnitSound(state, hbox, user.getBodyData(), SoundEffect.MAGIC0_DAMAGE, 0.3f, true)
						.setSynced(false));
				hbox.addStrategy(new ContactUnitLoseDurability(state, hbox, user.getBodyData()));

				hboxes[i] = hbox;
			}
		}
		return hboxes;
	}

	@Override
	public String[] getDescFields() {
		return new String[] {
				String.valueOf((int) baseDamage),
				String.valueOf((int) summonShootCd),
				String.valueOf(DroneBit.baseHp),
				String.valueOf(clipSize),
				String.valueOf(ammoSize),
				String.valueOf(reloadTime),
				String.valueOf(shootCd)};
	}
}
