package com.mygdx.hadal.event.ui;

import com.mygdx.hadal.event.Event;
import com.mygdx.hadal.event.userdata.EventData;
import com.mygdx.hadal.schmucks.entities.Player;
import com.mygdx.hadal.states.PlayState;

/**
 * A CameraChanger changes what the camera is zoomed in on.
 * <p>
 * Triggered Behavior: When triggered, this event makes changes the properties of the camera.
 * Triggering Behavior: N/A. However, it uses its connected event as a point to make the camera focus on instead. 
 * 	If there is no connected event, it will zoom in on the player.
 * <p>
 * Fields:
 * zoom: Sets the zoom of the camera. Optional. Default: 1.0f If zoom is 0, we only set camera focus
 * offsetX,offsetY: offset the camera by this much. Only used for specific events
 * 
 * @author Cruphelia Culdwin
 */
public class CameraChanger extends Event {

	private final float zoom, offsetX, offsetY;
	
	public CameraChanger(PlayState state, float zoom, float offsetX, float offsetY) {
		super(state);
		this.zoom = zoom;
		this.offsetX = offsetX;
		this.offsetY = offsetY;
	}
	
	@Override
	public void create() {
		this.eventData = new EventData(this) {
			
			@Override
			public void onActivate(EventData activator, Player p) {
				if (event.getConnectedEvent() != null) {
					state.getCameraManager().setCameraTarget(event.getConnectedEvent().getPixelPosition());
				} else {
					state.getCameraManager().setCameraTarget(null);
				}
				state.getCameraManager().setCameraOffset(offsetX, offsetY);

				if (zoom != 0.0f) {
					state.getCameraManager().setZoom(zoom);
				}
			}
		};
	}
	
	@Override
	public void loadDefaultProperties() {
		setServerSyncType(eventSyncTypes.SELF);
		setClientSyncType(eventSyncTypes.SELF);
	}
}
