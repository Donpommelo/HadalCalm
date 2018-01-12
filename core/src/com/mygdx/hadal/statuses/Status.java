package com.mygdx.hadal.statuses;

import com.mygdx.hadal.schmucks.userdata.BodyData;

public class Status {

	public float duration;
	public String name;
	public boolean perm, decay, removedEnd, visible;
	
	public int priority;
	
	public BodyData perp, vic;
	
	public Status(int i, String n, Boolean perm, Boolean vis, Boolean end, Boolean dec, BodyData p, BodyData v, int pr){
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
	
	public float onDealDamage(float damage, BodyData vic) {
		return damage;
	}
	
	public float onReceiveDamage(float damage, BodyData perp) {
		return damage;
	}
}
