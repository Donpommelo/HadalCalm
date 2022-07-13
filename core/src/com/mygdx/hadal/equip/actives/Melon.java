package com.mygdx.hadal.equip.actives;

import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.effects.HadalColor;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.equip.ActiveItem;
import com.mygdx.hadal.schmucks.SyncType;
import com.mygdx.hadal.schmucks.entities.ParticleEntity;
import com.mygdx.hadal.schmucks.entities.Schmuck;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Regeneration;
import com.mygdx.hadal.utils.Stats;

/**
 * @author Grurrault Ghineydew
 */
public class Melon extends ActiveItem {

	private static final float USECD = 0.0f;
	private static final float USEDELAY = 0.2f;
	private static final float MAX_CHARGE = 25.0f;
	
	private static final float DURATION = 8.0f;
	private static final float POWER = 0.04f;

	private static final float PARTICLE_DURATION = 1.0f;

	public Melon(Schmuck user) {
		super(user, USECD, USEDELAY, MAX_CHARGE);
	}
	
	@Override
	public void useItem(PlayState state, PlayerBodyData user) {
		SoundEffect.EATING.playUniversal(state, user.getPlayer().getPixelPosition(), 0.8f, false);
		new ParticleEntity(state, user.getPlayer(), Particle.KAMABOKO_IMPACT, 0.0f, PARTICLE_DURATION,
				true, SyncType.CREATESYNC).setColor(HadalColor.FRENCH_LIME);

		user.addStatus(new Regeneration(state, DURATION, user, user, POWER * user.getStat(Stats.MAX_HP)));
	}
	
	@Override
	public float getUseDuration() { return DURATION; }

	@Override
	public String[] getDescFields() {
		return new String[] {
				String.valueOf((int) MAX_CHARGE),
				String.valueOf((int) (DURATION * POWER * 100)),
				String.valueOf((int) DURATION)};
	}
}
