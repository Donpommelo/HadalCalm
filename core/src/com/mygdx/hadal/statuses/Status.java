package com.mygdx.hadal.statuses;

import com.mygdx.hadal.equip.ActiveItem;
import com.mygdx.hadal.equip.Equippable;
import com.mygdx.hadal.save.UnlockArtifact;
import com.mygdx.hadal.schmucks.bodies.enemies.Enemy;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;

/**
 * A status is a thing that afflicts a schmuck and has some affect for its duration.
 * @author Zachary Tu
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
	
	//this is the artifact that this status is attached to. (null for non-artifact statuses). This is used to remove an artifact statuses when unequipping
	private UnlockArtifact artifact;
	
	public Status(PlayState state, float i, Boolean perm, BodyData p, BodyData v) {
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
	public ProcTime statusProcTime(ProcTime o) {
		return o.statusProcTime(this);
	}
	
	/**
	 * This is run when this status is inflicted.
	 */
	public void onInflict() {}
	
	/**
	 * This is run when this status is removed.
	 */
	public void onRemove() {}
	
	/**
	 * This is called to calculate a schmuck's buffed stats
	 */
	public void statChanges() {}
	
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

	public void whileAttacking(float delta, Equippable tool) {}
	
	public void onShoot(Equippable tool) {}
	
	public void onReload(Equippable tool) {}
	
	public void onHitboxCreation(Hitbox hbox) {}
	
	public void playerCreate() {}
	
	public void scrapPickup() {}
	
	public void onAirBlast(Equippable tool) {}

	public void beforeActiveItem(ActiveItem tool) {}
	
	public void afterActiveItem(ActiveItem tool) {}
	
	public void afterBossSpawn(Enemy boss) {}
	
	public BodyData getInflicter() { return inflicter; }

	public void setInflicter(BodyData inflicter) { this.inflicter = inflicter; }

	public BodyData getInflicted() { return inflicted; }

	public void setInflicted(BodyData inflicted) { this.inflicted = inflicted; }
	
	public float getDuration() { return duration; }

	public void setDuration(float duration) { this.duration = duration; }

	public UnlockArtifact getArtifact() { return artifact; }

	public void setArtifact(UnlockArtifact artifact) { this.artifact = artifact; }

	/**
	 * This determines the behavior is this status is added to a schmuck who already has it.
	 * @return stack behavior
	 */
	public statusStackType getStackType() {	return statusStackType.ADD;	}
	
	public enum statusStackType {
		ADD,
		REPLACE,
		IGNORE,
	}
}
