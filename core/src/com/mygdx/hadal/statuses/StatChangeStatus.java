package com.mygdx.hadal.statuses;

import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;

public class StatChangeStatus extends Status {

	private static String name = "Stats Changed";
	
	private int statNum;
	private float statIncrement;
	
	public StatChangeStatus(PlayState state, float i, int stat, float amount, BodyData p, BodyData v, int pr) {
		super(state, i, name, false, false, true, true, p, v, pr);
		this.statNum = stat;
		this.statIncrement = amount;
	}
	
	public StatChangeStatus(PlayState state, int stat, float amount, BodyData p, BodyData v, int pr) {
		super(state, 0, name, true, false, false, false, p, v, pr);
		this.statNum = stat;
		this.statIncrement = amount;
	}
	
	@Override
	public void statChanges(){
		vic.setStat(statNum, vic.getStat(statNum) + statIncrement);
	}

}
