package com.mygdx.hadal.equip.artifacts;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.event.Poison;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Status;

public class RedTideTalisman extends Artifact {

	private final static int statusNum = 1;
	private final static int slotCost = 2;
	
	private final static Vector2 poisonSize = new Vector2(150, 150);
	private final static float poisonDamage = 40/60f;
	private final static float poisonDuration = 3.0f;
	
	public RedTideTalisman() {
		super(slotCost, statusNum);
	}

	@Override
	public Status[] loadEnchantments(PlayState state, BodyData b) {
		enchantment[0] = new Status(state, b) {
			
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
					new Poison(state, vic.getSchmuck().getPixelPosition(), poisonSize, poisonDamage, poisonDuration, inflicter.getSchmuck(), true, inflicter.getSchmuck().getHitboxfilter());
				}
			}
		};
		
		return enchantment;
	}
}
