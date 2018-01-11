package com.mygdx.hadal.event;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.hadal.event.userdata.EventData;
import com.mygdx.hadal.schmucks.UserDataTypes;
import com.mygdx.hadal.schmucks.userdata.HadalData;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.utils.Constants;
import com.mygdx.hadal.utils.b2d.BodyBuilder;

import box2dLight.RayHandler;

public class AirBubble extends Event{

	private static final int width = 16;
	private static final int height = 16;
	
	private static final int fuelRegained = 25;

	private AirBubbleSpawner spawner;
	
	private static final String name = "Air Bubble";
	
	public AirBubble(PlayState state, World world, OrthographicCamera camera, RayHandler rays, int x, int y,
			AirBubbleSpawner spawner) {
		super(state, world, camera, rays, name, width, height, x, y);
		this.spawner = spawner;
	}
	
	public void create() {

		this.eventData = new EventData(world, this) {
			public void onTouch(HadalData fixB) {
				if (fixB != null) {
					if (fixB.getType().equals(UserDataTypes.BODY)) {
						if (((PlayerBodyData)fixB).currentFuel < ((PlayerBodyData)fixB).getMaxFuel()) {
							((PlayerBodyData)fixB).fuelGain(fuelRegained);
							if (spawner != null) {
								spawner.readyToSpawn = true;
							}
							queueDeletion();
						}
					}
				}
			}
		};
		
		this.body = BodyBuilder.createBox(world, startX, startY, width, height, 1, 1, 0, true, true, Constants.BIT_SENSOR, 
				(short) (Constants.BIT_PLAYER),
				Constants.ENEMY_HITBOX, true, eventData);
	}
	
}
