package com.mygdx.hadal.statuses;

import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;

/**
 * A Composite Status is a status that contains multiple other statuses.
 * @author Zachary Tu
 *
 */
public class StatusComposite extends Status {

	private Status[] statuses;
	
	public StatusComposite(PlayState state, float i, boolean perm, BodyData p, BodyData v, Status...statuses){
		super(state, i, perm, p, v);
		this.statuses = statuses;
	}
	
	public StatusComposite(PlayState state, BodyData i, Status...statuses){
		super(state, i);
		this.statuses = statuses;
	}
	
	@Override
	public float statusProcTime(Object o) {
		super.statusProcTime(o);
		
		float finalAmount = 0;
		
		for (Status s: statuses) {
			finalAmount = s.statusProcTime(o);
		}
		
		return finalAmount;
	}
}
