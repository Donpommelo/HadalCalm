package com.mygdx.hadal.statuses;

import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.schmucks.bodies.Player;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;

/**
 * Temporary designates a schmuck as a summon that awards a summoner the score/kills it gets
 * @author Zachary Tu
 *
 */
public class Summoned extends Status {

	private Player summoner;
	
	public Summoned(PlayState state, BodyData i, Player summoner) {
		super(state, i);
		this.summoner = summoner;
	}
	
	@Override
	public void onKill(BodyData vic) {
		if (vic.getSchmuck() instanceof Player) {
			HadalGame.server.registerKill(summoner, null);
		}
	}
}
