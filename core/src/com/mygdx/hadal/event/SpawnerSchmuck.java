package com.mygdx.hadal.event;

import com.badlogic.gdx.math.Vector2;
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
 * boss: boolean of whether this spawns a boss enemy
 * bossName: nanme of the boss spawned (if a boss is spawned)
 * @author Zachary Tu
 *
 */
public class SpawnerSchmuck extends Event {
	
	private int id;
	private int limit;
	
	private static final String name = "Schmuck Spawner";

	//this is the amount of enemies left
	private int amountLeft = 0;
	
	//Extra field for enemies that require more information (like turret subtypes)
	private int extraField;
	
	//is this enemy a boss enemy and if so, what is its name?
	private boolean boss;
	private String bossName;
	
	public SpawnerSchmuck(PlayState state, Vector2 startPos, Vector2 size, int schmuckId, int limit, int extraField, boolean boss, String bossName) {
		super(state, name, startPos, size);
		this.id = schmuckId;
		this.limit = limit;
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
						
						switch(id) {
						case 1:
							if (Math.random() > 0.4f) {
								enemy = new Scissorfish(state, startPos, Constants.ENEMY_HITBOX, (SpawnerSchmuck) event);
							} else if (Math.random() > 0.7f){
								enemy = new Spittlefish(state, startPos, Constants.ENEMY_HITBOX, (SpawnerSchmuck) event);
							} else {
								enemy = new Torpedofish(state, startPos, Constants.ENEMY_HITBOX, (SpawnerSchmuck) event);
							}
							break;
						case 2:
							enemy = new Scissorfish(state, startPos, Constants.ENEMY_HITBOX, (SpawnerSchmuck) event);
							break;
						case 3:
							enemy = new Spittlefish(state, startPos, Constants.ENEMY_HITBOX, (SpawnerSchmuck) event);
							break;
						case 4:
							enemy = new Torpedofish(state, startPos, Constants.ENEMY_HITBOX, (SpawnerSchmuck) event);
							break;
						case 5:
							enemy = new Turret(state, startPos, enemyType.TURRET_FLAK, extraField, Constants.ENEMY_HITBOX, (SpawnerSchmuck) event);
							break;
						case 6:
							enemy = new Turret(state, startPos, enemyType.TURRET_VOLLEY, extraField, Constants.ENEMY_HITBOX, (SpawnerSchmuck) event);
							break;
						case 7:
							enemy = new Boss1(state, startPos, enemyType.BOSS, Constants.ENEMY_HITBOX, (SpawnerSchmuck) event);
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
		
		this.body = BodyBuilder.createBox(world, startPos, size, 1, 1, 0, true, true, Constants.BIT_SENSOR, 
				(short) (0), (short) 0, true, eventData);
	}
	
	/**
	 * This event is run when an enemy spawned by this event dies.
	 * @param schmuck: the enemy that died
	 */
	public void onDeath(Schmuck schmuck) {
		amountLeft--;
		
		//if all enemies spawned by this have been defeated, activate connected event
		if (amountLeft <= 0) {
			if (getConnectedEvent() != null) {
				getConnectedEvent().eventData.preActivate(eventData, null);
			}
			
			//if a boss was defeated, tell the ui to clear the boss hp bar from the ui
			if (boss) {
				state.clearBoss();
			}
		}
	}
}
