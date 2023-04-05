package com.mygdx.hadal.equip.actives;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.battle.SyncedAttack;
import com.mygdx.hadal.battle.attacks.active.MarineSnow;
import com.mygdx.hadal.equip.ActiveItem;
import com.mygdx.hadal.schmucks.entities.Player;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;

/**
 * @author Plungfisher Plubdul
 */
public class MarineSnowglobe extends ActiveItem {

	private static final float MAX_CHARGE = 8.0f;

	private static final float DURATION = MarineSnow.DURATION;
	private static final float PROJECTILE_DAMAGE = MarineSnow.PROJECTILE_DAMAGE;
	private static final float SLOW_DURATION = MarineSnow.SLOW_DURATION;
	private static final float SLOW_SLOW = MarineSnow.SLOW_SLOW;

	public MarineSnowglobe(Player user) {
		super(user, MAX_CHARGE);
	}
	
	@Override
	public void useItem(PlayState state, PlayerBodyData user) {
		SyncedAttack.MARINE_SNOW.initiateSyncedAttackSingle(state, user.getPlayer(), user.getPlayer().getPixelPosition(), new Vector2());
	}

	@Override
	public float getUseDuration() { return DURATION; }

	@Override
	public float getBotRangeMin() { return 5.0f; }

	@Override
	public String[] getDescFields() {
		return new String[] {
				String.valueOf((int) MAX_CHARGE),
				String.valueOf((int) PROJECTILE_DAMAGE),
				String.valueOf((int) SLOW_DURATION),
				String.valueOf((int) (SLOW_SLOW * 100))};
	}
}
