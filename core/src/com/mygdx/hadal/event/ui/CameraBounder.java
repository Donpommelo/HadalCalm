package com.mygdx.hadal.event.ui;

import com.badlogic.gdx.math.Vector2;
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
 * 
 * @author Zachary Tu
 *
 */
public class CameraBounder extends Event {

	private boolean right, left, up, down;
	
	public CameraBounder(PlayState state, Vector2 startPos, Vector2 size, boolean right, boolean left, boolean up, boolean down) {
		super(state, startPos, size);
		this.right = right;
		this.left = left;
		this.up = up;
		this.down = down;
	}
	
	@Override
	public void create() {
		this.eventData = new EventData(this) {
			
			@Override
			public void onActivate(EventData activator, Player p) {
				if (right) {
					state.getCameraBounds()[0] = getPixelPosition().x;
					state.getCameraBounded()[0] = true;
				}
				
				if (left) {
					state.getCameraBounds()[1] = getPixelPosition().x;
					state.getCameraBounded()[1] = true;
				}
				
				if (up) {
					state.getCameraBounds()[2] = getPixelPosition().y;
					state.getCameraBounded()[2] = true;
				}
				
				if (down) {
					state.getCameraBounds()[3] = getPixelPosition().y;
					state.getCameraBounded()[3] = true;
				}
			}
		};
		
		this.body = BodyBuilder.createBox(world, startPos, size, 1, 1, 0, true, true, Constants.BIT_SENSOR, (short) (0), (short) 0, true, eventData);
	}
	
	@Override
	public void loadDefaultProperties() {
		setSyncType(eventSyncTypes.ALL);
	}
}
