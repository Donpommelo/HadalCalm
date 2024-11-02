package com.mygdx.hadal.equip.ranged;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.battle.SyncedAttack;
import com.mygdx.hadal.battle.attacks.weapon.AssaultBitBeam;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.RangedWeapon;
import com.mygdx.hadal.managers.loaders.SoundManager;
import com.mygdx.hadal.requests.SoundLoad;
import com.mygdx.hadal.schmucks.entities.Player;
import com.mygdx.hadal.schmucks.entities.enemies.DroneBit;
import com.mygdx.hadal.schmucks.entities.enemies.Enemy;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Temporary;

public class AssaultBits extends RangedWeapon {

	private static final int CLIP_SIZE = 40;
	private static final int AMMO_SIZE = 200;
	private static final float SHOOT_CD = 0.3f;
	private static final float RELOAD_TIME = 1.1f;
	private static final int RELOAD_AMOUNT = 0;
	private static final float PROJECTILE_SPEED = 45.0f;
	private static final float SUMMON_SHOOT_CD = 1.0f;

	private static final Vector2 PROJECTILE_SIZE = AssaultBitBeam.PROJECTILE_SIZE;
	private static final float LIFESPAN = AssaultBitBeam.LIFESPAN;
	private static final float BASE_DAMAGE = AssaultBitBeam.BASE_DAMAGE;

	private static final Sprite WEAPON_SPRITE = Sprite.MT_CHAINLIGHTNING;
	private static final Sprite EVENT_SPRITE = Sprite.P_CHAINLIGHTNING;
	
	public AssaultBits(Player user) {
		super(user, CLIP_SIZE, AMMO_SIZE, RELOAD_TIME, PROJECTILE_SPEED, SHOOT_CD, RELOAD_AMOUNT,
				true, WEAPON_SPRITE, EVENT_SPRITE, LIFESPAN, PROJECTILE_SIZE.x, SUMMON_SHOOT_CD);
	}
	
	private final Vector2 realWeaponVelo = new Vector2();
	@Override
	public void mouseClicked(float delta, PlayState state, PlayerBodyData playerData, short faction, Vector2 mousePosition) {
		super.mouseClicked(delta, state, playerData, faction, mousePosition);
		realWeaponVelo.set(weaponVelo);
	}
	
	@Override
	public void fire(PlayState state, Player user, Vector2 startPosition, Vector2 startVelocity, short filter) {
		realWeaponVelo.setAngleRad(startVelocity.angleRad() - realWeaponVelo.angleRad());
		if (state.isServer()) {
			fireAllBits(state, user, realWeaponVelo);
		} else {
			SyncedAttack.ASSAULT_BITS_BEAM.initiateSyncedAttackNoHbox(state, user, realWeaponVelo, false);
		}
	}

	private static final Vector2 bitVelo = new Vector2(0, PROJECTILE_SPEED);
	public static void fireAllBits(PlayState state, Player user, Vector2 realWeaponVelo) {
		Array<Enemy> bits = user.getSpecialWeaponHelper().getBits();

		if (bits.isEmpty()) { return; }

		//each bit fires at the mouse
		Vector2[] positions = new Vector2[bits.size];
		Vector2[] velocities = new Vector2[bits.size];
		for (int i = 0; i < bits.size; i++) {
			positions[i] = bits.get(i).getProjectileOrigin(bitVelo, PROJECTILE_SIZE.x);
			bitVelo.setAngleRad(bits.get(i).getAngle() + realWeaponVelo.angleRad());
			velocities[i] = new Vector2(bitVelo);
		}
		SyncedAttack.ASSAULT_BITS_BEAM.initiateSyncedAttackMulti(state, user, realWeaponVelo, positions, velocities);
	}

	private float bitRespawn;
	@Override
	public void processEffects(PlayState state, float delta, Vector2 playerPosition) {
		if (!state.isServer()) { return; }

		Array<Enemy> bits = user.getSpecialWeaponHelper().getBits();

		if (!this.equals(user.getEquipHelper().getCurrentTool())) {
			for (Enemy bit: user.getSpecialWeaponHelper().getBits()) {
				if (bit.getBodyData() != null) {
					bit.getBodyData().addStatus(new Temporary(state, 10.0f, bit.getBodyData(), bit.getBodyData(), 0.25f));
				}
			}
		} else if (bits.size < 3) {
			if (bitRespawn < getChargeTime()) {
				bitRespawn += delta;

				if (bitRespawn >= getChargeTime()) {
					bitRespawn = 0.0f;

					SoundManager.playUniversal(state, new SoundLoad(SoundEffect.CYBER2)
							.setVolume(0.4f)
							.setPosition(playerPosition));

					//bits are removed from the list upon death
					DroneBit bit = new DroneBit(state, playerPosition, 0.0f, user.getHitboxFilter()) {

						@Override
						public boolean queueDeletion() {
							bits.removeValue(this, false);
							return super.queueDeletion();
						}
					};
					bit.setMoveTarget(user);
					bit.setOwner(user);
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
		for (Enemy bit: user.getSpecialWeaponHelper().getBits()) {
			if (bit.getBodyData() != null) {
				bit.getBodyData().addStatus(new Temporary(state, 10.0f, bit.getBodyData(), bit.getBodyData(), 0.25f));
			}
		}
	}

	@Override
	public String[] getDescFields() {
		return new String[] {
				String.valueOf((int) BASE_DAMAGE),
				String.valueOf((int) SUMMON_SHOOT_CD),
				String.valueOf(DroneBit.baseHp),
				String.valueOf(CLIP_SIZE),
				String.valueOf(AMMO_SIZE),
				String.valueOf(RELOAD_TIME),
				String.valueOf(SHOOT_CD)};
	}
}
