package com.mygdx.hadal.statuses;

import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.HadalColor;
import com.mygdx.hadal.schmucks.SyncType;
import com.mygdx.hadal.schmucks.entities.ParticleEntity;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;

/**
 * Magic Glow makes the schmuck glow with colorful particles
 * atm, this is just used to indicate when the hexenhowitzer weapon is fully charged.
 * @author Rubeck Rigwump
 */
public class MagicGlow extends Status {

	private float procCdCount;
	private static final float procCd = 1.0f;
	
	public MagicGlow(PlayState state, BodyData v) {
		super(state, v);
		this.procCdCount = procCd;
	}
	
	@Override
	public void timePassing(float delta) {
		super.timePassing(delta);
		if (procCdCount >= procCd) {
			procCdCount -= procCd;
			ParticleEntity particle = new ParticleEntity(state, inflicted.getSchmuck(), Particle.BRIGHT, procCd, procCd,
				true, SyncType.CREATESYNC);
			particle.setColor(HadalColor.RANDOM);
		}
		procCdCount += delta;
	}
}
