package com.mygdx.hadal.equip.actives;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.battle.WeaponUtils;
import com.mygdx.hadal.equip.ActiveItem;
import com.mygdx.hadal.schmucks.entities.Player;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;

import static com.mygdx.hadal.constants.Constants.PPM;

/**
 * @author Bleticia Blequat
 */
public class MeteorStrike extends ActiveItem {

	private static final float MAX_CHARGE = 20.0f;

	private static final float BASE_DAMAGE = 28.0f;

	private static final float METEOR_DURATION = 3.5f;
	private static final float METEOR_INTERVAL = 0.2f;
	private static final float SPREAD = 15.0f;
	
	public MeteorStrike(Player user) {
		super(user, MAX_CHARGE);
	}

	@Override
	public void useItem(PlayState state, PlayerBodyData user) {
		WeaponUtils.createMeteors(state, new Vector2(mouseLocation).scl(1 / PPM), user.getPlayer(), METEOR_DURATION,
				METEOR_INTERVAL, SPREAD, BASE_DAMAGE, DamageSource.METEOR_STRIKE);
	}
	
	@Override
	public float getUseDuration() { return METEOR_DURATION; }

	@Override
	public String[] getDescFields() {
		return new String[] {
				String.valueOf((int) MAX_CHARGE),
				String.valueOf(METEOR_DURATION),
				String.valueOf((int) (METEOR_DURATION / METEOR_INTERVAL)),
				String.valueOf((int) BASE_DAMAGE)};
	}
}
