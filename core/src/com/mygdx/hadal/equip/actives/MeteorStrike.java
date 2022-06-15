package com.mygdx.hadal.equip.actives;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.equip.ActiveItem;
import com.mygdx.hadal.battle.WeaponUtils;
import com.mygdx.hadal.schmucks.entities.Schmuck;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;

import static com.mygdx.hadal.battle.WeaponUtils.torpedoExplosionDamage;
import static com.mygdx.hadal.utils.Constants.PPM;

/**
 * @author Bleticia Blequat
 */
public class MeteorStrike extends ActiveItem {

	private static final float usecd = 0.0f;
	private static final float usedelay = 0.1f;
	private static final float maxCharge = 20.0f;

	private static final float baseDamage = 28.0f;

	private static final float meteorDuration = 3.5f;
	private static final float meteorInterval = 0.1f;
	private static final float spread = 15.0f;
	
	public MeteorStrike(Schmuck user) {
		super(user, usecd, usedelay, maxCharge);
	}

	@Override
	public void useItem(PlayState state, PlayerBodyData user) {
		WeaponUtils.createMeteors(state, new Vector2(mouseLocation).scl(1 / PPM), user.getPlayer(), meteorDuration,
				meteorInterval, spread, baseDamage, DamageSource.METEOR_STRIKE);
	}
	
	@Override
	public float getUseDuration() { return meteorDuration; }

	@Override
	public String[] getDescFields() {
		return new String[] {
				String.valueOf((int) maxCharge),
				String.valueOf(meteorDuration),
				String.valueOf((int) (meteorDuration / meteorInterval)),
				String.valueOf((int) baseDamage)};
	}
}
