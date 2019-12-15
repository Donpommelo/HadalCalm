package com.mygdx.hadal.statuses;

import com.mygdx.hadal.equip.Equipable;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;

/**
 * A Composite Status is a status that contains multiple other statuses.
 * @author Zachary Tu
 *
 */
public class StatusComposite extends Status {

	private Status[] statuses;
	
	public StatusComposite(PlayState state, float i, String name, String descr, Boolean perm, BodyData p, BodyData v, Status...statuses){
		super(state, i, name, descr, perm, p, v);
		this.statuses = statuses;
	}
	
	public StatusComposite(PlayState state, String name, String descr, BodyData i, Status...statuses){
		super(state, name, descr, i);
		this.statuses = statuses;
	}
	
	@Override
	public float statusProcTime(StatusProcTime procTime, BodyData schmuck, float amount, Status status, Equipable tool, Hitbox hbox, DamageTypes... tags) {
		super.statusProcTime(procTime, schmuck, amount, status, tool, hbox, tags);
		
		float finalAmount = amount;

		for (Status s: statuses) {
			finalAmount = s.statusProcTime(procTime, schmuck, finalAmount, status, tool, hbox, tags);
		}
		
		return finalAmount;
	}
}
