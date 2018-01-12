package com.mygdx.hadal.statuses;

import com.mygdx.hadal.schmucks.userdata.BodyData;

public class StatChangeStatus extends Status {

	public static String name = "Stats Changed";
	
	public int statNum;
	public double statIncrement;
	
	public StatChangeStatus(int i, int stat, float amount, BodyData p,	BodyData v, int pr) {
		super(i, name, false, false, true, true, p, v, pr);
		this.statNum = stat;
		this.statIncrement = amount;
	}
	
	public StatChangeStatus(int stat, float amount, BodyData p, BodyData v, int pr) {
		super(0, name, true, false, false, false, p, v, pr);
		this.statNum = stat;
		this.statIncrement = amount;
	}
	
	public void statChanges(BodyData bodyData){
		bodyData.buffedStats[statNum] += statIncrement;
	}

}
