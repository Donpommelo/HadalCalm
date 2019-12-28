package com.mygdx.hadal.statuses;

import com.mygdx.hadal.equip.Equipable;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;

/**
 * A status is a thing that afflicts a schmuck and has some affect for its duration.
 * @author Zachary Tu
 *
 */
public class Status {

	//References to game fields.
	protected PlayState state;
	
	//How long until the status (if temporary) is removed
	protected float duration;
	
	//Is this status removed when its duration expires?
	protected boolean perm;
	
	//The Data of the Schmuck that received/inflicted this status.
	protected BodyData inflicter, inflicted;
	
	public Status(PlayState state, float i, Boolean perm, BodyData p, BodyData v){
		this.state = state;
		this.duration = i;
		this.perm = perm;
		this.inflicter = p;
		this.inflicted = v;
	}
	
	public Status(PlayState state, BodyData i) {
		this(state, 0, true, i, i);
	}
	
	/**
	 * Each status runs this at any Status Proc Time. This triggers the effects of statuses
	 * Each input is some information that a specific proc time needs to process.
	 */
	public float statusProcTime(StatusProcTime procTime, BodyData schmuck, float amount, Status status, Equipable tool, Hitbox hbox, DamageTypes... tags) {
		
		float finalAmount = amount;
		
		switch(procTime) {
		case ON_INFLICT:
			onInflict(status);
			break;
		case ON_REMOVE:
			onRemove(status);
			break;
		case STAT_CHANGE:
			statChanges();
			break;
		case DEAL_DAMAGE:
			finalAmount = onDealDamage(finalAmount, schmuck, tags);
			break;
		case RECEIVE_DAMAGE:
			finalAmount = onReceiveDamage(finalAmount, schmuck, tags);
			break;
		case TIME_PASS:
			timePassing(amount);
			break;
		case ON_KILL:
			onKill(schmuck);
			break;
		case ON_DEATH:
			onDeath(schmuck);
			break;
		case ON_HEAL:
			finalAmount = onHeal(finalAmount, schmuck, tags);
			break;
		case WHILE_ATTACKING:
			whileShooting(amount, tool);
			break;
		case ON_SHOOT:
			onShoot(tool);
			break;
		case WHILE_RELOADING:
			whileReloading(amount, tool);
			break;
		case ON_RELOAD:
			onReload(tool);
			break;
		case HITBOX_CREATION:
			onHitboxCreation(hbox);
			break;
		case LEVEL_START:
			levelStart();
			break;
		case ON_AIRBLAST:
			onAirBlast(tool);
			break;
		default:
			break;
		}
		return finalAmount;
	}
	
	/**
	 * This is run when any status is inflicted. (Not just this one)
	 * @param s: Inflicted Status
	 */
	public void onInflict(Status s) {}
	
	/**
	 * This is run when this status is removed. (Not just this one)
	 * @param s: Removed Status
	 */
	public void onRemove(Status s) {}
	
	/**
	 * This is called to calculate a schmuck's buffed stats
	 */
	public void statChanges(){}
	
	/**
	 * This is called evey engine tick. Base Behavior: decrement duration and remove if 0 for temporary events.
	 * @param delta: The amount of time passed.
	 */
	public void timePassing(float delta) {
		if (!perm) { 
			duration -= delta;
			
			if (duration <= 0) {
				inflicted.removeStatus(this);
			}
		}
	}

	public float onDealDamage(float damage, BodyData vic, DamageTypes... tags) { return damage;	}
	
	public float onReceiveDamage(float damage, BodyData perp, DamageTypes... tags) { return damage; }
	
	public float onHeal(float damage, BodyData perp, DamageTypes... tags) { return damage; }
	
	public void onKill(BodyData vic) {}
	
	public void onDeath(BodyData perp) {}

	public void whileShooting(float delta, Equipable tool) {}
	
	public void onShoot(Equipable tool) {}
	
	public void whileReloading(float delta, Equipable tool) {}
	
	public void onReload(Equipable tool) {}
	
	public void onHitboxCreation(Hitbox hbox) {}
	
	public void levelStart() {}
	
	public void onAirBlast(Equipable tool) {}

	public BodyData getInflicter() { return inflicter; }

	public void setInflicter(BodyData inflicter) { this.inflicter = inflicter; }

	public BodyData getInflicted() { return inflicted; }

	public void setInflicted(BodyData inflicted) { this.inflicted = inflicted; }
	
	public float getDuration() { return duration; }

	public void setDuration(float duration) { this.duration = duration; }

	/**
	 * This determines the behavior is this status is added to a schmuckwho already has it.
	 * @return: stack behavior
	 */
	public statusStackType getStackType() {	return statusStackType.ADD;	}
	
	public enum statusStackType {
		ADD,
		REPLACE,
		IGNORE,
	}
}
