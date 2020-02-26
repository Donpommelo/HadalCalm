package com.mygdx.hadal.equip.artifacts;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.equip.Equipable;
import com.mygdx.hadal.equip.WeaponUtils;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Status;

public class ForagersHive extends Artifact {

	private final static int statusNum = 1;
	private final static int slotCost = 1;
	
	private final static float procCd = 0.75f;
	
	public ForagersHive() {
		super(slotCost, statusNum);
	}

	@Override
	public Status[] loadEnchantments(PlayState state, BodyData b) {
		enchantment[0] = new Status(state, b) {
			
			private float procCdCount;
			
			@Override
			public void whileAttacking(float delta, Equipable tool) {
				
				if (tool.isReloading()) {
					return;
				}
				
				if (procCdCount < procCd) {
					procCdCount += delta;
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
