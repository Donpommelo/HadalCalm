package com.mygdx.hadal.strategies.shader;

import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.strategies.ShaderStrategy;

/**
 * ObjectiveCoord feeds the shader the objective location.
 * if there are multiple objectives, it will just give the first one
 * @author Climingo Clohlrabi
 */
public class ObjectiveCoord extends ShaderStrategy {

	private final Vector3 screenCoord = new Vector3();
	private final Vector2 objectiveLocation = new Vector2();
	@Override
	public void playController(PlayState state, ShaderProgram shader, float delta) {
		if (!state.getUIManager().getUiObjective().getObjectives().isEmpty()) {
			objectiveLocation.set(state.getUIManager().getUiObjective().getObjectives().get(0).getObjectiveLocation());
			screenCoord.x = objectiveLocation.x;
			screenCoord.y = objectiveLocation.y;
			HadalGame.viewportCamera.project(screenCoord);
		} else {
			screenCoord.setZero();
		}
		shader.setUniformf("u_objective", screenCoord);
	}
}
