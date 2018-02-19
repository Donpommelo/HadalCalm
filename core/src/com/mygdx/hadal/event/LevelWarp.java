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
 * A Use Portal is a portal that transports the player elsewhere when they interact with it.
 * The event they are transported to does not have to be a portal.
 * @author Zachary Tu
 *
 */
public class LevelWarp extends Event {

	private static final String name = "Level Warp";

	String level;

	public LevelWarp(PlayState state, World world, OrthographicCamera camera, RayHandler rays, int width, int height,
			int x, int y, String level) {
		super(state, world, camera, rays, name, width, height, x, y);
		this.level = level;
	}
	
	@Override
	public void create() {
		this.eventData = new InteractableEventData(world, this) {
			
			@Override
			public void onInteract(Player p) {
				state.loadLevel("Maps/"+level);
			}
		};
		
		this.body = BodyBuilder.createBox(world, startX, startY, width, height, 1, 1, 0, true, true, Constants.BIT_SENSOR, 
				(short) (Constants.BIT_PLAYER),
				(short) 0, true, eventData);
	}
	
	@Override
	public String getText() {
		return name + " (E TO USE)";
	}

}
