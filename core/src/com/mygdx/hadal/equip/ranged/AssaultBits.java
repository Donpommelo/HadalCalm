package com.mygdx.hadal.equip.ranged;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.HadalColor;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.RangedWeapon;
import com.mygdx.hadal.schmucks.bodies.Player;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.schmucks.bodies.enemies.DroneBit;
import com.mygdx.hadal.schmucks.bodies.enemies.Enemy;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.bodies.hitboxes.RangedHitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;
import com.mygdx.hadal.statuses.Temporary;
import com.mygdx.hadal.strategies.hitbox.*;

import java.util.ArrayList;

public class AssaultBits extends RangedWeapon {

	private static final int clipSize = 40;
	private static final int ammoSize = 200;
	private static final float shootCd = 0.3f;
	private static final float shootDelay = 0.0f;
	private static final float reloadTime = 1.1f;
	private static final int reloadAmount = 0;
	private static final float recoil = 0.0f;
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
	private final ArrayList<Enemy> bits = new ArrayList<>();

	public AssaultBits(Schmuck user) {
		super(user, clipSize, ammoSize, reloadTime, recoil, projectileSpeed, shootCd, shootDelay, reloadAmount, true, weaponSprite, eventSprite, projectileSize.x, summonShootCd);
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
		SoundEffect.SHOOT2.playUniversal(state, startPosition, 0.6f, false);

		for (Enemy bit: bits) {

			bitVelo.setAngleRad(bit.getAngle() + startVelocity.angleRad() - realWeaponVelo.angleRad());
			Hitbox hbox = new RangedHitbox(state, bit.getProjectileOrigin(bitVelo, projectileSize.x), projectileSize, lifespan, new Vector2(bitVelo), filter, true, true, user, projSprite);

			hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));
			hbox.addStrategy(new AdjustAngle(state, hbox, user.getBodyData()));
			hbox.addStrategy(new DamageStandard(state, hbox, user.getBodyData(), baseDamage, knockback, DamageTypes.BULLET, DamageTypes.RANGED));
			hbox.addStrategy(new ContactWallParticles(state, hbox, user.getBodyData(), Particle.LASER_IMPACT).setOffset(true).setParticleColor(
				HadalColor.VIOLET));
			hbox.addStrategy(new ContactUnitParticles(state, hbox, user.getBodyData(), Particle.LASER_IMPACT).setOffset(true).setParticleColor(
				HadalColor.VIOLET));
			hbox.addStrategy(new ContactWallDie(state, hbox, user.getBodyData()));
			hbox.addStrategy(new ContactUnitSound(state, hbox, user.getBodyData(), SoundEffect.MAGIC0_DAMAGE, 0.3f, true));
			hbox.addStrategy(new ContactUnitLoseDurability(state, hbox, user.getBodyData()));
		}
	}

	@Override
	public void update(float delta) {
		if (bits.size() < 3) {
			setCharging(true);

			if (chargeCd < getChargeTime()) {
				chargeCd += delta;

				if (chargeCd >= getChargeTime()) {
					chargeCd = 0.0f;

					SoundEffect.CYBER2.playUniversal(user.getState(), user.getPixelPosition(), 0.4f, false);

					//bits are removed from the list upon death
					DroneBit bit = new DroneBit(user.getState(), user.getPixelPosition(), 0.0f, user.getHitboxfilter(), null) {

						@Override
						public boolean queueDeletion() {
							bits.remove(this);
							return super.queueDeletion();
						}
					};
					bit.setMoveTarget(user);
					bit.setAttackTarget(((Player) user).getMouse());
					bits.add(bit);

					if (bits.size() >= 3) {
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
			bit.getBodyData().addStatus(new Temporary(state, 10.0f, bit.getBodyData(), bit.getBodyData(), 0.25f));
		}
	}
}
