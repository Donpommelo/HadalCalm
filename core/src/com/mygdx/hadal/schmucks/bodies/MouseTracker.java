package com.mygdx.hadal.schmucks.bodies;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.utils.Constants;
import com.mygdx.hadal.utils.b2d.BodyBuilder;

import box2dLight.RayHandler;

public class MouseTracker extends HadalEntity {

	public MouseTracker(PlayState state, World world, OrthographicCamera camera, RayHandler rays) {
		super(state, world, camera, rays, 1, 1, 0, 0);
	}

	@Override
	public void create() {
		this.body = BodyBuilder.createBox(world, startX, startY, width, height, 1, 1, 0, true, true, Constants.BIT_SENSOR, 
				(short) (0), (short) 0, true, null);
	}

	@Override
	public void controller(float delta) {
		Vector3 mouse = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
		camera.unproject(mouse);
		body.setTransform(mouse.x / 32, mouse.y / 32, 0);
	}

	@Override
	public void render(SpriteBatch batch) {
		
	}

}
