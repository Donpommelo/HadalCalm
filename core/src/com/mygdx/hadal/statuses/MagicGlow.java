package com.mygdx.hadal.statuses;

import com.mygdx.hadal.effects.HadalColor;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.managers.EffectEntityManager;
import com.mygdx.hadal.requests.ParticleCreate;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;

/**
 * Magic Glow makes the schmuck glow with colorful particles
 * atm, this is just used to indicate when the hexenhowitzer weapon is fully charged.
 * @author Rubeck Rigwump
 */
public class MagicGlow extends Status {

	private float procCdCount;
	private static final float PROC_CD = 1.0f;
	
	public MagicGlow(PlayState state, BodyData v) {
		super(state, v);
		this.procCdCount = PROC_CD;
	}
	
	@Override
	public void timePassing(float delta) {
		super.timePassing(delta);
		if (procCdCount >= PROC_CD) {
			procCdCount -= PROC_CD;

			EffectEntityManager.getParticle(state, new ParticleCreate(Particle.BRIGHT, inflicted.getSchmuck())
					.setLifespan(PROC_CD)
					.setColor(HadalColor.RANDOM));
		}
		procCdCount += delta;
	}
}
