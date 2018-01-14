package com.mygdx.hadal.statuses;

import com.mygdx.hadal.equip.Equipable;
import com.mygdx.hadal.equip.misc.GrenadeDropTest;
import com.mygdx.hadal.schmucks.userdata.BodyData;

public class TimedStatusTest extends Status {

	private static String name = "TEST";
	private float procCdCount;
	private float procCd = 8.0f;

	private Equipable weapon;
	
	public TimedStatusTest(int i, BodyData p, BodyData v, int pr) {
		super(i, name, false, false, true, true, p, v, pr);
		weapon = new GrenadeDropTest(perp.getSchmuck());
		this.procCdCount = 0;
	}
	
	public TimedStatusTest(BodyData p, BodyData v, int pr) {
		super(0, name, true, false, false, false, p, v, pr);
	}
	
	public void timePassing(float delta) {
	
		if (procCdCount >= procCd) {
			procCdCount -= procCd;
//			weapon.mouseClicked(delta, state, bodyData, faction, x, y, world, camera, rays);
		}
		
		
		procCdCount += delta;
	}
	
}
