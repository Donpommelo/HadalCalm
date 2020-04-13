package com.mygdx.hadal.equip.actives;

import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.equip.ActiveItem;
import com.mygdx.hadal.equip.Equipable;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;

public class Reloader extends ActiveItem {

	private final static float usecd = 0.0f;
	private final static float usedelay = 0.0f;
	private final static float maxCharge = 300.0f;
	
	public Reloader(Schmuck user) {
		super(user, usecd, usedelay, maxCharge, chargeStyle.byDamageInflict);
	}
	
	@Override
	public void useItem(PlayState state, PlayerBodyData user) {
		SoundEffect.RELOAD.playUniversal(state, user.getPlayer().getPixelPosition(), 0.4f, false);
		
		for (Equipable e : user.getMultitools()) {
			e.gainClip(100);
			e.gainAmmo(0.5f);
		}
	}
}
