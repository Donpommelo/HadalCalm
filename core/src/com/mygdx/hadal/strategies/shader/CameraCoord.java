package com.mygdx.hadal.strategies.shader;

import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector3;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.strategies.ShaderStrategy;

/**
 */
public class CameraCoord extends ShaderStrategy {

	Vector3 cameraLocation = new Vector3();
	@Override
	public void playController(PlayState state, ShaderProgram shader, float delta) {
		cameraLocation.set(state.getCamera().position);
		shader.setUniformf("u_camera", cameraLocation);
	}
}
