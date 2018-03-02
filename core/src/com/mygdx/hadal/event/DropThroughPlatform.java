package com.mygdx.hadal.event;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.hadal.event.userdata.EventData;
import com.mygdx.hadal.schmucks.bodies.Player;
import com.mygdx.hadal.schmucks.userdata.FeetData;
import com.mygdx.hadal.schmucks.userdata.HadalData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.utils.Constants;
import com.mygdx.hadal.utils.b2d.BodyBuilder;

import box2dLight.RayHandler;

/**
 * This event is a solid block that can be passed by hitboxes, but not the player.
 * When the player presses crouch when standing on it, they will pass through it.
 * @author Zachary Tu
 *
 */
public class DropThroughPlatform extends Event {
	
	private static final String name = "Drop Through Platform";

	public DropThroughPlatform(PlayState state, World world, OrthographicCamera camera, RayHandler rays, int width, int height, int x, int y) {
		super(state, world, camera, rays, name, width, height, x, y);
	}
	
	@Override
	public void create() {

		this.eventData = new EventData(world, this) {
			
			@Override
			public void onTouch(HadalData fixB) {
				if (fixB != null) {
					if (fixB instanceof FeetData) {
						Filter filter = event.getBody().getFixtureList().get(0).getFilterData();
						filter.maskBits = (short) (Constants.BIT_SENSOR | Constants.BIT_PLAYER);
						event.getBody().getFixtureList().get(0).setFilterData(filter);
						
						((FeetData) fixB).setTerrain(this.event);
					}
				}
			}
			
			@Override
			public void onRelease(HadalData fixB) {
				if (fixB != null) {
					if (fixB instanceof FeetData) {
						Filter filter = event.getBody().getFixtureList().get(0).getFilterData();
						filter.maskBits = (short) (Constants.BIT_SENSOR);
						event.getBody().getFixtureList().get(0).setFilterData(filter);
						((FeetData) fixB).setTerrain(null);

					}
				}
			}
			
			@Override
			public void onInteract(Player p) {
				Filter filter = event.getBody().getFixtureList().get(0).getFilterData();
				filter.maskBits = (short) (Constants.BIT_SENSOR);
				event.getBody().getFixtureList().get(0).setFilterData(filter);
			}
		};
		
		this.body = BodyBuilder.createBox(world, startX, startY, width, height, 1, 1, 0, true, true, Constants.BIT_WALL, 
				(short) (Constants.BIT_SENSOR),
				(short) 0, false, eventData);
	}	
}
