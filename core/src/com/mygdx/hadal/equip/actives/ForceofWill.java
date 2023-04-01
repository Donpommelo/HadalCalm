package com.mygdx.hadal.equip.actives;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.battle.SyncedAttack;
import com.mygdx.hadal.equip.ActiveItem;
import com.mygdx.hadal.schmucks.entities.Player;
import com.mygdx.hadal.schmucks.entities.Schmuck;
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
		SyncedAttack.FORCE_OF_WILL.initiateSyncedAttackNoHbox(state, user.getPlayer(), user.getPlayer().getPixelPosition(), true);

	}

	public static void createForceOfWill(PlayState state, Schmuck user, Vector2 startPosition) {
		SoundEffect.MAGIC18_BUFF.playSourced(state, startPosition, 0.5f);
		user.getBodyData().addStatus(new Invulnerability(state, DURATION, user.getBodyData(), user.getBodyData()));
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
