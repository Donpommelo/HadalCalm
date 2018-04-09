package com.mygdx.hadal.event;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.hadal.event.userdata.EventData;
import com.mygdx.hadal.event.utility.TriggerAlt;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.schmucks.bodies.enemies.*;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.utils.Constants;
import com.mygdx.hadal.utils.b2d.BodyBuilder;

import box2dLight.RayHandler;

/**
 * A Trigger spawn is an enemy spawner that activates when triggered by another event.
 * Also, when all enemies are defeated, this event can trigger another event.
 * 
 * Triggered Behavior: When triggered, this will spawn a group of schmucks.
 * Triggering Behavior: When all spawned enemies are defeated, this will activate its connected event.
 * Alt-Triggered Behavior: When alt-triggered, this spawner changes the number of schmucks it will spawn at once.
 * 
 * 
 * Fields:
 * id: The id of the type of enemy to spawn
 * limit: The number of enemies to spawn simultaneously
 * spread: boolean of whether to spawn a group with some slight randomized location. Optional. Default: true
 * 
 * @author Zachary Tu
 *
 */
public class SpawnerSchmuck extends Event {
	
	private int id;
	private int limit;
	
	private int spawnX, spawnY;
	
	private static final String name = "Schmuck Spawner";

	private ArrayList<Schmuck> spawns;
	
	private float controllerCount = 0;

	private boolean defeated = false;
	private boolean spread;
	
	public SpawnerSchmuck(PlayState state, World world, OrthographicCamera camera, RayHandler rays, int width, int height,
			int x, int y, int schmuckId, int limit, Boolean spread) {
		super(state, world, camera, rays, name, width, height, x, y);
		this.id = schmuckId;
		this.limit = limit;
		this.spawnX = x;
		this.spawnY = y;
		this.spread = spread;
		this.spawns = new ArrayList<Schmuck>();
	}
	
	@Override
	public void create() {
		this.eventData = new EventData(world, this) {
			
			@Override
			public void onActivate(EventData activator) {
				
				if (activator.getEvent() instanceof TriggerAlt) {
					limit += Integer.parseInt(((TriggerAlt)activator.getEvent()).getMessage());
				} else {
					defeated = false;
					
					for (int i = 0; i < limit; i++) {
						
						int randX = spawnX + (spread ? (int)( (Math.random() - 0.5) * 100) : 0);
						int randY = spawnY + (spread ? (int)( (Math.random() - 0.5) * 100) : 0);
						switch(id) {
						case 1:
							if (Math.random() > 0.4f) {
								spawns.add(new Scissorfish(state, world, camera, rays, randX, randY));
							} else if (Math.random() > 0.7f){
								spawns.add(new Spittlefish(state, world, camera, rays, randX, randY));
							} else {
								spawns.add(new Torpedofish(state, world, camera, rays, randX, randY));
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
						case 5:
							spawns.add(new Turret(state, world, camera, rays, randX, (int) (randY - height / 2), "flak"));
							break;
						case 6:
							spawns.add(new Turret(state, world, camera, rays, randX, (int) (randY - height / 2), "volley"));
							break;
							
						}
					}
				}
			}
		};
		
		this.body = BodyBuilder.createBox(world, startX, startY, width, height, 1, 1, 0, true, true, Constants.BIT_SENSOR, 
				(short) (0), (short) 0, true, eventData);
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
						
						if (s.getBodyData() != null) {
							if (s.getBodyData().getCurrentHp() > 0) {
								defeated = false;
							}
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
