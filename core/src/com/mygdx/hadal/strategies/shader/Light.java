package com.mygdx.hadal.strategies.shader;

import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.strategies.ShaderStrategy;
import com.mygdx.hadal.constants.Stats;

/**
 * Light feeds the shader the player's light radius stat
 * This is used for lighting shaders that shine brighter when the player has certain items equipped. 
 * @author Manjamin Mundotticini
 */
public class Light extends ShaderStrategy {

	@Override
	public void playController(PlayState state, ShaderProgram shader, float delta) {
		if (state.getPlayer() != null) {
			if (state.getPlayer().getPlayerData() != null) {
				shader.setUniformf("u_light", state.getPlayer().getPlayerData().getStat(Stats.LIGHT_RADIUS));
			}
		}
	}
}
