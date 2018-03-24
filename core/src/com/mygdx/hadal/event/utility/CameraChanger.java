package com.mygdx.hadal.event.utility;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.hadal.event.Event;
import com.mygdx.hadal.event.userdata.EventData;
import com.mygdx.hadal.states.PlayState;

import box2dLight.RayHandler;

/**
 * A CameraChanger changes what the camera is zoomed in on.
 * 
 * Triggered Behavior: When triggered, this event makes changes the properties of the camera.
 * Triggering Behavior: N/A. However, it uses its connected event as a point to make the camera focus on instead. 
 * 	If there is no connected event, it will zoom in on the player.
 * 
 * Fields:
 * 
 * zoom: Sets the zoom of the camera. Optional. Default: 1.0f
 * 
 * @author Zachary Tu
 *
 */
public class CameraChanger extends Event {

	private static final String name = "Camera Changer";
	
	private float zoom;
	
	public CameraChanger(PlayState state, World world, OrthographicCamera camera, RayHandler rays, int width, int height,
			int x, int y, float zoom) {
		super(state, world, camera, rays, name, width, height, x, y);
		this.zoom = zoom;
	}
	
	@Override
	public void create() {
		this.eventData = new EventData(world, this) {
			
			@Override
			public void onActivate(EventData activator) {
				if (event.getConnectedEvent() != null) {
					state.setCameraTarget(event.getConnectedEvent());
				} else {
					state.setCameraTarget(state.getPlayer());
				}
				state.setZoom(zoom);
			}
		};
	}
}
