package com.mygdx.hadal.statuses.artifact;

import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Status;

public class ScalingScalesStatus extends Status {

	private static String name = "Scaling";
	
	public ScalingScalesStatus(PlayState state, BodyData i) {
		super(state, name, i);
	}
}
