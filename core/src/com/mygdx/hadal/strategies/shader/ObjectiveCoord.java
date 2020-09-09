package com.mygdx.hadal.strategies.shader;

import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector3;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.strategies.ShaderStrategy;

/**
 * PlayerCoord feeds the shader the player's location.
 * @author Zachary Tu
 *
 */
public class ObjectiveCoord extends ShaderStrategy {

	Vector3 screenCoord = new Vector3();
	@Override
	public void playController(PlayState state, ShaderProgram shader, float delta) {
		if (state.getObjectiveTarget() != null) {
			screenCoord.x = state.getObjectiveTarget().getPixelPosition().x;
			screenCoord.y = state.getObjectiveTarget().getPixelPosition().y;
			HadalGame.viewportCamera.project(screenCoord);
		}
		shader.setUniformf("u_objective", screenCoord);
	}
}
