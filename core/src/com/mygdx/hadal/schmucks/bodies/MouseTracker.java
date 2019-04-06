package com.mygdx.hadal.schmucks.bodies;

import static com.mygdx.hadal.utils.Constants.PPM;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.schmucks.UserDataTypes;
import com.mygdx.hadal.schmucks.userdata.HadalData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.utils.Constants;
import com.mygdx.hadal.utils.b2d.BodyBuilder;

/**
 * A mouse tracker is tied to a player and track's that player's mouse pointer
 * @author Zachary Tu
 *
 */
public class MouseTracker extends HadalEntity {
	
	//This tracks the location of the user's (host) mouse
	private Vector3 tmpVec3 = new Vector3();
	
	//This tracks the location of a client mouse sent by packet
	private Vector2 desiredLocation = new Vector2();
	
	//Whether this player is the host or not
	private boolean server;
	
	public MouseTracker(PlayState state, boolean server) {
		super(state, 1, 1, 0, 0);
		this.server = server;
	}

	@Override
	public void create() {
		this.hadalData = new HadalData(UserDataTypes.EVENT, this);
		this.body = BodyBuilder.createBox(world, startX, startY, width, height, 1, 1, 0, true, true, Constants.BIT_SENSOR, 
				(short) (0), (short) 0, true, hadalData);
	}

	@Override
	public void controller(float delta) {
		if (server) {
			tmpVec3.set(Gdx.input.getX(), Gdx.input.getY(), 0);
			HadalGame.viewportCamera.unproject(tmpVec3);
			setTransform(tmpVec3.x / PPM, tmpVec3.y / PPM, 0);
		} else {
			setTransform(desiredLocation.x / PPM, desiredLocation.y / PPM, 0);
		}
	}

	@Override
	public void render(SpriteBatch batch) {}

	public Vector2 getDesiredLocation() {
		return desiredLocation;
	}

	public void setDesiredLocation(int x, int y) {
		this.desiredLocation.x = x;
		this.desiredLocation.y = y;
	}
}
