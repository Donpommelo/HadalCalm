package com.mygdx.hadal.statuses;

import com.badlogic.gdx.math.MathUtils;
import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.battle.DamageTag;
import com.mygdx.hadal.schmucks.entities.PlayerBot;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;

/**
 * This is a status automatically applied to bot players.
 * This is used for bot effects that can easily be attached to status proc times.
 * Atm, this handles bot emoting.
 * @author Yogwump Yeetcorn
 */
public class Botting extends Status {

	private final PlayerBot bot;

	public Botting(PlayState state, PlayerBot bot) {
		super(state, bot.getBodyData());
		this.bot = bot;
		this.setServerOnly(true);
	}

	@Override
	public void onKill(BodyData vic, DamageSource source, DamageTag... tags) {
		if (bot.getChatWheelDesire() > MathUtils.random()) {
			state.getUIManager().getChatWheel().emote(bot, MathUtils.random(7), bot.getUser().getConnID());
		}
	}
}
