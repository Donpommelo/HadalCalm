package com.mygdx.hadal.statuses;

import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;

public class Status {

	//References to game fields.
	protected PlayState state;
	
	//TODO:implement or delete these 
	protected float duration;
	protected String name;
	protected boolean perm, decay, removedEnd, visible;
	
	protected int priority;
	
	protected BodyData perp, vic;
	
	public Status(PlayState state, float i, String n, Boolean perm, Boolean vis, Boolean end, Boolean dec, 
			BodyData p, BodyData v, int pr){
		this.state = state;

		this.duration=i;
		this.name = n;
		this.perm = perm;
		this.visible = vis;
		this.removedEnd = end;
		this.decay = dec;
		this.perp = p;
		this.vic = v;
		this.priority = pr;
		
	}
	
	public void statChanges(BodyData bodyData){
		
	}
	
	public void timePassing(float delta) {
		if (decay) { 
			duration -= delta;
			
			if (duration <= 0 && !perm) {
				vic.removeStatus(this);
			}
		}
	}
	
	public float onDealDamage(float damage, BodyData vic, DamageTypes... tags) { return damage;	}
	
	public float onReceiveDamage(float damage, BodyData perp, DamageTypes... tags) { return damage; }
	
	public float onHeal(float damage, BodyData perp, DamageTypes... tags) { return damage; }
	
	public void onKill(BodyData vic) {}
	
	public void onDeath(BodyData perp) {}

	public String getName() {
		return name;
	}
}
