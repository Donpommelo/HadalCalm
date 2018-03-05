package com.mygdx.hadal.event.utility;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.hadal.event.Event;
import com.mygdx.hadal.event.userdata.InteractableEventData;
import com.mygdx.hadal.schmucks.bodies.Player;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.utils.Constants;
import com.mygdx.hadal.utils.b2d.BodyBuilder;

import box2dLight.RayHandler;

/**
 * A switch is an activating event that will activate a connected event when the player interacts with it.
 * @author Zachary Tu
 *
 */
public class Switch extends Event {

	private static final String name = "Switch";

	private boolean oneTime, active;
	
	public Switch(PlayState state, World world, OrthographicCamera camera, RayHandler rays, int width, int height,
			int x, int y, boolean oneTime) {
		super(state, world, camera, rays, name, width, height, x, y);
		this.oneTime = oneTime;
		this.active = true;
	}
	
	@Override
	public void create() {
		this.eventData = new InteractableEventData(world, this) {
			
			@Override
			public void onInteract(Player p) {
				if (active) {
					if (event.getConnectedEvent() != null) {
						event.getConnectedEvent().getEventData().onActivate(this);
					}
				}
				if (oneTime) {
					active = false;
				}
				
			}
		};
		
		this.body = BodyBuilder.createBox(world, startX, startY, width, height, 0, 0, 0, false, false, Constants.BIT_SENSOR, 
				(short) (Constants.BIT_PLAYER),	(short) 0, true, eventData);
	}
	
	@Override
	public String getText() {
		if (active) {
			return name + " (E TO ACTIVATE)";
		} else {
			return name + " (ACTIVATED)";
		}
		
	}

}
