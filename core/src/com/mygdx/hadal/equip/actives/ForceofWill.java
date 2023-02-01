package com.mygdx.hadal.equip.actives;

import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.equip.ActiveItem;
import com.mygdx.hadal.schmucks.entities.Player;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Invulnerability;

/**
 * @author Fibbadon Flabitha
 */
public class ForceofWill extends ActiveItem {

	private static final float MAX_CHARGE = 15.0f;
	
	private static final float DURATION = 2.0f;
	
	public ForceofWill(Player user) {
		super(user, MAX_CHARGE);
	}
	
	@Override
	public void useItem(PlayState state, PlayerBodyData user) {
		SoundEffect.MAGIC18_BUFF.playUniversal(state, user.getPlayer().getPixelPosition(), 0.5f, false);
		user.addStatus(new Invulnerability(state, DURATION, user, user));
	}
	
	@Override
	public float getUseDuration() { return DURATION; }

	@Override
	public String[] getDescFields() {
		return new String[] {
				String.valueOf((int) MAX_CHARGE),
				String.valueOf((int) DURATION)};
	}
}
