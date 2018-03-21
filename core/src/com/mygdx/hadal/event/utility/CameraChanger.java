package com.mygdx.hadal.event.utility;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.hadal.event.Event;
import com.mygdx.hadal.event.userdata.EventData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.utils.Constants;
import com.mygdx.hadal.utils.b2d.BodyBuilder;

import box2dLight.RayHandler;

/**
 * A CameraChanger changes what the camera is zoomed in on.
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
		
		this.body = BodyBuilder.createBox(world, startX, startY, width, height, 1, 1, 0, true, true, Constants.BIT_SENSOR, 
				(short) 0, (short) 0, true, eventData);
	}
}
