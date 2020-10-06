package com.mygdx.hadal.equip.artifacts;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.equip.Equippable;
import com.mygdx.hadal.equip.WeaponUtils;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Status;

public class ForagersHive extends Artifact {

	private static final int statusNum = 1;
	private static final int slotCost = 1;
	
	private static final float procCd = 0.8f;
	
	public ForagersHive() {
		super(slotCost, statusNum);
	}

	@Override
	public Status[] loadEnchantments(PlayState state, BodyData b) {
		enchantment[0] = new Status(state, b) {
			
			private float procCdCount = procCd;
			
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
					WeaponUtils.createBees(state, inflicted.getSchmuck().getPixelPosition(), inflicted.getSchmuck(), 1, new Vector2(1, 1), false, inflicted.getSchmuck().getHitboxfilter());
				}
			}
		};
		return enchantment;
	}
}
