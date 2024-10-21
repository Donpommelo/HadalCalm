package com.mygdx.hadal.statuses;

import com.badlogic.gdx.physics.box2d.Filter;
import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.battle.DamageTag;
import com.mygdx.hadal.constants.BodyConstants;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.managers.EffectEntityManager;
import com.mygdx.hadal.requests.ParticleCreate;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;

/**
 * Impermeable units are like invisible units, except they also pass through characters and hitboxes
 * @author Derkhammer Dankabourne
 */
public class Impermeable extends Status {

	public Impermeable(PlayState state, float i, BodyData p, BodyData v) {
		super(state, i, false, p, v);

		EffectEntityManager.getParticle(state, new ParticleCreate(Particle.SMOKE, inflicted.getSchmuck())
				.setLifespan(1.0f)
				.setScale(0.4f));

		//set unit's invisibility to true. this is used to turn off movement particles
		if (inflicted instanceof PlayerBodyData playerData) {
			playerData.getPlayer().getEffectHelper().setTransparent(true);
		}
	}

	@Override
	public void onInflict() {
		if (inflicted.getSchmuck().getMainFixture() != null) {
			Filter filter = inflicted.getSchmuck().getMainFixture().getFilterData();
			filter.maskBits = (short) (filter.maskBits &~ BodyConstants.BIT_PROJECTILE &~ BodyConstants.BIT_PLAYER &~ BodyConstants.BIT_ENEMY);
			inflicted.getSchmuck().getMainFixture().setFilterData(filter);
		}
	}
	
	@Override
	public void onRemove() {
		EffectEntityManager.getParticle(state, new ParticleCreate(Particle.SMOKE, inflicted.getSchmuck())
				.setLifespan(1.0f)
				.setScale(0.4f));

		if (inflicted instanceof PlayerBodyData playerData) {
			playerData.getPlayer().getEffectHelper().setTransparent(false);
		}

		if (inflicted.getSchmuck().getMainFixture() != null) {
			Filter filter = inflicted.getSchmuck().getMainFixture().getFilterData();
			filter.maskBits = (short) (filter.maskBits | BodyConstants.BIT_PROJECTILE | BodyConstants.BIT_PLAYER | BodyConstants.BIT_ENEMY);
			inflicted.getSchmuck().getMainFixture().setFilterData(filter);
		}
	}
	
	@Override
	public void onDeath(BodyData perp, DamageSource source, DamageTag... tags) {
		if (inflicted instanceof PlayerBodyData playerData) {
			playerData.getPlayer().getEffectHelper().setTransparent(false);
		}
	}
	
	@Override
	public statusStackType getStackType() {
		return statusStackType.REPLACE;
	}
}
