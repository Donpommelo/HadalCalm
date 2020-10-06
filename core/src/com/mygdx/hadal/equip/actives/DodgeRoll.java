package com.mygdx.hadal.equip.actives;

import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.equip.ActiveItem;
import com.mygdx.hadal.schmucks.MoveState;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Invulnerability;

public class DodgeRoll extends ActiveItem {

	private static final float usecd = 0.0f;
	private static final float usedelay = 0.0f;
	private static final float maxCharge = 3.0f;
	
	private static final float recoil = 40.0f;
	private static final float invulnerabilityDuration = 0.5f;

	public DodgeRoll(Schmuck user) {
		super(user, usecd, usedelay, maxCharge, chargeStyle.byTime);
	}
	
	@Override
	public void useItem(PlayState state, PlayerBodyData user) {
		SoundEffect.WOOSH.playUniversal(state, user.getPlayer().getPixelPosition(), 1.0f, false);
		
		int direction;
		
		if (user.getPlayer().getMoveState().equals(MoveState.MOVE_LEFT)) {
			direction = -1;
		} else if (user.getPlayer().getMoveState().equals(MoveState.MOVE_RIGHT)) {
			direction = 1;
		} else if (weaponVelo.x > 0){
			direction = 1;
		} else {
			direction = -1;
		}
		user.addStatus(new Invulnerability(state, invulnerabilityDuration, user, user));
		
		user.getPlayer().pushMomentumMitigation(recoil * direction, 0);
	}
}
