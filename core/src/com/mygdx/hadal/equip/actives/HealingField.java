package com.mygdx.hadal.equip.actives;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.equip.ActiveItem;
import com.mygdx.hadal.event.HealingArea;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.schmucks.bodies.SoundEntity;
import com.mygdx.hadal.schmucks.bodies.SoundEntity.soundSyncType;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;

public class HealingField extends ActiveItem {

	private final static float usecd = 0.0f;
	private final static float usedelay = 0.0f;
	private final static float maxCharge = 20.0f;
	
	private final static Vector2 fieldSize = new Vector2(360, 360);
	private final static float fieldHeal = 0.2f;
	private final static float healDuration = 10.0f;
	
	public HealingField(Schmuck user) {
		super(user, usecd, usedelay, maxCharge, chargeStyle.byDamageInflict);
	}
	
	@Override
	public void useItem(PlayState state, PlayerBodyData user) {	
		new SoundEntity(state, new HealingArea(state, user.getSchmuck().getPixelPosition(), fieldSize, fieldHeal, healDuration, user.getSchmuck(), (short) 0),
				SoundEffect.MAGIC21_HEAL, 0.25f, 1.0f, true, true, soundSyncType.TICKSYNC);
	}
	
	@Override
	public float getUseDuration() { return healDuration; }
}
