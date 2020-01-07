package com.mygdx.hadal.equip.artifacts;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.equip.Equipable;
import com.mygdx.hadal.equip.WeaponUtils;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Status;

public class WrathoftheFrogman extends Artifact {

	private final static int statusNum = 1;
	private final static int slotCost = 2;
	
	private final static float procCd = 1.0f;
	private final static float damage = 12.0f;
	
	public WrathoftheFrogman() {
		super(slotCost, statusNum);
	}

	@Override
	public Status[] loadEnchantments(PlayState state, BodyData b) {
		enchantment[0] = new Status(state, b) {
			
			private float procCdCount;
			
			@Override
			public void whileAttacking(float delta, Equipable tool) {
				
				if (procCdCount < procCd) {
					procCdCount += delta;
				}
				
				if (procCdCount >= procCd) {
					procCdCount -= procCd;
					
					WeaponUtils.createHomingTorpedo(state, inflicted.getSchmuck().getPixelPosition(), inflicted.getSchmuck(), damage, 1, 1, new Vector2(0, 1), false, inflicted.getSchmuck().getHitboxfilter());
				}
			}
		};
		return enchantment;
	}
}
