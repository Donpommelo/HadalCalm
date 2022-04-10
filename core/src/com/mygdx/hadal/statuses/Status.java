package com.mygdx.hadal.statuses;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.battle.DamageTag;
import com.mygdx.hadal.equip.ActiveItem;
import com.mygdx.hadal.equip.Equippable;
import com.mygdx.hadal.save.UnlockArtifact;
import com.mygdx.hadal.schmucks.entities.enemies.Enemy;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;

import static com.mygdx.hadal.utils.Constants.PRIORITY_DEFAULT;

/**
 * A status is a thing that afflicts a schmuck and has some affect for its duration.
 * @author Whurabeau Wrongenrique
 */
public class Status implements Comparable<Status> {

	//References to game fields.
	protected final PlayState state;
	
	//How long until the status (if temporary) is removed
	protected float duration;
	
	//Is this status removed when its duration expires?
	protected final boolean perm;
	
	//The Data of the Schmuck that received/inflicted this status.
	protected final BodyData inflicter, inflicted;
	
	//this is the artifact that this status is attached to. (null for non-artifact statuses). This is used to remove an artifact statuses when unequipping
	private UnlockArtifact artifact;

	//Status priority determins the order in which multiple statuses will proc their effects (small number = goes first)
	private int priority = PRIORITY_DEFAULT;

	private boolean clientIndependent;
	
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

	public float onDealDamage(float damage, BodyData vic, Hitbox damaging, DamageSource source, DamageTag... tags) { return damage; }
	
	public float onReceiveDamage(float damage, BodyData perp, Hitbox damaging, DamageSource source, DamageTag... tags) { return damage; }

	public float onHeal(float damage, BodyData perp, DamageTag... tags) { return damage; }
	
	public void onKill(BodyData vic, DamageSource source) {}
	
	public void onDeath(BodyData perp, DamageSource source) {}

	public void whileAttacking(float delta, Equippable tool) {}
	
	public void onShoot(Equippable tool) {}

	public void onReloadStart(Equippable tool) {}

	public void onReloadFinish(Equippable tool) {}
	
	public void onHitboxCreation(Hitbox hbox) {}
	
	public void playerCreate() {}
	
	public void scrapPickup() {}
	
	public void onAirBlast(Equippable tool) {}

	public void whileHover(Vector2 hoverDirection) {}

	public void beforeActiveItem(ActiveItem tool) {}
	
	public void afterActiveItem(ActiveItem tool) {}
	
	public void afterBossSpawn(Enemy boss) {}

	public float getDuration() { return duration; }

	public void setDuration(float duration) { this.duration = duration; }

	public UnlockArtifact getArtifact() { return artifact; }

	public void setArtifact(UnlockArtifact artifact) { this.artifact = artifact; }

	public boolean isClientIndependent() { return clientIndependent; }

	/**
	 * This determines the behavior is this status is added to a schmuck who already has it.
	 * @return stack behavior
	 */
	public statusStackType getStackType() {	return statusStackType.ADD;	}

	public Status setPriority(int priority) {
		this.priority = priority;
		return this;
	}

	public Status setClientIndependent(boolean clientIndependent) {
		this.clientIndependent = clientIndependent;
		return this;
	}

	@Override
	public int compareTo(Status o) {
		return priority - o.priority;
	}

	public enum statusStackType {
		ADD,
		REPLACE,
		IGNORE,
		INCREMENT_DURATION
	}
}
