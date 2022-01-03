package com.mygdx.hadal.statuses;

import com.badlogic.gdx.math.MathUtils;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.schmucks.SyncType;
import com.mygdx.hadal.schmucks.bodies.ParticleEntity;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;


public class Blinded extends Status {

	private static final float fadeCooldown = 0.5f;
	private float fadeTimer = fadeCooldown;

	//this particle entity follows the player
	private ParticleEntity blind;

	public Blinded(PlayState state, float i, BodyData p, BodyData v) {
		super(state, i, false, p, v);

		if (inflicted instanceof PlayerBodyData playerData) {
			playerData.getPlayer().setBlinded(i);
		}
	}
	
	@Override
	public void timePassing(float delta) {
		if (fadeTimer > 0.0f) {
			fadeTimer -= delta;
		}
		if (fadeTimer <= 0.0f) {
			super.timePassing(delta);
		}
		if (inflicted instanceof PlayerBodyData playerData) {
			playerData.getPlayer().setBlinded(duration);
		}

		if (blind == null) {
			blind = new ParticleEntity(state, inflicted.getSchmuck(), Particle.BLIND, duration, duration, true, SyncType.CREATESYNC);
			blind.setOffset(0, inflicted.getSchmuck().getSize().y / 2);
		}
	}

	@Override
	public void onRemove() {
		if (inflicted instanceof PlayerBodyData playerData) {
			playerData.getPlayer().setBlinded(0);
		}
		if (blind != null) {
			blind.setDespawn(true);
			blind.turnOff();
		}
	}
	
	@Override
	public void onDeath(BodyData perp) {
		if (inflicted instanceof PlayerBodyData playerData) {
			playerData.getPlayer().setBlinded(0);
		}
	}

	public static final float blindFadeDuration = 4.0f;
	public static final float blindFadeThreshold1 = 3.75f;
	public static final float botBlindThreshold = 2.0f;
	public static final float maxBlind = 1.0f;
	public static final float threshold1Blind = 0.9f;

	/**
	 * This determines how "blind" a character should be depending on the duration remaining of the status
	 * @param blindDuration: duration remaining of blind
	 * @return blind amount (1 being max amount and 0 being no blind at all)
	 */
	public static float getBlindAmount(float blindDuration) {

		//if blind duration is more than fade duration, cap blind amount at 1
		if (blindDuration > blindFadeDuration) {
			return maxBlind;
		}

		//while fading, blind lerps towards in 2 separate stages
		if (blindDuration > blindFadeThreshold1) {
			return MathUtils.lerp(threshold1Blind, maxBlind, (blindDuration - blindFadeThreshold1) /
					(blindFadeDuration - blindFadeThreshold1));
		}
		return MathUtils.lerp(0, threshold1Blind, blindDuration / blindFadeThreshold1);
	}

	@Override
	public void setDuration(float duration) {
		this.duration = Math.min(blindFadeDuration + fadeTimer, duration);

		//reset fade timer so stacking blind doesn't make it flicker
		fadeTimer = fadeCooldown;
	}

	@Override
	public statusStackType getStackType() {
		return statusStackType.INCREMENT_DURATION;
	}
}
