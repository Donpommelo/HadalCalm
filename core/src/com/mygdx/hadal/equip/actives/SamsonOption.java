package com.mygdx.hadal.equip.actives;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.battle.SyncedAttack;
import com.mygdx.hadal.equip.ActiveItem;
import com.mygdx.hadal.schmucks.entities.Player;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;

/**
 * @author Larlando Locwump
 */
public class SamsonOption extends ActiveItem {

	private static final float MAX_CHARGE = 5.0f;

	private static final Vector2 PROJECTILE_SIZE = new Vector2(400, 400);
	private static final float DURATION = 1.5f;
	private static final float PROC_CD = 0.12f;
	private static final float EXPLOSION_DAMAGE = 50.0f;
	private static final int EXPLOSION_NUMBER = 13;

	public SamsonOption(Player user) {
		super(user, MAX_CHARGE);
	}
	
	@Override
	public void useItem(PlayState state, PlayerBodyData user) {
		float[] explosionCoords = new float[EXPLOSION_NUMBER * 2];

		Vector2 startPosition = user.getPlayer().getPixelPosition();
		for (int i = 0; i < EXPLOSION_NUMBER; i++) {
			explosionCoords[i * 2] = (MathUtils.random() * PROJECTILE_SIZE.x) - (PROJECTILE_SIZE.x / 2) + startPosition.x;
			explosionCoords[i * 2 + 1] = (MathUtils.random() * PROJECTILE_SIZE.y) - (PROJECTILE_SIZE.y / 2) + startPosition.y;
		}

		SyncedAttack.SAMSON_OPTION.initiateSyncedAttackSingle(state, user.getPlayer(), startPosition, weaponVelo, explosionCoords);
	}
	
	@Override
	public float getUseDuration() { return DURATION; }

	@Override
	public String[] getDescFields() {
		return new String[] {
				String.valueOf((int) MAX_CHARGE),
				String.valueOf(DURATION),
				String.valueOf(PROC_CD),
				String.valueOf((int) EXPLOSION_DAMAGE)};
	}
}
