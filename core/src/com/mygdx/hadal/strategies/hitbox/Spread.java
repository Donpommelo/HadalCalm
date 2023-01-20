package com.mygdx.hadal.strategies.hitbox;

import com.badlogic.gdx.math.MathUtils;
import com.mygdx.hadal.schmucks.entities.PlayerClientOnHost;
import com.mygdx.hadal.schmucks.entities.PlayerSelfOnClient;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.strategies.HitboxStrategy;

/**
 * This strategy randomly changes a hbox's velocity after it is initiated.
 * @author Damp Darbchamp
 */
public class Spread extends HitboxStrategy {
	
	public Spread(PlayState state, Hitbox proj, BodyData user, int spread) {
		super(state, proj, user);

		//set start velo upon initializing so trajectory can be sent with Synced Attack
		//this is run by clients for themselves and by host for everyone else
		if ((hbox.getState().isServer() && !(user.getSchmuck() instanceof PlayerClientOnHost)) ||
				(!hbox.getState().isServer() && user.getSchmuck() instanceof PlayerSelfOnClient)) {
			float newDegrees = hbox.getStartVelo().angleDeg() + (MathUtils.random(-spread, spread + 1));
			hbox.setStartVelo(hbox.getStartVelo().setAngleDeg(newDegrees));
		}
	}
}
