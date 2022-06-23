package com.mygdx.hadal.equip.actives;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.equip.ActiveItem;
import com.mygdx.hadal.schmucks.entities.Schmuck;
import com.mygdx.hadal.battle.SyncedAttack;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;

import static com.mygdx.hadal.battle.WeaponUtils.nauticalMineExplosionDamage;
import static com.mygdx.hadal.battle.WeaponUtils.nauticalMineLifespan;

/**
 * @author Froginald Frugwump
 */
public class NauticalMine extends ActiveItem {

	private static final float usecd = 0.0f;
	private static final float usedelay = 0.1f;
	private static final float maxCharge = 8.0f;
	
	private static final float projectileSpeed = 15.0f;
	
	public NauticalMine(Schmuck user) {
		super(user, usecd, usedelay, maxCharge);
	}
	
	@Override
	public void useItem(PlayState state, PlayerBodyData user) {
		SyncedAttack.NAUTICAL_MINE.initiateSyncedAttackSingle(state, user.getPlayer(), user.getPlayer().getPixelPosition(),
				new Vector2(weaponVelo).nor().scl(projectileSpeed));
	}

	@Override
	public String[] getDescFields() {
		return new String[] {
				String.valueOf((int) maxCharge),
				String.valueOf((int) nauticalMineExplosionDamage),
				String.valueOf((int) nauticalMineLifespan)};
	}
}
