package com.mygdx.hadal.statuses;

import com.mygdx.hadal.equip.Equipable;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;

public class StatusComposite extends Status {

	
	private Status[] statuses;
	
	public StatusComposite(PlayState state, float i, String name, Boolean perm, Boolean vis, Boolean end, Boolean dec,
			BodyData p, BodyData v, int pr, Status...statuses){
		super(state, i, name, perm, vis, end, dec, p, v, pr);
		this.statuses = statuses;
	}
	
	public float statusProcTime(int procTime, BodyData schmuck, float amount, Status status, Equipable tool, DamageTypes... tags) {
		float finalAmount = amount;

		for (Status s: statuses) {
			finalAmount = s.statusProcTime(procTime, schmuck, finalAmount, status, tool, tags);
		}
		
		return finalAmount;
	}
}
