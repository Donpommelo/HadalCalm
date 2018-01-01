package com.mygdx.hadal.schmucks.userdata;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.hadal.event.ai.Zone;
import com.mygdx.hadal.schmucks.UserDataTypes;
import com.mygdx.hadal.schmucks.bodies.Schmuck;

/**
 * Body data contains the stats and methods of any unit; player or enemy.
 * @author Zachary Tu
 *
 */
public class BodyData extends HadalData {

	//The Schmuck that owns this data
	public Schmuck schmuck;
	
	//Speed on ground
	public float maxGroundXSpeed = 15.0f;
	public float maxAirXSpeed = 10.0f;
	
	//Speed on ground
	public float maxGroundYSpeed = 10.0f;
	public float maxAirYSpeed = 7.5f;
		
	//Accelerating on the ground/air
	public float groundXAccel = 0.10f;
	public float airXAccel = 0.05f;
	public float groundXDeaccel = 0.05f;
	public float airXDeaccel = 0.01f;
	
	public float groundYAccel = 0.10f;
	public float airYAccel = 0.50f;
	public float groundYDeaccel = 0.01f;
	public float airYDeaccel = 0.01f;
	
	//Hp and regen
	public int maxHp = 100;
	public float currentHp = 100;
	public float hpRegen = 0.0f;
	
	public Zone currentZone;
	
	/**
	 * This is created upon the create() method of any schmuck.
	 * Schmucks are the Body data type.
	 * @param world
	 * @param schmuck
	 */
	public BodyData(World world, Schmuck schmuck) {
		super(world, UserDataTypes.BODY, schmuck);
		this.schmuck = schmuck;		
	}	
	
	/**
	 * This method is called when this schmuck receives damage.
	 * @param basedamage: amount of damage received
	 * @param knockback: amount of knockback to apply.
	 *TODO: include the source of damage
	 */
	public void receiveDamage(float basedamage, Vector2 knockback) {
		currentHp -= basedamage;
		schmuck.getBody().applyLinearImpulse(knockback, schmuck.getBody().getLocalCenter(), true);
		if (currentHp <= 0) {
			currentHp = 0;
			die();
		}
	}
	
	/**
	 * This method is called when the schmuck is healed
	 * @param heal: amount of Hp to regenerate
	 */
	public void regainHp(float heal) {
		currentHp += heal;
		if (currentHp >= maxHp) {
			currentHp = maxHp;
		}
	}
	
	/**
	 * This method is called when the schmuck dies. Queue up to be deleted next engine tick.
	 */
	public void die() {
		schmuck.queueDeletion();
	}

	public Schmuck getSchmuck() {
		return schmuck;
	}
}
