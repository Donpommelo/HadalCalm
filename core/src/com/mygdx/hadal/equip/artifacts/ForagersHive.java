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
	private static final float beeSpeed = 15.0f;
	private static final int homeRadius = 20;

	public ForagersHive() {
		super(slotCost, statusNum);
	}

	@Override
	public Status[] loadEnchantments(PlayState state, BodyData b) {
		enchantment[0] = new Status(state, b) {
			
			private float procCdCount = procCd;
			private final Vector2 startVelo = new Vector2();
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
					WeaponUtils.createBees(state, inflicted.getSchmuck().getPixelPosition(), inflicted.getSchmuck(), 1, homeRadius,
						startVelo.set(tool.getWeaponVelo()).nor().scl(beeSpeed), false, inflicted.getSchmuck().getHitboxfilter());
				}
			}
		};
		return enchantment;
	}
}
