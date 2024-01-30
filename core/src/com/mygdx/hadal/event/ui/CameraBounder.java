package com.mygdx.hadal.event.ui;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.mygdx.hadal.constants.BodyConstants;
import com.mygdx.hadal.event.Event;
import com.mygdx.hadal.event.userdata.EventData;
import com.mygdx.hadal.schmucks.entities.Player;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.utils.b2d.HadalBody;

/**
 * A CameraBounder adds movement restrictions to the camera
 * <p>
 * Triggered Behavior: When triggered, this event makes changes the properties of the camera.
 * Triggering Behavior: N/A. However, it uses its connected event as a point to make the camera focus on instead. 
 * <p>
 * Fields:
 * <p>
 * right/left/up/down: Will this set a right/left/up/down bound on the camera (at its own location)
 * spectator: Will this set the camera bounds for normal players or spectators?
 * @author Frabalante Flircester
 */
public class CameraBounder extends Event {

	//which bound are we setting?
	private final boolean right, left, up, down;

	public CameraBounder(PlayState state, Vector2 startPos, Vector2 size, boolean right, boolean left, boolean up, boolean down, boolean spectator) {
		super(state, startPos, size);
		this.right = right;
		this.left = left;
		this.up = up;
		this.down = down;

		//is this regular camera bound or spectator bound?
		if (spectator) {
			state.getCameraManager().setSpectatorBounded(true);
			if (right) {
				state.getCameraManager().getSpectatorBounds()[0] = startPos.x;
			}
			if (left) {
				state.getCameraManager().getSpectatorBounds()[1] = startPos.x;
			}
			if (up) {
				state.getCameraManager().getSpectatorBounds()[2] = startPos.y;
			}
			if (down) {
				state.getCameraManager().getSpectatorBounds()[3] = startPos.y;
			}
		}
	}
	
	@Override
	public void create() {
		this.eventData = new EventData(this) {
			
			@Override
			public void onActivate(EventData activator, Player p) {
				if (right) {
					state.getCameraManager().getCameraBounds()[0] = getPixelPosition().x;
				}
				if (left) {
					state.getCameraManager().getCameraBounds()[1] = getPixelPosition().x;
				}
				if (up) {
					state.getCameraManager().getCameraBounds()[2] = getPixelPosition().y;
				}
				if (down) {
					state.getCameraManager().getCameraBounds()[3] = getPixelPosition().y;
				}
			}
		};

		this.body = new HadalBody(eventData, startPos, size, BodyConstants.BIT_SENSOR, (short) 0, (short) 0)
				.setBodyType(BodyDef.BodyType.KinematicBody)
				.addToWorld(world);

		//this line lets player-less spectators have camera bounds when the map does not have spectator-specific bounds
		if (!state.getCameraManager().isSpectatorBounded()) {
			eventData.onActivate(null, null);
		}
	}
	
	@Override
	public void loadDefaultProperties() {
		setSyncType(eventSyncTypes.USER);
	}
}
