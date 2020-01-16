package com.mygdx.hadal.strategies.shader;

import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.strategies.ShaderStrategy;
import com.mygdx.hadal.utils.Stats;

/**
 * @author Zachary Tu
 *
 */
public class Light extends ShaderStrategy {

	@Override
	public void controller(PlayState state, ShaderProgram shader, float delta) {
		if (state.getPlayer() != null) {
			shader.setUniformf("u_light", state.getPlayer().getPlayerData().getStat(Stats.LIGHT_RADIUS));
		}
	}
}
