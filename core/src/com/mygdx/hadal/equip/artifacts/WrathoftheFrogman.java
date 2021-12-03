package com.mygdx.hadal.equip.artifacts;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.equip.Equippable;
import com.mygdx.hadal.equip.WeaponUtils;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Status;

public class WrathoftheFrogman extends Artifact {

	private static final int slotCost = 2;
	
	private static final float procCd = 0.8f;
	private static final float damage = 26;
	
	public WrathoftheFrogman() {
		super(slotCost);
	}

	@Override
	public void loadEnchantments(PlayState state, PlayerBodyData p) {
		enchantment = new Status(state, p) {
			
			private float procCdCount = procCd;
			@Override
			public void timePassing(float delta) {
				if (procCdCount < procCd) {
					procCdCount += delta;
				}
			}
			
			@Override
			public void whileAttacking(float delta, Equippable tool) {
				if (tool.isReloading()) { return; }
				
				if (procCdCount >= procCd) {
					procCdCount -= procCd;
					
					WeaponUtils.createHomingTorpedo(state, p.getSchmuck().getPixelPosition(), p.getSchmuck(), damage, 1,
							new Vector2(0, 1), false, p.getSchmuck().getHitboxfilter());
				}
			}
		};
	}
}
