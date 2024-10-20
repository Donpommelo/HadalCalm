package com.mygdx.hadal.statuses;

import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.constants.ObjectLayer;
import com.mygdx.hadal.constants.Stats;
import com.mygdx.hadal.constants.SyncType;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.schmucks.entities.ParticleEntity;
import com.mygdx.hadal.schmucks.entities.SoundEntity;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.ClientState;
import com.mygdx.hadal.states.PlayState;

/**
 * Schmucks with Regeneration gradually heal over time
 * This is pretty much just a stat boost to hp regen with sound and particles built in.
 * @author Svortellini Sporzvlak
 */
public class Regeneration extends Status {

	private static final float LINGER = 1.0f;

	//this is the power of the heal
	private final float heal;

	public Regeneration(PlayState state, float i, BodyData p, BodyData v, float heal) {
		super(state, i, false, p, v);
		this.heal = heal;
	}
	
	@Override
	public void onInflict() {
		//the sound and particles attached to the status
		ParticleEntity particle = new ParticleEntity(state, inflicted.getSchmuck(), Particle.REGEN, LINGER, duration + LINGER,
				true, SyncType.NOSYNC).setPrematureOff(LINGER);
		SoundEntity sound = new SoundEntity(state, inflicted.getSchmuck(), SoundEffect.MAGIC21_HEAL, duration, 0.25f, 1.0f,
				true, true, SyncType.NOSYNC);

		if (!state.isServer()) {
			((ClientState) state).addEntity(particle.getEntityID(), particle, false, ObjectLayer.EFFECT);
			((ClientState) state).addEntity(sound.getEntityID(), sound, false, ObjectLayer.EFFECT);
		}
	}

	@Override
	public void statChanges() {
		inflicted.setStat(Stats.HP_REGEN, inflicted.getStat(Stats.HP_REGEN) + heal);
	}
}
