package com.mygdx.hadal.statuses;

import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;

/**
 * A Composite Status is a status that contains multiple other statuses.
 * @author Zachary Tu
 */
public class StatusComposite extends Status {

	//these are the statuses that are contained in this status.
	private final Status[] statuses;
	
	public StatusComposite(PlayState state, float i, boolean perm, BodyData p, BodyData v, Status...statuses){
		super(state, i, perm, p, v);
		this.statuses = statuses;
	}
	
	public StatusComposite(PlayState state, BodyData i, Status...statuses){
		super(state, i);
		this.statuses = statuses;
	}
	
	@Override
	public ProcTime statusProcTime(ProcTime o) {
		super.statusProcTime(o);
		
		ProcTime finalProcTime = o;
		
		for (Status s: statuses) {
			finalProcTime = s.statusProcTime(o);
		}
		return finalProcTime;
	}
}
