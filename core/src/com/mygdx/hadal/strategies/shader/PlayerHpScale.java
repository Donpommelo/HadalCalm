package com.mygdx.hadal.strategies.shader;

import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.mygdx.hadal.schmucks.entities.HadalEntity;
import com.mygdx.hadal.schmucks.entities.Schmuck;
import com.mygdx.hadal.strategies.ShaderStrategy;
import com.mygdx.hadal.utils.Stats;

/**
 */
public class PlayerHpScale extends ShaderStrategy {

	private static final float SpeedMax = 15.0f;
	private static final float SpeedMin = 4.0f;

	@Override
	public void shaderEntityUpdate(ShaderProgram shader, HadalEntity entity, float completion) {
		if (entity instanceof Schmuck schmuck) {
			float hpPercent = schmuck.getBodyData().getCurrentHp() / schmuck.getBodyData().getStat(Stats.MAX_HP);
			shader.setUniformf("speed", SpeedMax - hpPercent * (SpeedMax - SpeedMin) * 2);
		}
	}
}
