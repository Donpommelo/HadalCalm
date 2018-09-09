package com.mygdx.hadal.equip.artifacts;

import static com.mygdx.hadal.utils.Constants.PPM;

import com.mygdx.hadal.event.Poison;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Status;

public class RedTideTalisman extends Artifact {

	private final static String name = "Red Tide Talisman";
	private final static String descr = "Create poison cloud on kill.";
	private final static String descrLong = "";
	private final static int statusNum = 1;
	
	private final static int poisonRadius = 150;
	private final static float poisonDamage = 40/60f;
	private final static float poisonDuration = 3.0f;
	
	public RedTideTalisman() {
		super(name, descr, descrLong, statusNum);
	}

	@Override
	public Status[] loadEnchantments(PlayState state, BodyData b) {
		enchantment[0] = new Status(state, name, descr, b) {
			
			private float procCdCount;
			private float procCd = .5f;
			
			@Override
			public void timePassing(float delta) {
				if (procCdCount < procCd) {
					procCdCount += delta;

				}
			}
			
			@Override
			public void onKill(BodyData vic) {
				if (procCdCount >= procCd) {
					procCdCount -= procCd;
					new Poison(state, poisonRadius, poisonRadius,
							(int)(vic.getSchmuck().getBody().getPosition().x * PPM), 
							(int)(vic.getSchmuck().getBody().getPosition().y * PPM), 
							poisonDamage, poisonDuration, inflicter.getSchmuck(), true, inflicter.getSchmuck().getHitboxfilter());
				}
			}
		};
		
		return enchantment;
	}
}
