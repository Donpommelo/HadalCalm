package com.mygdx.hadal.statuses;

import com.mygdx.hadal.equip.Equipable;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
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
	
	public float statusProcTime(int procTime, BodyData schmuck, float amount, Status status, Equipable tool, Hitbox hbox, DamageTypes... tags) {
		float finalAmount = amount;
		
		switch(procTime) {
		case 0:
			statChanges();
			break;
		case 1:
			finalAmount = onDealDamage(finalAmount, schmuck, tags);
			break;
		case 2:
			finalAmount = onReceiveDamage(finalAmount, schmuck, tags);
			break;
		case 3:
			timePassing(amount);
			break;
		case 4:
			onKill(schmuck);
			break;
		case 5:
			onDeath(schmuck);
			break;
		case 6:
			finalAmount = onHeal(finalAmount, schmuck, tags);
			break;
		case 7:
			whileShooting(amount, tool);
			break;
		case 8:
			onShoot(tool);
			break;
		case 9:
			whileReloading(amount, tool);
			break;
		case 10:
			onReload(tool);
			break;
		case 11:
			onHitboxCreation(hbox);
			break;
		}
		
		return finalAmount;
	}
	
	public void statChanges(){
		
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

	public void whileShooting(float delta, Equipable tool) {}
	
	public void onShoot(Equipable tool) {}
	
	public void whileReloading(float delta, Equipable tool) {}
	
	public void onReload(Equipable tool) {}
	
	public void onHitboxCreation(Hitbox hbox) {}
	
	public String getName() {
		return name;
	}
}
