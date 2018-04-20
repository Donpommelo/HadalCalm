package com.mygdx.hadal.statuses;

import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;

public class StatusComposite extends Status {

	
	private Status[] statuses;
	
	public StatusComposite(PlayState state, float i, String name, Boolean perm, Boolean vis, Boolean end, Boolean dec,
			BodyData p, BodyData v, int pr, Status...statuses){
		super(state, i, name, perm, vis, end, dec, p, v, pr);
		this.statuses = statuses;
	}
	
	public void statChanges(BodyData bodyData){
		for (Status s : statuses) {
			s.statChanges(bodyData);
		}
	}
	
	public void timePassing(float delta) {
		for (Status s : statuses) {
			s.timePassing(delta);
		}
	}
	
	public float onDealDamage(float damage, BodyData vic, DamageTypes... tags) { 
		
		float finalDamage = damage;
		
		for (Status s : statuses) {
			finalDamage = s.onDealDamage(finalDamage, vic, tags);
		}
		
		return finalDamage;	
	}
	
	public float onReceiveDamage(float damage, BodyData perp, DamageTypes... tags) { 
		float finalDamage = damage;
		
		for (Status s : statuses) {
			finalDamage = s.onReceiveDamage(finalDamage, perp, tags);
		}
		
		return finalDamage;	
	}
	
	public float onHeal(float damage, BodyData perp, DamageTypes... tags) { 
		float finalDamage = damage;
		
		for (Status s : statuses) {
			finalDamage = s.onHeal(finalDamage, perp, tags);
		}
		
		return finalDamage;	 
	}
	
	public void onKill(BodyData vic) {
		for (Status s : statuses) {
			s.onKill(vic);
		}
	}
	
	public void onDeath(BodyData perp) {
		for (Status s : statuses) {
			s.onKill(perp);
		}
	}
}
