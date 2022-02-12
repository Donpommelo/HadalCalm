package com.mygdx.hadal.strategies.hitbox;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.effects.Shader;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;
import com.mygdx.hadal.strategies.HitboxStrategy;

/**
 * This strategy makes a hbox die after receiving too much damage in a short period of time.
 * @author Keneshram Krichard
 */
public class DamageThresholdDie extends HitboxStrategy {

	//this is the amount of damage that must be received to trigger death
	private final float damageThreshold;

	//the "saved" damage taken is decreased by this amount every second.
	private final float thresholdDepreciation;

	//the amount of "recent" damage this hbox has received
	private float damageCurrent;

	//the duration of each flash
	private static final float flashDuration = 0.1f;

	public DamageThresholdDie(PlayState state, Hitbox proj, BodyData user, float damageThreshold, float thresholdDepreciation) {
		super(state, proj, user);
		this.damageThreshold = damageThreshold;
		this.thresholdDepreciation = thresholdDepreciation;
	}

	@Override
	public void controller(float delta) {
		if (damageCurrent > 0.0f) {
			damageCurrent -= thresholdDepreciation * delta;
		}

		//the hbox will start flashing when ithas received too much damage in a short period of time
		final float flashLifespan = 0.75f;
		if (damageCurrent >= damageThreshold * flashLifespan) {
			if (hbox.getShaderCount() < -flashDuration) {
				hbox.setShader(Shader.WHITE, flashDuration, true);
			}
		}
	}
	
	@Override
	public void receiveDamage(BodyData perp, float baseDamage, Vector2 knockback, DamageTypes... tags) {

		damageCurrent += baseDamage;

		if (damageCurrent > damageThreshold) {
			hbox.die();
		}
	}
}
