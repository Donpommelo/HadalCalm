package com.mygdx.hadal.statuses;

import com.badlogic.gdx.math.MathUtils;
import com.mygdx.hadal.schmucks.entities.PlayerBot;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;

/**
 */
public class Botting extends Status {

	final private PlayerBot bot;

	public Botting(PlayState state, PlayerBot bot) {
		super(state, bot.getBodyData());
		this.bot = bot;
	}

	@Override
	public void onKill(BodyData vic) {
		if (bot.getChatWheelDesire() > MathUtils.random()) {
			state.getChatWheel().emote(bot, MathUtils.random(7));
		}
	}
}
