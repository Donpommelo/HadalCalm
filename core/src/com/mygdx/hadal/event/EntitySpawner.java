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

public class EntitySpawner extends Event {
	
	public int id;
	public float interval;
	public int limit;
	
	public float spawnCount = 0;
	public int amountCount = 0;
	
	public int spawnX, spawnY;
	
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
				state.player.getBody().setTransform(
						spawnX / PPM + state.getPlayer().width / PPM / 2, 
						spawnY / PPM + state.getPlayer().height / PPM / 2, 0);
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
