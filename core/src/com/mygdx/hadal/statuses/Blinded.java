package com.mygdx.hadal.statuses;

import com.badlogic.gdx.math.MathUtils;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;


public class Blinded extends Status {

	private static final float fadeCooldown = 0.5f;
	private float fadeTimer = fadeCooldown;
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
	}

	@Override
	public void onRemove() {
		if (inflicted instanceof PlayerBodyData playerData) {
			playerData.getPlayer().setBlinded(0);
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
	public static float getBlindAmount(float blindDuration) {
		if (blindDuration > blindFadeDuration) {
			return maxBlind;
		}
		if (blindDuration > blindFadeThreshold1) {
			return MathUtils.lerp(threshold1Blind, maxBlind, (blindDuration - blindFadeThreshold1) /
					(blindFadeDuration - blindFadeThreshold1));
		}
		return MathUtils.lerp(0, threshold1Blind, blindDuration / blindFadeThreshold1);
	}

	@Override
	public void setDuration(float duration) {
		this.duration = Math.min(blindFadeDuration + fadeTimer, duration);
		fadeTimer = fadeCooldown;
	}

	@Override
	public statusStackType getStackType() {
		return statusStackType.INCREMENT_DURATION;
	}
}
