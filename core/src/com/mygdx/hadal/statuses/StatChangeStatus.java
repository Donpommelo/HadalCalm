package com.mygdx.hadal.statuses;

import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;

/**
 * This Status represents a change in any Schmuck stat
 * @author Zukins Zorbcourt
 */
public class StatChangeStatus extends Status {

	//Which stat will be changed
	private final int statNum;
	
	//How much will the stat be changed by
	private final float statIncrement;
	
	public StatChangeStatus(PlayState state, float i, int stat, float amount, BodyData p, BodyData v) {
		super(state, i, false, p, v);
		this.statNum = stat;
		this.statIncrement = amount;
	}
	
	public StatChangeStatus(PlayState state, int stat, float amount, BodyData i) {
		super(state, i);
		this.statNum = stat;
		this.statIncrement = amount;
	}
	
	@Override
	public void statChanges() {
		inflicted.setStat(statNum, inflicted.getStat(statNum) + statIncrement);
	}
}
