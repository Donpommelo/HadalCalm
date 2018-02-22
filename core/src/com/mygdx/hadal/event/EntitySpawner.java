package com.mygdx.hadal.event;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.hadal.event.userdata.EventData;
import com.mygdx.hadal.schmucks.bodies.enemies.*;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.utils.Constants;
import com.mygdx.hadal.utils.b2d.BodyBuilder;

import box2dLight.RayHandler;
import static com.mygdx.hadal.utils.Constants.PPM;

/**
 * The Entity spawner periodically spawns entities.
 * @author Zachary Tu
 *
 */
public class EntitySpawner extends Event {
	
	//id of the entity to spawned
	private int id;
	
	//How frequently will the spawns occur? Every interval seconds.
	private float interval;
	
	//The event will spawn limit entites before stopping. If this is 0, the event will never stop.
	private int limit;
	
	//These keep track of how long since last spawn and total spawn number respectively
	private float spawnCount = 0;
	private int amountCount = 0;
	
	//Where entites will be spawned
	private int spawnX, spawnY;
	
	private static final String name = "Schmuck Spawner";

	public EntitySpawner(PlayState state, World world, OrthographicCamera camera, RayHandler rays, int width, int height,
			int x, int y, int schmuckId, float interval, int limit) {
		super(state, world, camera, rays, name, width, height, x, y);
		this.id = schmuckId;
		this.interval = interval;
		this.limit = limit;
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
		spawnCount += delta;
		if (spawnCount >= interval && (limit == 0 || amountCount < limit)) {
			spawnCount = 0;
			amountCount++;
			switch(id) {
			case 0:
				
				//This is kinda a dumb hacky way of having player spawns. Player spawns are technically dictated by the 
				//playstate, but this lets us spawn on this event provided interval = 0 and limit = 1.
				state.getPlayer().getBody().setTransform(spawnX / PPM, spawnY / PPM , 0);
				break;
			case 1:
				if (Math.random() > 0.4f) {
					new Scissorfish(state, world, camera, rays, spawnX, spawnY);
				} else if (Math.random() > 0.7f){
					new Spittlefish(state, world, camera, rays, spawnX, spawnY);
				} else {
					new Torpedofish(state, world, camera, rays, spawnX, spawnY);
				}
				break;
			case 2:
				new TrailingEnemy(state, world, camera, rays, 32, 16, spawnX, spawnY);
				break;
				
			}
		}
	}
}
