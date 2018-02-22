package com.mygdx.hadal.event;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.hadal.event.userdata.EventData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.utils.Constants;
import com.mygdx.hadal.utils.b2d.BodyBuilder;

import box2dLight.RayHandler;

public class AirBubbleSpawner extends Event {
	
	private float interval;
	
	private float spawnCount = 0;
	
	private int spawnX, spawnY;
	
	private boolean readyToSpawn = true;
	
	private static final String name = "Fuel Spawner";

	public AirBubbleSpawner(PlayState state, World world, OrthographicCamera camera, RayHandler rays, int width, int height,
			int x, int y, float interval) {
		super(state, world, camera, rays, name, width, height, x, y);
		this.interval = interval;
		this.spawnX = x;
		this.spawnY = y;
	}
	
	@Override
	public void create() {

		this.eventData = new EventData(world, this);
		
		this.body = BodyBuilder.createBox(world, startX, startY, width, height, 1, 1, 0, true, true, Constants.BIT_SENSOR, 
				(short) (Constants.BIT_PLAYER | Constants.BIT_ENEMY | Constants.BIT_PROJECTILE),
				(short) 0, true, eventData);
	}
	
	@Override
	public void controller(float delta) {
		if (readyToSpawn) {
			spawnCount += delta;
		}
		if (spawnCount >= interval) {
			spawnCount = 0;
			
			if (readyToSpawn) {
				readyToSpawn = false;
				new AirBubble(state, world, camera, rays, spawnX, spawnY, this);
			}
		}
	}

	public void setReadyToSpawn(boolean readyToSpawn) {
		this.readyToSpawn = readyToSpawn;
	}
	
	
}
