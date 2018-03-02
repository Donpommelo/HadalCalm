package com.mygdx.hadal.event;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.hadal.event.userdata.InteractableEventData;
import com.mygdx.hadal.schmucks.bodies.Player;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.utils.Constants;
import com.mygdx.hadal.utils.b2d.BodyBuilder;

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

		this.eventData = new InteractableEventData(world, this) {
			
			@Override
			public void onInteract(Player p) {
				state.getStage().addDialogue(id);
			}
		};
		
		this.body = BodyBuilder.createBox(world, startX, startY, width, height, 1, 1, 0, true, true, Constants.BIT_SENSOR, 
				(short) (Constants.BIT_PLAYER),	(short) 0, true, eventData);
	}
	
	@Override
	public String getText() {
		if (eventData.getSchmucks().isEmpty()) {
			return "RADIO";
		} else {
			return "RADIO (E TO LISTEN)";
		}
	}

}
