package com.mygdx.hadal.schmucks.bodies.enemies;

import com.badlogic.gdx.ai.steer.SteeringBehavior;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.equip.Equipable;
import com.mygdx.hadal.event.SpawnerSchmuck;
import com.mygdx.hadal.schmucks.bodies.HadalEntity;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.server.Packets;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.StatusProcTime;

/**
 * Enemies are Schmucks that attack the player.
 * @author Zachary Tu
 *
 */
public class Enemy extends Schmuck {
	
	//This is the entity this enemy is trying to attack
	protected HadalEntity target;
	
	//This is the type of enemy
	protected enemyType type;
	
	//This is the range that the enemy will be able to detect targets
    protected static final float aiRadius = 2000;

    //the enemy's base hp.
    protected int baseHp;
    
    //is this enemy a boss? (makes it show up in the boss ui)
    private boolean isBoss = false;
    private String name;
    
    //This is the event that spwner this enemy. Is null for the client and for enemies spawned in other ways.
    protected SpawnerSchmuck spawner;
    
	/**
	 * Enemy constructor is run when an enemy spawner makes a new enemy.
	 * @param state: current gameState
	 * @param world: box2d world
	 * @param camera: game camera
	 * @param rays: game rayhandler
	 * @param width: width of enemy
	 * @param height: height of enemy
	 * @param x: enemy starting x position.
	 * @param y: enemy starting x position.
	 */
	public Enemy(PlayState state, float width, float height, int x, int y, enemyType type, short filter, int baseHp, SpawnerSchmuck spawner) {
		super(state, width, height, x, y, filter);
		this.type = type;
		this.baseHp = baseHp;
		this.spawner = spawner;
	}
	
	/**
	 * Create the enemy's body and initialize enemy's user data.
	 */
	@Override
	public void create() {
		this.bodyData = new BodyData(this, baseHp) {
			
			@Override
			public void die(BodyData perp, Equipable tool) {
				if (schmuck.queueDeletion()) {
					if (spawner != null) {
						spawner.onDeath(schmuck);
					}
					perp.statusProcTime(StatusProcTime.ON_KILL, this, 0, null, tool, null);
					statusProcTime(StatusProcTime.ON_DEATH, perp, 0, null, currentTool, null);
				}	
			}
		};
	}
	
	/**
	 * When created in the server, tell the client what kind of enemy was reated to sync
	 */
	@Override
	public Object onServerCreate() {
		return new Packets.CreateEnemy(entityID.toString(), type, isBoss, name);
	}

	public HadalEntity getTarget() {
		return target;
	}

	public void setTarget(HadalEntity target, SteeringBehavior<Vector2> behavior) {
		super.setBehavior(behavior);
		this.target = target;
	}
	
	public boolean isBoss() {
		return isBoss;
	}

	public void setBoss(boolean isBoss) {
		this.isBoss = isBoss;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}



	public enum enemyType {
		SCISSORFISH,
		SPITTLEFISH,
		TORPEDOFISH,
		TURRET_FLAK,
		TURRET_VOLLEY,
		MISC, 
		BOSS,
	}
}
