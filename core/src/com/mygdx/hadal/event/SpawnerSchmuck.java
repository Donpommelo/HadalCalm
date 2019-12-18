package com.mygdx.hadal.event;

import com.mygdx.hadal.event.userdata.EventData;
import com.mygdx.hadal.event.utility.TriggerAlt;
import com.mygdx.hadal.schmucks.bodies.Player;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.schmucks.bodies.enemies.*;
import com.mygdx.hadal.schmucks.bodies.enemies.Enemy.enemyType;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.utils.Constants;
import com.mygdx.hadal.utils.b2d.BodyBuilder;

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

	//this is the amount of enemies left
	private int amountLeft = 0;
	
	//Should the spawned mob be spawned with spread?
	private boolean spread;
	
	//Extra field for enemies that require more information (like turret subtypes)
	private int extraField;
	
	//is this enemy a boss enemy and if so, what is its name?
	private boolean boss;
	private String bossName;
	
	public SpawnerSchmuck(PlayState state, int width, int height, int x, int y, int schmuckId, int limit, Boolean spread, int extraField, boolean boss, String bossName) {
		super(state, name, width, height, x, y);
		this.id = schmuckId;
		this.limit = limit;
		this.spawnX = x;
		this.spawnY = y;
		this.spread = spread;
		this.extraField = extraField;
		this.boss = boss;
		this.bossName = bossName;
	}
	
	@Override
	public void create() {
		this.eventData = new EventData(this) {
			
			@Override
			public void onActivate(EventData activator, Player p) {
				
				if (activator.getEvent() instanceof TriggerAlt) {
					limit += Integer.parseInt(((TriggerAlt)activator.getEvent()).getMessage());
				} else {
					
					for (int i = 0; i < limit; i++) {
						
						Enemy enemy = null;
						
						int randX = spawnX + (spread ? (int)( (Math.random() - 0.5) * 100) : 0);
						int randY = spawnY + (spread ? (int)( (Math.random() - 0.5) * 100) : 0);
						switch(id) {
						case 1:
							if (Math.random() > 0.4f) {
								enemy = new Scissorfish(state, randX, randY, Constants.ENEMY_HITBOX, (SpawnerSchmuck) event);
							} else if (Math.random() > 0.7f){
								enemy = new Spittlefish(state, randX, randY, Constants.ENEMY_HITBOX, (SpawnerSchmuck) event);
							} else {
								enemy = new Torpedofish(state, randX, randY, Constants.ENEMY_HITBOX, (SpawnerSchmuck) event);
							}
							break;
						case 2:
							enemy = new Scissorfish(state, randX, randY, Constants.ENEMY_HITBOX, (SpawnerSchmuck) event);
							break;
						case 3:
							enemy = new Spittlefish(state, randX, randY, Constants.ENEMY_HITBOX, (SpawnerSchmuck) event);
							break;
						case 4:
							enemy = new Torpedofish(state, randX, randY, Constants.ENEMY_HITBOX, (SpawnerSchmuck) event);
							break;
						case 5:
							enemy = new Turret(state, randX, (int) (randY - height / 2), enemyType.TURRET_FLAK, extraField, Constants.ENEMY_HITBOX, (SpawnerSchmuck) event);
							break;
						case 6:
							enemy = new Turret(state, randX, (int) (randY - height / 2), enemyType.TURRET_VOLLEY, extraField, Constants.ENEMY_HITBOX, (SpawnerSchmuck) event);
							break;
						case 7:
							enemy = new Boss1(state, randX, (int) (randY - height / 2), enemyType.BOSS, Constants.ENEMY_HITBOX, (SpawnerSchmuck) event);
							break;
						}
						amountLeft++;
						if (boss) {
							enemy.setBoss(true);
							enemy.setName(bossName);
							state.setBoss(enemy);
						}
					}
				}
			}
		};
		
		this.body = BodyBuilder.createBox(world, startX, startY, width, height, 1, 1, 0, true, true, Constants.BIT_SENSOR, 
				(short) (0), (short) 0, true, eventData);
	}
	
	public void onDeath(Schmuck schmuck) {
		amountLeft--;
		if (amountLeft <= 0) {
			if (getConnectedEvent() != null) {
				getConnectedEvent().eventData.preActivate(eventData, null);
			}
			if (boss) {
				state.clearBoss();
			}
		}
	}
}
