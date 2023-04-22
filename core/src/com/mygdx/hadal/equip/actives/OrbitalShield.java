package com.mygdx.hadal.equip.actives;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.battle.SyncedAttack;
import com.mygdx.hadal.battle.attacks.active.OrbitalStar;
import com.mygdx.hadal.equip.ActiveItem;
import com.mygdx.hadal.schmucks.entities.Player;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;

/**
 * @author Toberdash Twaldbaum
 */
public class OrbitalShield extends ActiveItem {

	private static final float MAX_CHARGE = 18.0f;
	
	private static final int PROJ_NUM = OrbitalStar.PROJ_NUM;
	private static final float PROJ_DAMAGE = OrbitalStar.PROJ_DAMAGE;
	private static final float PROJ_LIFESPAN = OrbitalStar.PROJ_LIFESPAN;

	public OrbitalShield(Player user) {
		super(user, MAX_CHARGE);
	}
	
	@Override
	public void useItem(PlayState state, PlayerBodyData user) {
		SyncedAttack.ORBITAL_STAR.initiateSyncedAttackMulti(state, user.getPlayer(), new Vector2(), new Vector2[]{}, new Vector2[]{});
	}

	@Override
	public float getBotRangeMin() { return 9.0f; }

	@Override
	public String[] getDescFields() {
		return new String[] {
				String.valueOf((int) MAX_CHARGE),
				String.valueOf((int) PROJ_LIFESPAN),
				String.valueOf(PROJ_NUM),
				String.valueOf((int) PROJ_DAMAGE)};
	}
}
