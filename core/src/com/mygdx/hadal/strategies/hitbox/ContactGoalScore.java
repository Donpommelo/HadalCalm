package com.mygdx.hadal.strategies.hitbox;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.event.modes.FootballGoal;
import com.mygdx.hadal.schmucks.bodies.Player;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.schmucks.userdata.HadalData;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;
import com.mygdx.hadal.strategies.HitboxStrategy;

/**
 * This strategy makes the hbox "score" when it comes in contact with a "goal" event.
 * hboxes die when they score
 * @author Twarvuldemar Thersula
 */
public class ContactGoalScore extends HitboxStrategy {

	//the player that last hit the "ball" to be given score credit
	private Player lastHit;

	public ContactGoalScore(PlayState state, Hitbox proj, BodyData user) {
		super(state, proj, user);
	}
	
	@Override
	public void onHit(HadalData fixB) {
		if (fixB != null) {
			if (fixB.getEntity() instanceof FootballGoal goal) {

				//scorer is null for clients so goal displays particles but no score increment
				if (state.isServer()) {
					goal.getEventData().preActivate(null, lastHit);
				} else {
					goal.getEventData().preActivate(null, null);
				}
				hbox.die();
			}
		}
	}

	@Override
	public void receiveDamage(BodyData perp, float baseDamage, Vector2 knockback, DamageTypes... tags) {
		if (perp instanceof PlayerBodyData playerData) {
			lastHit = playerData.getPlayer();
		}
	}
}
