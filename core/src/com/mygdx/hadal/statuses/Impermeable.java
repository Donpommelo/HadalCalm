package com.mygdx.hadal.statuses;

import com.badlogic.gdx.physics.box2d.Filter;
import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.schmucks.SyncType;
import com.mygdx.hadal.schmucks.entities.ParticleEntity;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.utils.Constants;

/**
 * Impermeable units are like invisible units, except they also pass through characters and hitboxes
 * @author Derkhammer Dankabourne
 */
public class Impermeable extends Status {

	//fade time determines the window of time where the player can attack before the invisibility status is removed
	private static final float FADE_TIME = 0.5f;
	private float fadeCount;

	public Impermeable(PlayState state, float i, BodyData p, BodyData v) {
		super(state, i, false, p, v);
		new ParticleEntity(state, inflicted.getSchmuck(), Particle.SMOKE, 1.0f, 3.0f, true, SyncType.CREATESYNC)
				.setScale(0.2f);
		
		//set unit's invisibility to true. this is used to turn off movement particles
		if (inflicted instanceof PlayerBodyData playerData) {
			playerData.getPlayer().setInvisible(3);
		}
		
		fadeCount = FADE_TIME;
	}
	
	@Override
	public void timePassing(float delta) {
		super.timePassing(delta);
		if (fadeCount >= 0) {
			fadeCount -= delta;
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
		new ParticleEntity(state, inflicted.getSchmuck(), Particle.SMOKE, 1.0f, 3.0f, true, SyncType.CREATESYNC).setScale(0.4f);
		
		if (inflicted instanceof PlayerBodyData playerData) {
			playerData.getPlayer().setInvisible(0);
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
			playerData.getPlayer().setInvisible(0);
		}
	}
	
	@Override
	public statusStackType getStackType() {
		return statusStackType.REPLACE;
	}
}
