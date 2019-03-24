package com.mygdx.hadal.statuses;

import static com.mygdx.hadal.utils.Constants.PPM;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.equip.WeaponUtils;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;

public class LaunchingMissiles extends Status {

	private static String name = "Launching Missiles";
	private static String descr = "Missiles";
	
	private float procCdCount;
	private float procCd = .1f;
	
	public LaunchingMissiles(PlayState state, float i, BodyData p, BodyData v) {
		super(state, i, name, descr, false, p, v);
	}
	
	@Override
	public void timePassing(float delta) {
		super.timePassing(delta);
		if (procCdCount >= procCd) {
			procCdCount -= procCd;
			WeaponUtils.createHomingTorpedo(state, 
					inflicted.getSchmuck().getBody().getPosition().x * PPM, 
					inflicted.getSchmuck().getBody().getPosition().y * PPM,
					inflicted.getSchmuck(), inflicted.getCurrentTool(), 1, 15, new Vector2(0, 1), false, inflicted.getSchmuck().getHitboxfilter());
		}
		procCdCount += delta;
	}
}
