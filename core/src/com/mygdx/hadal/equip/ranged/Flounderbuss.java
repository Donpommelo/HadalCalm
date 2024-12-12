package com.mygdx.hadal.equip.ranged;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.battle.SyncedAttack;
import com.mygdx.hadal.battle.attacks.weapon.Flounder;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.RangedWeapon;
import com.mygdx.hadal.schmucks.entities.Player;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;

public class Flounderbuss extends RangedWeapon {

	private static final int CLIP_SIZE = 1;
	private static final int AMMO_SIZE = 15;
	private static final float SHOOT_CD = 0.0f;
	private static final float RELOAD_TIME = 0.8f;
	private static final int RELOAD_AMOUNT = 0;
	private static final float PROJECTILE_SPEED = 30.0f;
	private static final float MAX_CHARGE = 0.5f;
	private static final float VELO_SPREAD = 0.6f;
	private static final int BASE_NUM_PROJ = 3;
	private static final int MAX_NUM_PROJ = 13;

	private static final Vector2 PROJECTILE_SIZE = Flounder.PROJECTILE_SIZE;
	private static final float LIFESPAN = Flounder.LIFESPAN;
	private static final float BASE_DAMAGE = Flounder.BASE_DAMAGE;

	private static final Sprite WEAPON_SPRITE = Sprite.MT_SHOTGUN;
	private static final Sprite EVENT_SPRITE = Sprite.P_SHOTGUN;

	public Flounderbuss(Player user) {
		super(user, CLIP_SIZE, AMMO_SIZE, RELOAD_TIME, PROJECTILE_SPEED, SHOOT_CD, RELOAD_AMOUNT, WEAPON_SPRITE, EVENT_SPRITE,
				PROJECTILE_SIZE.x, LIFESPAN, MAX_CHARGE);
	}
	
	@Override
	public void mouseClicked(float delta, PlayState state, PlayerBodyData playerData, short faction, Vector2 mousePosition) {
		super.mouseClicked(delta, state, playerData, faction, mousePosition);

		if (reloading || getClipLeft() == 0) { return; }
		
		charging = true;
		
		//while held, build charge until maximum (if not reloading)
		if (chargeCd < getChargeTime()) {
			setChargeCd(chargeCd + delta);
		}
	}
	
	@Override
	public void execute(PlayState state, PlayerBodyData playerData) {}
	
	@Override
	public void release(PlayState state, PlayerBodyData playerData) {
		super.execute(state, playerData);
		charging = false;
		chargeCd = 0;
	}
	
	private final Vector2 newVelocity = new Vector2();
	@Override
	public void fire(PlayState state, Player user, Vector2 startPosition, Vector2 startVelocity, short filter) {
		//amount of projectiles scales to charge percent
		int numProj = (int) (MAX_NUM_PROJ * chargeCd / getChargeTime() + BASE_NUM_PROJ);
		Vector2[] positions = new Vector2[numProj];
		Vector2[] velocities = new Vector2[numProj];
		velocities[0] = startVelocity;
		for (int i = 0; i < numProj; i++) {
			newVelocity.set(startVelocity).scl((MathUtils.random() * VELO_SPREAD + 1 - VELO_SPREAD / 2));
			positions[i] = startPosition;
			velocities[i] = new Vector2(newVelocity);
		}
		SyncedAttack.FLOUNDER.initiateSyncedAttackMulti(state, user, startVelocity, positions, velocities);
	}

	@Override
	public String[] getDescFields() {
		return new String[] {
				String.valueOf((int) BASE_DAMAGE),
				String.valueOf(BASE_NUM_PROJ),
				String.valueOf(MAX_NUM_PROJ),
				String.valueOf(CLIP_SIZE),
				String.valueOf(AMMO_SIZE),
				String.valueOf(RELOAD_TIME),
				String.valueOf(MAX_CHARGE)};
	}
}
