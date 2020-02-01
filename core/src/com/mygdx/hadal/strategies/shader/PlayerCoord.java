package com.mygdx.hadal.strategies.shader;

import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector3;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.strategies.ShaderStrategy;

/**
 * @author Zachary Tu
 *
 */
public class PlayerCoord extends ShaderStrategy {

	Vector3 screenCoord = new Vector3();
	@Override
	public void controller(PlayState state, ShaderProgram shader, float delta) {
		if (state.getPlayer() != null) {
			screenCoord.x = state.getPlayer().getPixelPosition().x;
			screenCoord.y = state.getPlayer().getPixelPosition().y;
			state.camera.project(screenCoord);
			shader.setUniformf("u_player", screenCoord);
		}
	}
}
