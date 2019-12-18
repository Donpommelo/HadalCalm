package com.mygdx.hadal.event.utility;

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
 * right/left/up/down: Will this set a right/left/up/down bound on the camera (at its own location)?
 * 
 * @author Zachary Tu
 *
 */
public class CameraBounder extends Event {

	private static final String name = "Camera Changer";
	
	private boolean right, left, up, down;
	
	public CameraBounder(PlayState state, int width, int height, int x, int y, boolean right, boolean left, boolean up, boolean down) {
		super(state, name, width, height, x, y);
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
					state.getCameraBounds()[0] = getPosition().x;
					state.getCameraBounded()[0] = true;
				}
				
				if (left) {
					state.getCameraBounds()[1] = getPosition().x;
					state.getCameraBounded()[1] = true;
				}
				
				if (up) {
					state.getCameraBounds()[2] = getPosition().y;
					state.getCameraBounded()[2] = true;
				}
				
				if (down) {
					state.getCameraBounds()[3] = getPosition().y;
					state.getCameraBounded()[3] = true;
				}
			}
		};
		
		this.body = BodyBuilder.createBox(world, startX, startY, width, height, 1, 1, 0, true, true, Constants.BIT_SENSOR, 
				(short) (0), (short) 0, true, eventData);
	}
	
	@Override
	public void loadDefaultProperties() {
		setSyncType(eventSyncTypes.ALL);
	}
}
