package com.mygdx.hadal.schmucks.bodies;

import static com.mygdx.hadal.utils.Constants.PPM;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.schmucks.UserDataTypes;
import com.mygdx.hadal.schmucks.userdata.HadalData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.utils.Constants;
import com.mygdx.hadal.utils.b2d.BodyBuilder;

import box2dLight.RayHandler;

public class MouseTracker extends HadalEntity {

	private Vector3 tmpVec3 = new Vector3();
	
	public MouseTracker(PlayState state, World world, OrthographicCamera camera, RayHandler rays) {
		super(state, world, camera, rays, 1, 1, 0, 0);
	}

	@Override
	public void create() {
		this.hadalData = new HadalData(world, UserDataTypes.EVENT, this);

		this.body = BodyBuilder.createBox(world, startX, startY, width, height, 1, 1, 0, true, true, Constants.BIT_SENSOR, 
				(short) (0), (short) 0, true, hadalData);
		
	}

	@Override
	public void controller(float delta) {
		tmpVec3.set(Gdx.input.getX(), Gdx.input.getY(), 0);
		HadalGame.viewportCamera.unproject(tmpVec3);

		body.setTransform(tmpVec3.x / PPM, tmpVec3.y / PPM, 0);
	}

	@Override
	public void render(SpriteBatch batch) {

	}

}
