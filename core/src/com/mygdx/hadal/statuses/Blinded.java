package com.mygdx.hadal.statuses;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.battle.DamageTag;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.managers.EffectEntityManager;
import com.mygdx.hadal.requests.ParticleCreate;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;

/**
 * Blind debuff is inflicted by certain artifacts or active items.
 * This causes the screen to fade in from white.
 */
public class Blinded extends Status {

	private static final float FADE_COOLDOWN = 0.5f;
	private float fadeTimer = FADE_COOLDOWN;

	public Blinded(PlayState state, float i, BodyData p, BodyData v, boolean instant) {
		super(state, i, false, p, v);

		if (instant && inflicted instanceof PlayerBodyData playerBodyData) {
			playerBodyData.getPlayer().setBlinded(i);
		}
	}

	@Override
	public void onInflict() {
		EffectEntityManager.getParticle(state, new ParticleCreate(Particle.BLIND, inflicted.getSchmuck())
				.setLifespan(duration)
				.setShowOnInvis(true)
				.setOffset(new Vector2(0, inflicted.getSchmuck().getSize().y / 2)));
	}

	public static final float BLIND_INTERPOLATION = 0.05f;
	@Override
	public void timePassing(float delta) {
		if (fadeTimer > 0.0f) {
			fadeTimer -= delta;
		}
		if (fadeTimer <= 0.0f) {
			super.timePassing(delta);
		}
		if (inflicted instanceof PlayerBodyData playerData) {
			float currentDuration = playerData.getPlayer().getBlinded();
			playerData.getPlayer().setBlinded(currentDuration + (duration - currentDuration) * BLIND_INTERPOLATION);
		}
	}

	@Override
	public void onRemove() {
		if (inflicted instanceof PlayerBodyData playerData) {
			playerData.getPlayer().setBlinded(0);
		}
	}
	
	@Override
	public void onDeath(BodyData perp, DamageSource source, DamageTag... tags) {
		if (inflicted instanceof PlayerBodyData playerData) {
			playerData.getPlayer().setBlinded(0);
		}
	}

	public static final float BLIND_FADE_DURATION = 4.5f;
	public static final float BLIND_FADE_THRESHOLD_1 = 4.0f;
	public static final float BOT_BLIND_THRESHOLD = 2.0f;
	public static final float MAX_BLIND = 1.0f;
	public static final float THRESHOLD_1_BLIND = 0.9f;
	/**
	 * This determines how "blind" a character should be depending on the duration remaining of the status
	 * @param blindDuration: duration remaining of blind
	 * @return blind amount (1 being max amount and 0 being no blind at all)
	 */
	public static float getBlindAmount(float blindDuration) {

		//if blind duration is more than fade duration, cap blind amount at 1
		if (blindDuration > BLIND_FADE_DURATION) {
			return MAX_BLIND;
		}

		//while fading, blind lerps towards in 2 separate stages
		if (blindDuration > BLIND_FADE_THRESHOLD_1) {
			return MathUtils.lerp(THRESHOLD_1_BLIND, MAX_BLIND, (blindDuration - BLIND_FADE_THRESHOLD_1) /
					(BLIND_FADE_DURATION - BLIND_FADE_THRESHOLD_1));
		}
		return MathUtils.lerp(0, THRESHOLD_1_BLIND, blindDuration / BLIND_FADE_THRESHOLD_1);
	}

	@Override
	public void setDuration(float duration) {
		this.duration = Math.min(BLIND_FADE_DURATION + fadeTimer, duration);

		//reset fade timer so stacking blind doesn't make it flicker
		fadeTimer = FADE_COOLDOWN;

		EffectEntityManager.getParticle(state, new ParticleCreate(Particle.BLIND, inflicted.getSchmuck())
				.setLifespan(duration)
				.setShowOnInvis(true)
				.setOffset(new Vector2(0, inflicted.getSchmuck().getSize().y / 2)));
	}

	@Override
	public statusStackType getStackType() {
		return statusStackType.INCREMENT_DURATION;
	}
}
