package com.mygdx.hadal.equip.actives;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.equip.ActiveItem;
import com.mygdx.hadal.event.HealingArea;
import com.mygdx.hadal.schmucks.SyncType;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.schmucks.bodies.SoundEntity;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;

/**
 * @author Gludelaire Grancisco
 */
public class HealingField extends ActiveItem {

	private static final float usecd = 0.0f;
	private static final float usedelay = 0.0f;
	private static final float maxCharge = 20.0f;
	
	private static final Vector2 fieldSize = new Vector2(360, 360);
	private static final float fieldHeal = 0.2f;
	private static final float healDuration = 10.0f;
	
	public HealingField(Schmuck user) {
		super(user, usecd, usedelay, maxCharge, chargeStyle.byDamageInflict);
	}
	
	@Override
	public void useItem(PlayState state, PlayerBodyData user) {	
		new SoundEntity(state, new HealingArea(state, user.getSchmuck().getPixelPosition(), fieldSize, fieldHeal, healDuration, user.getSchmuck(), (short) 0),
				SoundEffect.MAGIC21_HEAL, 0.25f, 1.0f, true, true, SyncType.CREATESYNC);
	}
	
	@Override
	public float getUseDuration() { return healDuration; }
}
