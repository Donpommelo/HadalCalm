package com.mygdx.hadal.statuses;

import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.HadalColor;
import com.mygdx.hadal.constants.SyncType;
import com.mygdx.hadal.schmucks.entities.ParticleEntity;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.ClientState;
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
			ParticleEntity particle = new ParticleEntity(state, inflicted.getSchmuck(), Particle.BRIGHT, PROC_CD, PROC_CD,
				true, SyncType.NOSYNC);
			particle.setColor(HadalColor.RANDOM);

			if (!state.isServer()) {
				((ClientState) state).addEntity(particle.getEntityID(), particle, false, PlayState.ObjectLayer.EFFECT);
			}
		}
		procCdCount += delta;
	}
}
