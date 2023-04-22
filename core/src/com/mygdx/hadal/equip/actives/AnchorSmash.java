package com.mygdx.hadal.equip.actives;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.battle.SyncedAttack;
import com.mygdx.hadal.battle.attacks.active.Anchor;
import com.mygdx.hadal.constants.Constants;
import com.mygdx.hadal.equip.ActiveItem;
import com.mygdx.hadal.schmucks.entities.Player;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.utils.WorldUtil;

import static com.mygdx.hadal.constants.Constants.PPM;

/**
 * @author Louhaha Losemary
 */
public class AnchorSmash extends ActiveItem {

	private static final float MAX_CHARGE = 16.0f;
	private static final float RANGE = 1800.0f;
	private static final float BASE_DAMAGE = Anchor.BASE_DAMAGE;

	public AnchorSmash(Player user) {
		super(user, MAX_CHARGE);
	}

	private float shortestFraction;
	private final Vector2 originPt = new Vector2();
	private final Vector2 endPt = new Vector2();

	@Override
	public void useItem(PlayState state, PlayerBodyData user) {

		originPt.set(mouseLocation).scl(1 / PPM);
		endPt.set(originPt).add(0, -RANGE);
		shortestFraction = 1.0f;

		if (WorldUtil.preRaycastCheck(originPt, endPt)) {
			state.getWorld().rayCast((fixture, point, normal, fraction) -> {
				if (fixture.getFilterData().categoryBits == Constants.BIT_WALL && fraction < shortestFraction) {
					shortestFraction = fraction;
					return fraction;
				}
				return -1.0f;
			}, originPt, endPt);
		}

		endPt.set(originPt).add(0, -RANGE * shortestFraction).scl(PPM);
		originPt.set(endPt).add(0, RANGE);

		SyncedAttack.ANCHOR.initiateSyncedAttackSingle(state, user.getPlayer(), originPt, new Vector2(), endPt.x, endPt.y);
	}

	@Override
	public String[] getDescFields() {
		return new String[] {
			String.valueOf((int) MAX_CHARGE),
			String.valueOf((int) BASE_DAMAGE)};
	}
}
