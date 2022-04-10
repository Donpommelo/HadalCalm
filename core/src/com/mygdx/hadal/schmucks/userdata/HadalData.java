package com.mygdx.hadal.schmucks.userdata;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.schmucks.UserDataType;
import com.mygdx.hadal.schmucks.entities.HadalEntity;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.battle.DamageTag;

/**
 * This is the basic user data of any entity. The only thing this needs to do is keep track of type and basic stuff like that.
 * @author Perudatter Pranella
 */
public class HadalData {

	//the number of other entities or walls this entity is touching. Atm only used for feet to determine grounded
	private int numContacts;
	
	//Enum that describes the type of entity. This field is often checked on contact.
	private final UserDataType type;
	
	//The entity that owns this data
	private HadalEntity entity;
	
	/**
	 * aye
	 */
	public HadalData(UserDataType type, HadalEntity entity) {
		this.type = type;
		this.entity = entity;
	}

	/**
	 * This method is called when this entity gets hit. Most non-body entities will only care about the kb.
	 * @param basedamage : amount of damage received
	 * @param knockback : amount of knockback to apply.
	 * @param perp : the schmuck who did this
	 * @param procEffects : Should this proc status effects?
	 * @param hbox: hbox that inflicted the damage. Null if not inflicted by a hbox
	 * @param source : attack/weapon source of this damage
	 * @param tags : damage tags used for type-specific damage resistance/amplification
	 */
	public float receiveDamage(float basedamage, Vector2 knockback, BodyData perp, Boolean procEffects, Hitbox hbox,
							   DamageSource source, DamageTag... tags) {
		if (getEntity().isAlive()) {
			getEntity().push(knockback);
		}
		return basedamage;
	}
	
	public int getNumContacts() { return numContacts; }

	public void setNumContacts(int numContacts) { this.numContacts = numContacts; }

	public UserDataType getType() { return type; }

	public HadalEntity getEntity() { return entity; }

	public void setEntity(HadalEntity entity) { this.entity = entity; }
}
