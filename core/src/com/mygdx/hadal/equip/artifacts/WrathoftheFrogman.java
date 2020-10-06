package com.mygdx.hadal.equip.artifacts;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.equip.Equippable;
import com.mygdx.hadal.equip.WeaponUtils;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Status;

public class WrathoftheFrogman extends Artifact {

	private static final int statusNum = 1;
	private static final int slotCost = 2;
	
	private static final float procCd = 1.0f;
	private static final float damage = 18.0f;
	
	public WrathoftheFrogman() {
		super(slotCost, statusNum);
	}

	@Override
	public Status[] loadEnchantments(PlayState state, BodyData b) {
		enchantment[0] = new Status(state, b) {
			
			private float procCdCount;
			
			@Override
			public void timePassing(float delta) {
				if (procCdCount < procCd) {
					procCdCount += delta;
				}
			}
			
			@Override
			public void whileAttacking(float delta, Equippable tool) {
				
				if (tool.isReloading()) {
					return;
				}
				
				if (procCdCount >= procCd) {
					procCdCount -= procCd;
					
					WeaponUtils.createHomingTorpedo(state, inflicted.getSchmuck().getPixelPosition(), inflicted.getSchmuck(), damage, 1, new Vector2(0, 1), false, inflicted.getSchmuck().getHitboxfilter());
				}
			}
		};
		return enchantment;
	}
}
