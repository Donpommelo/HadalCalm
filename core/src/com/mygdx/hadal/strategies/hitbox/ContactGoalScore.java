package com.mygdx.hadal.strategies.hitbox;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.event.FootballGoal;
import com.mygdx.hadal.event.userdata.EventData;
import com.mygdx.hadal.schmucks.bodies.Player;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.schmucks.userdata.HadalData;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;
import com.mygdx.hadal.strategies.HitboxStrategy;

/**
 *
 */
public class ContactGoalScore extends HitboxStrategy {

	private Player lastHit;
	public ContactGoalScore(PlayState state, Hitbox proj, BodyData user) {
		super(state, proj, user);
	}
	
	@Override
	public void onHit(HadalData fixB) {
		if (fixB != null) {
			if (fixB.getEntity() instanceof FootballGoal) {
				((EventData) fixB).preActivate(null, lastHit);
				hbox.die();
			}
		}
	}

	@Override
	public void receiveDamage(BodyData perp, float basedamage, Vector2 knockback, DamageTypes... tags) {
		if (perp instanceof PlayerBodyData) {
			lastHit = (Player) perp.getSchmuck();
		}
	}
}
