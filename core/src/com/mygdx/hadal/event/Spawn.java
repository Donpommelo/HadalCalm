package com.mygdx.hadal.event;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.hadal.event.userdata.EventData;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.schmucks.bodies.enemies.*;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.utils.Constants;
import com.mygdx.hadal.utils.b2d.BodyBuilder;

import box2dLight.RayHandler;

/**
 * A Trigger spawn is an enemy spawner that activates when triggered by another event.
 * Also, when all enemies are defeated, this event can trigger another event.
 * @author Zachary Tu
 *
 */
public class Spawn extends Event {
	
	private int id;
	private int limit;
	
	private int spawnX, spawnY;
	
	private static final String name = "Schmuck Spawner";

	private ArrayList<Schmuck> spawns;
	
	private float controllerCount = 0;

	boolean defeated = false;
	
	public Spawn(PlayState state, World world, OrthographicCamera camera, RayHandler rays, int width, int height,
			int x, int y, int schmuckId, int limit) {
		super(state, world, camera, rays, name, width, height, x, y);
		this.id = schmuckId;
		this.limit = limit;
		this.spawnX = x;
		this.spawnY = y;
		this.spawns = new ArrayList<Schmuck>();
	}
	
	@Override
	public void create() {
		this.eventData = new EventData(world, this) {
			
			@Override
			public void onActivate(EventData activator) {
				for (int i = 0; i < limit; i++) {

					int randX = spawnX + (int)( (Math.random() - 0.5) * 100);
					int randY = spawnY + (int)( (Math.random() - 0.5) * 100);
					switch(id) {
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
						spawns.add(new Scissorfish(state, world, camera, rays, randX, randY));
						break;
					case 3:
						spawns.add(new Spittlefish(state, world, camera, rays, randX, randY));
						break;
					case 4:
						spawns.add(new Torpedofish(state, world, camera, rays, randX, randY));
						break;
						
					}
				}
			}
		};
		
		this.body = BodyBuilder.createBox(world, startX, startY, width, height, 1, 1, 0, true, true, Constants.BIT_SENSOR, 
				(short) (Constants.BIT_PLAYER | Constants.BIT_ENEMY | Constants.BIT_PROJECTILE),
				(short) 0, true, eventData);
	}
	
	@Override
	public void controller(float delta) {
		
		if (!defeated && getConnectedEvent() != null) {
			controllerCount+=delta;
			if (controllerCount >= 1f) {
				controllerCount = 0;
				
				if (!spawns.isEmpty()) {
					
					defeated = true;
					
					for (Schmuck s : spawns) {
						
						if (s.getBodyData().getCurrentHp() > 0) {
							defeated = false;
						}
					}
					
					if (defeated) {
						getConnectedEvent().eventData.onActivate(eventData);
					}
				}
			}
		}
		
	}
}
