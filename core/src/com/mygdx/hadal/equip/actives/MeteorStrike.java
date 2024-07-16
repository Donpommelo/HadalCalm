package com.mygdx.hadal.equip.actives;

import com.badlogic.gdx.math.Vector2;
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

	private static final int METEOR_NUMBER = 18;
	private static final float METEOR_DURATION = 3.6f;
	private static final float SPREAD = 15.0f;
	
	public MeteorStrike(Player user) {
		super(user, MAX_CHARGE);
	}

	@Override
	public void useItem(PlayState state, PlayerBodyData user) {
		WeaponUtils.createMeteors(state, user.getPlayer(), new Vector2(mouseLocation).scl(1 / PPM), METEOR_NUMBER, METEOR_DURATION / METEOR_NUMBER,
				BASE_DAMAGE, SPREAD);
	}
	
	@Override
	public float getUseDuration() { return METEOR_DURATION; }

	@Override
	public String[] getDescFields() {
		return new String[] {
				String.valueOf((int) MAX_CHARGE),
				String.valueOf(METEOR_DURATION),
				String.valueOf(METEOR_NUMBER),
				String.valueOf((int) BASE_DAMAGE)};
	}
}
