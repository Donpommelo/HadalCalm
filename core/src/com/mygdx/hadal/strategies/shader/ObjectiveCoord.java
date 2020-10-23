package com.mygdx.hadal.strategies.shader;

import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.strategies.ShaderStrategy;

/**
 * ObjectiveCoord feeds the shader the objective location.
 * @author Climingo Clohlrabi
 *
 */
public class ObjectiveCoord extends ShaderStrategy {

	Vector3 screenCoord = new Vector3();
	Vector2 objectiveLocation = new Vector2();
	@Override
	public void playController(PlayState state, ShaderProgram shader, float delta) {
		if (state.getUiObjective().getObjectiveTarget() != null) {
			objectiveLocation.set(state.getUiObjective().getObjectiveTarget().getPixelPosition());
			screenCoord.x = objectiveLocation.x;
			screenCoord.y = objectiveLocation.y;
			HadalGame.viewportCamera.project(screenCoord);
		}
		shader.setUniformf("u_objective", screenCoord);
	}
}
