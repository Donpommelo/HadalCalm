package com.mygdx.hadal.equip.actives;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.equip.ActiveItem;
import com.mygdx.hadal.event.HealingArea;
import com.mygdx.hadal.constants.SyncType;
import com.mygdx.hadal.schmucks.entities.Schmuck;
import com.mygdx.hadal.schmucks.entities.SoundEntity;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;

/**
 * @author Gludelaire Grancisco
 */
public class HealingField extends ActiveItem {

	private static final float MAX_CHARGE = 20.0f;
	
	private static final Vector2 FIELD_SIZE = new Vector2(360, 360);
	private static final float FIELD_HEAL = 0.2f;
	private static final float HEAL_DURATION = 10.0f;
	
	public HealingField(Schmuck user) {
		super(user, MAX_CHARGE);
	}
	
	@Override
	public void useItem(PlayState state, PlayerBodyData user) {	
		new SoundEntity(state, new HealingArea(state, user.getSchmuck().getPixelPosition(), FIELD_SIZE, FIELD_HEAL, HEAL_DURATION, user.getSchmuck(), (short) 0),
				SoundEffect.MAGIC21_HEAL, HEAL_DURATION, 0.25f, 1.0f, true, true, SyncType.CREATESYNC);
	}
	
	@Override
	public float getUseDuration() { return HEAL_DURATION; }

	@Override
	public String[] getDescFields() {
		return new String[] {
				String.valueOf((int) MAX_CHARGE),
				String.valueOf((int) HEAL_DURATION),
				String.valueOf((int) (FIELD_HEAL * 60))};
	}
}
