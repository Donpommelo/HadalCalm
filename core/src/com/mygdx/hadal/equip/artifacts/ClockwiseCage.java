package com.mygdx.hadal.equip.artifacts;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.equip.Equippable;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Status;

public class ClockwiseCage extends Artifact {

	private static final int statusNum = 1;
	private static final int slotCost = 2;
	
	private static final float procCd = 2.0f;
	private static final float echoCd = 0.25f;
	
	public ClockwiseCage() {
		super(slotCost, statusNum);
	}

	@Override
	public Status[] loadEnchantments(PlayState state, final BodyData b) {
		enchantment[0] = new Status(state, b) {
			
			private float procCdCount = procCd;
			private boolean echoing;
			private float echoCdCount;
			private Equippable echoTool;
			private final Vector2 angle = new Vector2();
			@Override
			public void timePassing(float delta) {
				if (procCdCount < procCd) {
					procCdCount += delta;
				}
				if (echoing) {
					echoCdCount -= delta;
					
					if (echoCdCount <= 0) {
						echoing = false;
						echoTool.fire(state, inflicted.getSchmuck(), inflicted.getSchmuck().getProjectileOrigin(angle, echoTool.getAmmoSize()), angle, inflicted.getSchmuck().getHitboxfilter());
					}
				}
			}

			@Override
			public void onShoot(Equippable tool) {
				
				if (procCdCount >= procCd) {
					procCdCount -= procCd;
					echoing = true;
					echoCdCount = echoCd;
					echoTool = tool;
					angle.set(tool.getWeaponVelo());
				}
			}
		};
		return enchantment;
	}
}
