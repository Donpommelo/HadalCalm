package com.mygdx.hadal.statuses;

import com.mygdx.hadal.equip.Equipable;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;

public class Status {

	//References to game fields.
	protected PlayState state;
	
	protected float duration;
	protected String name, descr;
	protected boolean perm, visible;
	
	protected BodyData inflicter, inflicted;
	
	public Status(PlayState state, float i, String n, String d, Boolean perm, Boolean vis, BodyData p, BodyData v){
		this.state = state;

		this.duration=i;
		this.name = n;
		this.descr = d;
		this.perm = perm;
		this.visible = vis;
		this.inflicter = p;
		this.inflicted = v;
	}
	
	public Status(PlayState state, String n, String d, Boolean vis, BodyData i) {
		this(state, 0, n, d, true, vis, i, i);
	}
	
	public Status(PlayState state, String n, String d, BodyData i) {
		this(state, 0, n, d, true, true, i, i);
	}
	
	public float statusProcTime(StatusProcTime procTime, BodyData schmuck, float amount, Status status, Equipable tool, Hitbox hbox, DamageTypes... tags) {
		float finalAmount = amount;
		
		switch(procTime) {
		case ON_INFLICT:
			onInflict();
			break;
		case ON_REMOVE:
			onRemove();
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
		case WHILE_SHOOTING:
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
	
	public void onInflict() {}
	
	public void onRemove() {}
	
	public void statChanges(){}
	
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

	
	public String getName() {
		return name;
	}
		
	public String getDescr() {
		return descr;
	}

	public void reset(PlayState state, BodyData inflicted, BodyData inflicter) {
		this.state = state;
		this.inflicted = inflicted;
		this.inflicter = inflicter;
	}

	public boolean isVisible() {
		return visible;
	}	
}
