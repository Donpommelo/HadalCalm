package com.mygdx.hadal.event;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.hadal.event.userdata.EventData;
import com.mygdx.hadal.states.PlayState;

import box2dLight.RayHandler;

/**
 * A radio is a simple event that when interacted with will put up a test dialogue actor into the play stage
 * @author Zachary Tu
 *
 */
public class Radio extends Event {

	private static final String name = "Radio";

	private String id;
	
	public Radio(PlayState state, World world, OrthographicCamera camera, RayHandler rays, int width, int height, int x, int y, String id) {
		super(state, world, camera, rays, name, width, height, x, y);
		this.id = id;
	}
	
	@Override
	public void create() {

		this.eventData = new EventData(world, this) {
			
			@Override
			public void onActivate(EventData activator) {
				if (event.getConnectedEvent() != null) {
					state.getStage().addDialogue(id, this, event.getConnectedEvent().getEventData());
				} else {
					state.getStage().addDialogue(id, this, null);
				}
				
			}
		};
	}
}
