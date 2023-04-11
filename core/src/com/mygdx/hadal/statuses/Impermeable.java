package com.mygdx.hadal.statuses;

import com.badlogic.gdx.physics.box2d.Filter;
import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.constants.SyncType;
import com.mygdx.hadal.schmucks.entities.ParticleEntity;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.ClientState;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.constants.Constants;

/**
 * Impermeable units are like invisible units, except they also pass through characters and hitboxes
 * @author Derkhammer Dankabourne
 */
public class Impermeable extends Status {

	public Impermeable(PlayState state, float i, BodyData p, BodyData v) {
		super(state, i, false, p, v);

		ParticleEntity particle = new ParticleEntity(state, inflicted.getSchmuck(), Particle.SMOKE, 1.0f, 3.0f,
				true, SyncType.NOSYNC).setScale(0.4f);
		if (!state.isServer()) {
			((ClientState) state).addEntity(particle.getEntityID(), particle, false, PlayState.ObjectLayer.EFFECT);
		}
		
		//set unit's invisibility to true. this is used to turn off movement particles
		if (inflicted instanceof PlayerBodyData playerData) {
			playerData.getPlayer().getEffectHelper().setTransparent(true);
		}
	}

	@Override
	public void onInflict() {
		if (inflicted.getSchmuck().getMainFixture() != null) {
			Filter filter = inflicted.getSchmuck().getMainFixture().getFilterData();
			filter.maskBits = (short) (filter.maskBits &~ Constants.BIT_PROJECTILE &~ Constants.BIT_PLAYER &~ Constants.BIT_ENEMY);
			inflicted.getSchmuck().getMainFixture().setFilterData(filter);
		}
	}
	
	@Override
	public void onRemove() {
		ParticleEntity particle = new ParticleEntity(state, inflicted.getSchmuck(), Particle.SMOKE, 1.0f, 3.0f,
				true, SyncType.NOSYNC).setScale(0.4f);
		if (!state.isServer()) {
			((ClientState) state).addEntity(particle.getEntityID(), particle, false, PlayState.ObjectLayer.EFFECT);
		}

		if (inflicted instanceof PlayerBodyData playerData) {
			playerData.getPlayer().getEffectHelper().setTransparent(false);
		}

		if (inflicted.getSchmuck().getMainFixture() != null) {
			Filter filter = inflicted.getSchmuck().getMainFixture().getFilterData();
			filter.maskBits = (short) (filter.maskBits | Constants.BIT_PROJECTILE | Constants.BIT_PLAYER | Constants.BIT_ENEMY);
			inflicted.getSchmuck().getMainFixture().setFilterData(filter);
		}
	}
	
	@Override
	public void onDeath(BodyData perp, DamageSource source) {
		if (inflicted instanceof PlayerBodyData playerData) {
			playerData.getPlayer().getEffectHelper().setTransparent(false);
		}
	}
	
	@Override
	public statusStackType getStackType() {
		return statusStackType.REPLACE;
	}
}
