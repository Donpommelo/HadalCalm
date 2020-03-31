package com.mygdx.hadal.equip.actives;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.equip.ActiveItem;
import com.mygdx.hadal.event.HealingArea;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;

public class HealingField extends ActiveItem {

	private final static float usecd = 0.0f;
	private final static float usedelay = 0.0f;
	private final static float maxCharge = 150.0f;
	
	private final static Vector2 fieldSize = new Vector2(360, 360);
	private final static float fieldHeal = 6 / 60f;
	private final static float healDuration = 10.0f;
	
	public HealingField(Schmuck user) {
		super(user, usecd, usedelay, maxCharge, chargeStyle.byDamageInflict);
	}
	
	@Override
	public void useItem(PlayState state, final PlayerBodyData user) {	
		new HealingArea(state, user.getSchmuck().getPixelPosition(), fieldSize, fieldHeal, healDuration, user.getSchmuck(), (short)0);
	}
	
	@Override
	public float getUseDuration() { return healDuration; }
}
