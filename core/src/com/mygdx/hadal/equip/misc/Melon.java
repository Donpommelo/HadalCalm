package com.mygdx.hadal.equip.misc;
import com.mygdx.hadal.equip.Consumable;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.StatChangeStatus;

public class Melon extends Consumable {

	private final static String name = "Melon";
	private final static float useCd = 1.50f;
	private final static float useDelay = 0.0f;
	private final static int charges = 3;
	
	private final static float duration = 5.0f;
	private final static float power = 8.0f;
	
	public Melon(Schmuck user) {
		super(user, name, useCd, useDelay, charges);
	}
	
	@Override
	public void execute(PlayState state, BodyData bodyData) {
		
		bodyData.addStatus(new StatChangeStatus(state, duration, 2, power, bodyData, bodyData));
		
		super.execute(state, bodyData);
	}
}
