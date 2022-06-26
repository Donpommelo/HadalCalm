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

	private static final float usecd = 0.0f;
	private static final float usedelay = 0.2f;
	private static final float maxCharge = 25.0f;
	
	private static final float duration = 8.0f;
	private static final float power = 0.04f;

	private static final float particleDuration = 1.0f;

	public Melon(Schmuck user) {
		super(user, usecd, usedelay, maxCharge);
	}
	
	@Override
	public void useItem(PlayState state, PlayerBodyData user) {
		SoundEffect.EATING.playUniversal(state, user.getPlayer().getPixelPosition(), 0.8f, false);
		new ParticleEntity(state, user.getPlayer(), Particle.KAMABOKO_IMPACT, 0.0f, particleDuration,
				true, SyncType.CREATESYNC).setColor(HadalColor.FRENCH_LIME);

		user.addStatus(new Regeneration(state, duration, user, user, power * user.getStat(Stats.MAX_HP)));
	}
	
	@Override
	public float getUseDuration() { return duration; }

	@Override
	public String[] getDescFields() {
		return new String[] {
				String.valueOf((int) maxCharge),
				String.valueOf((int) (duration * power * 100)),
				String.valueOf((int) duration)};
	}
}
