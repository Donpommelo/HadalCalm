package com.mygdx.hadal.event;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.mygdx.hadal.constants.BodyConstants;
import com.mygdx.hadal.event.userdata.EventData;
import com.mygdx.hadal.event.utility.TriggerAlt;
import com.mygdx.hadal.schmucks.entities.Player;
import com.mygdx.hadal.schmucks.entities.enemies.EnemyType;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.utils.b2d.HadalBody;

/**
 * A Trigger spawn is an enemy spawner that activates when triggered by another event.
 * Also, when all enemies are defeated, this event can trigger another event.
 * <p>
 * Triggered Behavior: When triggered, this will spawn a group of schmucks.
 * Triggering Behavior: When all spawned enemies are defeated, this will activate its connected event.
 * Alt-Triggered Behavior: When alt-triggered, this spawner changes the number of schmucks it will spawn at once.
 * <p>
 * 
 * Fields:
 * id: The id of the type of enemy to spawn
 * numEnemies: The number of enemies to spawn simultaneously
 * limit: the max number of enemies that can be spawned at once. If 0, no limit is used.
 * extraField: Extra field for enemies that require more information (like turret subtypes)
 * delay: float delay of how much time until the enemy is spawned. default: 1.0f
 * boss: boolean of whether this spawns a boss enemy
 * bossName: name of the boss spawned (if a boss is spawned)
 * @author Hafrodite Halligator
 */
public class SpawnerSchmuck extends Event {

	private int numEnemies;
	private final int limit;
	private final int extraField;
	private final float delay;
	private final boolean boss;
	private final String bossName;
	
	//this is the amount of enemies left
	private int amountLeft;
		
	private final EnemyType type;
	
	public SpawnerSchmuck(PlayState state, Vector2 startPos, Vector2 size, String schmuckId,int numEnemies,  int limit, int extraField, float delay, boolean boss, String bossName) {
		super(state, startPos, size);
		this.type = EnemyType.valueOf(schmuckId);
		this.numEnemies = numEnemies;
		this.limit = limit;
		this.extraField = extraField;
		this.delay = delay;
		this.boss = boss;
		this.bossName = bossName;
	}
	
	@Override
	public void create() {
		this.eventData = new EventData(this) {
			
			@Override
			public void onActivate(EventData activator, Player p) {
				if (!state.isServer()) { return; }
				
				if (activator.getEvent() instanceof TriggerAlt trigger) {
					numEnemies += Integer.parseInt(trigger.getMessage());
				} else {
					if (amountLeft < limit || limit == 0) {
						for (int i = 0; i < numEnemies; i++) {
							amountLeft++;
							type.generateEnemyDelayed(state, event.getPixelPosition(), delay, BodyConstants.ENEMY_HITBOX, extraField,
									(SpawnerSchmuck) event, boss, bossName);
						}
					}
				}
			}
		};

		this.body = new HadalBody(eventData, startPos, size, BodyConstants.BIT_SENSOR, (short) 0, (short) 0)
				.setBodyType(BodyDef.BodyType.KinematicBody)
				.addToWorld(world);
	}
	
	/**
	 * This event is run when an enemy spawned by this event dies.
	 */
	public void onDeath() {
		amountLeft--;
		
		//if all enemies spawned by this have been defeated, activate connected event
		if (amountLeft <= 0) {
			if (getConnectedEvent() != null) {
				getConnectedEvent().eventData.preActivate(eventData, null);
			}
			
			//if a boss was defeated, tell the ui to clear the boss hp bar from the ui
			if (boss) {
				state.getUIManager().clearBoss();
			}
		}
	}
}
