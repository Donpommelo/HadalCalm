package com.mygdx.hadal.schmucks.bodies.enemies;

import com.badlogic.gdx.ai.steer.SteeringBehavior;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.event.SpawnerSchmuck;
import com.mygdx.hadal.schmucks.bodies.HadalEntity;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.server.Packets;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.ProcTime;

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
    
    //this is the size of the enemy's hitbox
    protected Vector2 hboxSize;
    
    /**
     * 
     * @param state: current play state
     * @param startPos: starting position in screen coordinates
     * @param size: current size in pixel
     * @param hboxSize: hbox size
     * @param type: type of enemy
     * @param filter: hitbox filter that determines faction
     * @param baseHp: base hp of enemy
     * @param spawner: the event that spawned this enemy
     */
	public Enemy(PlayState state, Vector2 startPos, Vector2 size, Vector2 hboxSize, enemyType type, short filter, int baseHp, SpawnerSchmuck spawner) {
		super(state, startPos, size, filter);
		this.hboxSize = hboxSize;
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
			public void die(BodyData perp) {
				if (schmuck.queueDeletion()) {
					
					//if this was spawned by an spawing event, run its on-death method
					if (spawner != null) {
						spawner.onDeath(schmuck);
					}
					perp.statusProcTime(new ProcTime.Kill(this));
					statusProcTime(new ProcTime.Death(perp));
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

	public HadalEntity getTarget() { return target; }

	public void setTarget(HadalEntity target, SteeringBehavior<Vector2> behavior) {
		super.setBehavior(behavior);
		this.target = target;
	}
	
	public void setBoss(boolean isBoss) { this.isBoss = isBoss; }

	public String getName() { return name; }

	public void setName(String name) { this.name = name; }

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
