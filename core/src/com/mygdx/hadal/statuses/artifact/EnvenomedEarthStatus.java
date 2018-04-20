package com.mygdx.hadal.statuses.artifact;

import static com.mygdx.hadal.utils.Constants.PPM;

import com.mygdx.hadal.event.Poison;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Status;

public class EnvenomedEarthStatus extends Status {

	private static String name = "Envenomed";
	
	private final static int poisonRadius = 150;
	private final static float poisonDamage = 40/60f;
	private final static float poisonDuration = 3.0f;
	
	public EnvenomedEarthStatus(PlayState state, BodyData p, BodyData v, int pr) {
		super(state, 0, name, true, false, false, false, p, v, pr);
	}
	
	@Override
	public void onKill(BodyData vic) {
		new Poison(state, poisonRadius, poisonRadius,
				(int)(vic.getSchmuck().getBody().getPosition().x * PPM), 
				(int)(vic.getSchmuck().getBody().getPosition().y * PPM), 
				poisonDamage, poisonDuration, perp.getSchmuck(), perp.getSchmuck().getHitboxfilter());
	}
}
