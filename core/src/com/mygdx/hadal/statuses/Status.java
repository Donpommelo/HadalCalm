package com.mygdx.hadal.statuses;

import com.mygdx.hadal.equip.ActiveItem;
import com.mygdx.hadal.equip.Equipable;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.ProcTime.*;

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
	public float statusProcTime(Object o) {
		
		float finalAmount = 0;
		
		if (o instanceof ProcTime.PlayerCreate) { 
			playerCreate();
		} else if (o instanceof StatusInflict){
			StatusInflict pt = (StatusInflict)o;
			onInflict(pt.s);
		} else if (o instanceof StatusRemove){
			StatusRemove pt = (StatusRemove)o;
			onRemove(pt.s);
		} else if (o instanceof StatCalc){
			statChanges();
		} else if (o instanceof ReceiveDamage){
			ReceiveDamage pt = (ReceiveDamage)o;
			finalAmount = onReceiveDamage(pt.damage, pt.perp, pt.tags);
		} else if (o instanceof InflictDamage){
			InflictDamage pt = (InflictDamage)o;
			finalAmount = onDealDamage(pt.damage, pt.vic, pt.tags);
		} else if (o instanceof ReceiveHeal){
			ReceiveHeal pt = (ReceiveHeal)o;
			finalAmount = onHeal(pt.heal, pt.perp, pt.tags);
		} else if (o instanceof Kill){
			Kill pt = (Kill)o;
			onKill(pt.vic);
		} else if (o instanceof Death){
			Death pt = (Death)o;
			onDeath(pt.perp);
		} else if (o instanceof TimePass){
			TimePass pt = (TimePass)o;
			timePassing(pt.time);
		} else if (o instanceof WhileAttack){
			WhileAttack pt = (WhileAttack)o;
			whileAttacking(pt.time, pt.tool);
		} else if (o instanceof Shoot){
			Shoot pt = (Shoot)o;
			onShoot(pt.tool);
		} else if (o instanceof Reload){
			Reload pt = (Reload)o;
			onReload(pt.tool);
		} else if (o instanceof CreateHitbox){
			CreateHitbox pt = (CreateHitbox)o;
			onHitboxCreation(pt.hbox);
		} else if (o instanceof PlayerCreate){
			playerCreate();
		} else if (o instanceof Airblast){
			Airblast pt = (Airblast)o;
			onAirBlast(pt.tool);
		} else if (o instanceof ActiveUse){
			ActiveUse pt = (ActiveUse)o;
			beforeActiveItem(pt.tool);
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

	public void whileAttacking(float delta, Equipable tool) {}
	
	public void onShoot(Equipable tool) {}
	
	public void onReload(Equipable tool) {}
	
	public void onHitboxCreation(Hitbox hbox) {}
	
	public void playerCreate() {}
	
	public void onAirBlast(Equipable tool) {}

	public void beforeActiveItem(ActiveItem tool) {}
	
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
