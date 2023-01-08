package com.mygdx.hadal.schmucks.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.constants.UserDataType;
import com.mygdx.hadal.schmucks.userdata.HadalData;
import com.mygdx.hadal.states.PlayState;

import static com.mygdx.hadal.constants.Constants.PPM;

/**
 * A mouse tracker is tied to a player and tracks that player's mouse pointer
 * @author Narsabaum Nolfner
 */
public class MouseTracker extends HadalEntity {

	//this is the mouse's position (instead of body position to avoid transforming)
	private final Vector2 mousePosition = new Vector2();

	//This tracks the location of the user's (host) mouse
	private final Vector3 tmpVec3 = new Vector3();

	//This tracks the location of a client mouse sent by packet
	private final Vector2 desiredLocation = new Vector2();

	//Whether this player is the host or not
	private final boolean server;

	public MouseTracker(PlayState state, boolean server) {
		super(state, new Vector2(), new Vector2(1, 1));
		this.server = server;
	}

	@Override
	public void create() {
		this.hadalData = new HadalData(UserDataType.EVENT, this);
	}

	@Override
	public void controller(float delta) {

		//server player's mouse sets location constantly. Client's mouse moves to desired location which is set when receiving packets from respective client
		if (server) {
			tmpVec3.set(Gdx.input.getX(), Gdx.input.getY(), 0);
			HadalGame.viewportCamera.unproject(tmpVec3);
			mousePosition.set(tmpVec3.x / PPM, tmpVec3.y / PPM);
		} else {
			mousePosition.set(desiredLocation.x / PPM, desiredLocation.y / PPM);
		}
	}

	@Override
	public void clientController(float delta) {
		controller(delta);
	}

	@Override
	public void render(SpriteBatch batch) {}

	//we want this entity to not send any sync packets to the client because it doesn't do anything on the client side
	@Override
	public void onServerSync() {}

	/**
	 * When receiving a mouse location from client, the server updates that client's mouse
	 */
	public void setDesiredLocation(float x, float y) {
		this.desiredLocation.x = x;
		this.desiredLocation.y = y;
	}

	@Override
	public Vector2 getPosition() { return mousePosition; }

	private final Vector2 mousePixelPosition = new Vector2();
	@Override
	public Vector2 getPixelPosition() {
		mousePixelPosition.set(mousePosition).scl(PPM);
		mousePixelPosition.set((int) mousePixelPosition.x, (int) mousePixelPosition.y);
		return mousePixelPosition;
	}
}
