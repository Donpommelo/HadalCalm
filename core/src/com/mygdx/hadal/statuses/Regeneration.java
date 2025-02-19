package com.mygdx.hadal.statuses;

import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.constants.Stats;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.managers.EffectEntityManager;
import com.mygdx.hadal.requests.ParticleCreate;
import com.mygdx.hadal.requests.SoundCreate;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;

/**
 * Schmucks with Regeneration gradually heal over time
 * This is pretty much just a stat boost to hp regen with sound and particles built in.
 * @author Svortellini Sporzvlak
 */
public class Regeneration extends Status {

	//this is the power of the heal
	private final float heal;

	public Regeneration(PlayState state, float i, BodyData p, BodyData v, float heal) {
		super(state, i, false, p, v);
		this.heal = heal;
	}
	
	@Override
	public void onInflict() {
		//the sound and particles attached to the status
		EffectEntityManager.getParticle(state, new ParticleCreate(Particle.REGEN, inflicted.getSchmuck())
				.setLifespan(duration));

		EffectEntityManager.getSound(state, new SoundCreate(SoundEffect.MAGIC21_HEAL, inflicted.getSchmuck())
				.setLifespan(duration)
				.setVolume(0.25f));
	}

	@Override
	public void statChanges() {
		inflicted.setStat(Stats.HP_REGEN, inflicted.getStat(Stats.HP_REGEN) + heal);
	}
}
