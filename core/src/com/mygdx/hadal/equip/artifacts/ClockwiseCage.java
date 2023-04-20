package com.mygdx.hadal.equip.artifacts;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.equip.Equippable;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Status;

public class ClockwiseCage extends Artifact {

	private static final int SLOT_COST = 2;
	
	private static final float PROC_CD = 2.0f;
	private static final float ECHO_CD = 0.2f;
	
	public ClockwiseCage() { super(SLOT_COST); }

	@Override
	public void loadEnchantments(PlayState state, PlayerBodyData p) {
		enchantment = new Status(state, p) {
			
			private float procCdCount = PROC_CD;
			private boolean echoing;
			private float echoCdCount;
			private Equippable echoTool;
			private final Vector2 angle = new Vector2();
			@Override
			public void timePassing(float delta) {
				if (procCdCount < PROC_CD) {
					procCdCount += delta;
				}
				if (echoing) {
					echoCdCount -= delta;
					
					if (echoCdCount <= 0) {
						echoing = false;
						echoTool.fire(state, p.getPlayer(), p.getPlayer().getProjectileOrigin(angle, echoTool.getAmmoSize()),
								angle, p.getSchmuck().getHitboxFilter());
					}
				}
			}

			@Override
			public void onShoot(Equippable tool) {
				if (procCdCount >= PROC_CD) {
					procCdCount -= PROC_CD;
					echoing = true;
					echoCdCount = ECHO_CD;
					echoTool = tool;
					angle.set(tool.getWeaponVelo());
				}
			}
		};
	}

	@Override
	public String[] getDescFields() {
		return new String[] {
				String.valueOf((int) PROC_CD)};
	}
}
