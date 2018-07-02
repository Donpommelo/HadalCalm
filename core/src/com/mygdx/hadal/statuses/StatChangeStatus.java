package com.mygdx.hadal.statuses;

import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;

public class StatChangeStatus extends Status {

	private static String name = "Stats Changed";
	private static String descr = "Stats Changed";
	
	private int statNum;
	private float statIncrement;
	
	public StatChangeStatus(PlayState state, float i, int stat, float amount, BodyData p, BodyData v) {
		super(state, i, name, descr, false, false, true, true, p, v);
		this.statNum = stat;
		this.statIncrement = amount;
	}
	
	public StatChangeStatus(PlayState state, int stat, float amount, BodyData i) {
		super(state, name, descr, i);
		this.statNum = stat;
		this.statIncrement = amount;
	}
	
	@Override
	public void statChanges(){
		inflicted.setStat(statNum, inflicted.getStat(statNum) + statIncrement);
	}

}
