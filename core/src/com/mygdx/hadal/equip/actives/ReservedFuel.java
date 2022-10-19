package com.mygdx.hadal.equip.actives;

import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.effects.HadalColor;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.equip.ActiveItem;
import com.mygdx.hadal.constants.SyncType;
import com.mygdx.hadal.schmucks.entities.ParticleEntity;
import com.mygdx.hadal.schmucks.entities.Schmuck;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.StatChangeStatus;
import com.mygdx.hadal.constants.Stats;

/**
 * @author Foworth Frogwump
 */
public class ReservedFuel extends ActiveItem {

	private static final float USECD = 0.0f;
	private static final float USEDELAY = 0.0f;
	private static final float MAX_CHARGE = 15.0f;
	
	private static final float DURATION = 5.0f;
	private static final float POWER = 18.0f;
	
	public ReservedFuel(Schmuck user) {
		super(user, USECD, USEDELAY, MAX_CHARGE);
	}
	
	@Override
	public void useItem(PlayState state, PlayerBodyData user) {
		SoundEffect.MAGIC2_FUEL.playUniversal(state, user.getPlayer().getPixelPosition(), 0.5f, false);
		new ParticleEntity(state, user.getSchmuck(), Particle.BRIGHT, 1.0f, DURATION, true, SyncType.CREATESYNC).setColor(
			HadalColor.BLUE);
		user.addStatus(new StatChangeStatus(state, DURATION, Stats.FUEL_REGEN, POWER, user, user));
	}
	
	@Override
	public float getUseDuration() { return DURATION; }

	@Override
	public String[] getDescFields() {
		return new String[] {
				String.valueOf((int) MAX_CHARGE),
				String.valueOf((int) (POWER * DURATION)),
				String.valueOf((int) DURATION)};
	}
}
