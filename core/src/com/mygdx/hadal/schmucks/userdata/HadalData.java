package com.mygdx.hadal.schmucks.userdata;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.schmucks.UserDataTypes;
import com.mygdx.hadal.schmucks.bodies.HadalEntity;
import com.mygdx.hadal.statuses.DamageTypes;

/**
 * This is the basic user data of any entity. The only thing this needs to do is keep track of type and basic stuff like that.
 * @author Zachary Tu
 */
public class HadalData {

	//the number of other entities or walls this entity is touching. Atm only used for feet to determine groundedness
	private int numContacts;
	
	//Enum that describes the type of entity. This field is often checked on contact.
	private UserDataTypes type;
	
	//The entity that owns this data
	private HadalEntity entity;
	
	/**
	 * aye
	 */
	public HadalData(UserDataTypes type, HadalEntity entity) {
		this.type = type;
		this.entity = entity;
	}

	/**
	 * This method is called when this entity gets hit. Most non-body entities will only care about the kb.
	 * @param basedamage: amount of damage received
	 * @param knockback: amount of knockback to apply.
	 * @param perp: the schmuck who did this
	 * @param procEffects: Should this proc status effects?
	 * @param tags: damage tags
	 */
	public float receiveDamage(float basedamage, Vector2 knockback, BodyData perp, Boolean procEffects, DamageTypes... tags) {
		if (getEntity().isAlive()) {
			getEntity().push(knockback);
		}
		return basedamage;
	}
	
	public int getNumContacts() { return numContacts; }

	public void setNumContacts(int numContacts) { this.numContacts = numContacts; }

	public UserDataTypes getType() { return type; }

	public HadalEntity getEntity() { return entity; }

	public void setEntity(HadalEntity entity) { this.entity = entity; }
}
