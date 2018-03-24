package com.mygdx.hadal.event;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.hadal.event.userdata.EventData;
import com.mygdx.hadal.states.PlayState;

import box2dLight.RayHandler;

/**
 * An End event makes the game end with either a victory or loss.
 * 
 * Triggered Behavior: When triggered, this will initiate the end of the game.
 * Triggering Behavior: N/A
 * 
 * 
 * Fields:
 * won: boolean that determines if the player wins or not. Optional. Default: true
 * 
 * @author Zachary Tu
 *
 */
public class End extends Event {

	private static final String name = "VICTORY";

	private boolean won;
	
	public End(PlayState state, World world, OrthographicCamera camera, RayHandler rays, int width, int height, int x, int y, boolean won) {
		super(state, world, camera, rays, name, width, height, x, y);
		this.won = won;
	}
	
	@Override
	public void create() {

		this.eventData = new EventData(world, this) {
			
			@Override
			public void onActivate(EventData activator) {
				state.gameOver(won);
			}
			
		};
	}
}
