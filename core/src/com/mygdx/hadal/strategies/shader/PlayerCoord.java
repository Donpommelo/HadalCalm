package com.mygdx.hadal.strategies.shader;

import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.strategies.ShaderStrategy;

/**
 * PlayerCoord feeds the shader the player's location.
 * @author Nargurnaise Neldufinder
 *
 */
public class PlayerCoord extends ShaderStrategy {

	Vector3 screenCoord = new Vector3();
	Vector2 playerLocation = new Vector2();
	@Override
	public void playController(PlayState state, ShaderProgram shader, float delta) {
		if (state.getPlayer() != null) {
			playerLocation.set(state.getPlayer().getPixelPosition());
			screenCoord.x = playerLocation.x;
			screenCoord.y = playerLocation.y;
			HadalGame.viewportCamera.project(screenCoord);
			shader.setUniformf("u_player", screenCoord);
		}
	}
}
