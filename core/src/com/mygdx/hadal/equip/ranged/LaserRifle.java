package com.mygdx.hadal.equip.ranged;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.battle.SyncedAttack;
import com.mygdx.hadal.battle.attacks.weapon.Laser;
import com.mygdx.hadal.constants.BodyConstants;
import com.mygdx.hadal.constants.Stats;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.RangedWeapon;
import com.mygdx.hadal.schmucks.entities.Player;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.utils.WorldUtil;

public class LaserRifle extends RangedWeapon {

	private static final int CLIP_SIZE = 8;
	private static final int AMMO_SIZE = 56;
	private static final float SHOOT_CD = 0.4f;
	private static final float RELOAD_TIME = 1.25f;
	private static final int RELOAD_AMOUNT = 0;
	private static final float PROJECTILE_SPEED = 20.0f;

	private static final int PROJECTILE_WIDTH = Laser.PROJECTILE_WIDTH;
	private static final float LIFESPAN = Laser.LIFESPAN;
	private static final float BASE_DAMAGE = Laser.BASE_DAMAGE;

	private static final Sprite WEAPON_SPRITE = Sprite.MT_LASERRIFLE;
	private static final Sprite EVENT_SPRITE = Sprite.P_LASERRIFLE;
	
	private float shortestFraction;
	
	public LaserRifle(Player user) {
		super(user, CLIP_SIZE, AMMO_SIZE, RELOAD_TIME, PROJECTILE_SPEED, SHOOT_CD, RELOAD_AMOUNT, true,
				WEAPON_SPRITE, EVENT_SPRITE, LIFESPAN, 0);
	}

	private final Vector2 endPt = new Vector2();
	private final Vector2 entityLocation = new Vector2();
	@Override
	public void fire(PlayState state, Player user, Vector2 startPosition, Vector2 startVelocity, short filter) {

		//This is the max distance this weapon can shoot (hard coded to scale to weapon range modifiers)
		float distance = PROJECTILE_WIDTH * (1 + user.getBodyData().getStat(Stats.RANGED_PROJ_LIFESPAN));
		entityLocation.set(user.getPosition());
		endPt.set(entityLocation).add(new Vector2(startVelocity).nor().scl(distance));
		shortestFraction = 1.0f;
		
		//Raycast length of distance until we hit a wall
		if (WorldUtil.preRaycastCheck(entityLocation, endPt)) {
			state.getWorld().rayCast((fixture, point, normal, fraction) -> {
				if (fixture.getFilterData().categoryBits == BodyConstants.BIT_WALL) {
					if (fraction < shortestFraction) {
						shortestFraction = fraction;
						return fraction;
					}
				}
				return -1.0f;
			}, entityLocation, endPt);
		}
		SyncedAttack.LASER.initiateSyncedAttackSingle(state, user, startPosition, startVelocity, distance * shortestFraction);
	}

	@Override
	public float getBotRangeMax() { return PROJECTILE_WIDTH; }

	@Override
	public String[] getDescFields() {
		return new String[] {
				String.valueOf((int) BASE_DAMAGE),
				String.valueOf(CLIP_SIZE),
				String.valueOf(AMMO_SIZE),
				String.valueOf(RELOAD_TIME),
				String.valueOf(SHOOT_CD)};
	}
}
