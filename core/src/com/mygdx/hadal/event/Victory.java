package com.mygdx.hadal.event;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.hadal.event.userdata.EventData;
import com.mygdx.hadal.schmucks.userdata.HadalData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.utils.Constants;
import com.mygdx.hadal.utils.b2d.BodyBuilder;

import box2dLight.RayHandler;

/**
 * A Victory event (temporarily) is the way that a player "wins" a level.
 * After the player touches the victory, they will win briefly afterwards.
 * @author Zachary Tu
 *
 */
public class Victory extends Event {

	private static final String name = "VICTORY";

	private boolean touched = false;
	
	public Victory(PlayState state, World world, OrthographicCamera camera, RayHandler rays, int width, int height, int x, int y) {
		super(state, world, camera, rays, name, width, height, x, y);
	}
	
	@Override
	public void create() {

		this.eventData = new EventData(world, this) {
			
			@Override
			public void onTouch(HadalData fixB) {
				if (!touched) {
					touched = true;
					state.gameOver(true);
					event.queueDeletion();
				}
			}
		};
		
		this.body = BodyBuilder.createBox(world, startX, startY, width, height, 1, 1, 0, true, true, Constants.BIT_SENSOR, 
				(short) (Constants.BIT_PLAYER),
				(short) 0, true, eventData);
	}
}
