package com.mygdx.hadal.event;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.hadal.event.userdata.EventData;
import com.mygdx.hadal.event.userdata.InteractableEventData;
import com.mygdx.hadal.schmucks.bodies.Player;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.utils.Constants;
import com.mygdx.hadal.utils.b2d.BodyBuilder;

import box2dLight.RayHandler;

public class Switch extends Event {

	private static final String name = "Switch";

	private EventData connectedEvent;
	
	public Switch(PlayState state, World world, OrthographicCamera camera, RayHandler rays, int width, int height,
			int x, int y, EventData event) {
		super(state, world, camera, rays, name, width, height, x, y);
		this.connectedEvent = event;
	}
	
	public void create() {
		this.eventData = new InteractableEventData(world, this) {
			public void onInteract(Player p) {
				connectedEvent.onActivate(p, this);
			}
		};
		
		this.body = BodyBuilder.createBox(world, startX, startY, width, height, 1, 1, 0, true, true, Constants.BIT_SENSOR, 
				(short) (Constants.BIT_PLAYER),
				(short) 0, true, eventData);
	}
	
	public String getText() {
		return name + " (E TO ACTIVATE)";
	}

}
