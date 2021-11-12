package com.mygdx.hadal.event.ui;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.mygdx.hadal.event.Event;
import com.mygdx.hadal.event.userdata.EventData;
import com.mygdx.hadal.schmucks.bodies.Player;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.utils.Constants;
import com.mygdx.hadal.utils.b2d.BodyBuilder;

/**
 * A CameraBounder adds movement restrictions to the camera
 * 
 * Triggered Behavior: When triggered, this event makes changes the properties of the camera.
 * Triggering Behavior: N/A. However, it uses its connected event as a point to make the camera focus on instead. 
 * 
 * Fields:
 * 
 * right/left/up/down: Will this set a right/left/up/down bound on the camera (at its own location)
 * spectator: Will this set the camera bounds for normal players or spectators?
 * @author Frabalante Flircester
 */
public class CameraBounder extends Event {

	//which bound are we setting?
	private final boolean right, left, up, down;
	
	//is this regular camera bound or spectator bound?
	private final boolean spectator;
	
	public CameraBounder(PlayState state, Vector2 startPos, Vector2 size, boolean right, boolean left, boolean up, boolean down, boolean spectator) {
		super(state, startPos, size);
		this.right = right;
		this.left = left;
		this.up = up;
		this.down = down;
		this.spectator = spectator;
	}
	
	@Override
	public void create() {
		this.eventData = new EventData(this) {
			
			@Override
			public void onActivate(EventData activator, Player p) {
				if (right) {
					state.getCameraBounds()[0] = getPixelPosition().x;
				}
				if (left) {
					state.getCameraBounds()[1] = getPixelPosition().x;
				}
				if (up) {
					state.getCameraBounds()[2] = getPixelPosition().y;
				}
				if (down) {
					state.getCameraBounds()[3] = getPixelPosition().y;
				}
			}
		};
		
		this.body = BodyBuilder.createBox(world, startPos, size, 1, 1, 0, true, true,
				Constants.BIT_SENSOR, (short) 0, (short) 0, true, eventData);
		this.body.setType(BodyType.KinematicBody);
		
		if (spectator) {
			state.setSpectatorBounded(true);
			if (right) {
				state.getSpectatorBounds()[0] = getPixelPosition().x;
			}
			if (left) {
				state.getSpectatorBounds()[1] = getPixelPosition().x;
			}
			if (up) {
				state.getSpectatorBounds()[2] = getPixelPosition().y;
			}
			if (down) {
				state.getSpectatorBounds()[3] = getPixelPosition().y;
			}
		}

		//this line lets player-less spectators have camera bounds
		eventData.onActivate(null, null);
	}
	
	@Override
	public void loadDefaultProperties() {
		setSyncType(eventSyncTypes.USER);
	}
}
