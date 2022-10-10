package com.mygdx.hadal.strategies.shader;

import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.mygdx.hadal.schmucks.entities.HadalEntity;
import com.mygdx.hadal.schmucks.entities.Schmuck;
import com.mygdx.hadal.strategies.ShaderStrategy;
import com.mygdx.hadal.constants.Stats;

/**
 */
public class PlayerHpScale extends ShaderStrategy {

	private static final float SPEED_MAX = 15.0f;
	private static final float SPEED_MIN = 4.0f;

	@Override
	public void shaderEntityUpdate(ShaderProgram shader, HadalEntity entity, float completion) {
		if (entity instanceof Schmuck schmuck) {
			float hpPercent = schmuck.getBodyData().getCurrentHp() / schmuck.getBodyData().getStat(Stats.MAX_HP);
			shader.setUniformf("speed", SPEED_MAX - hpPercent * (SPEED_MAX - SPEED_MIN) * 2);
		}
	}
}
