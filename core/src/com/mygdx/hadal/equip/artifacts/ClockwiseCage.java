package com.mygdx.hadal.equip.artifacts;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.equip.Equipable;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Status;

public class ClockwiseCage extends Artifact {

	private final static String name = "Clockwise Cage";
	private final static String descr = "Projectile Echo.";
	private final static String descrLong = "";
	private final static int statusNum = 1;
	
	
	public ClockwiseCage() {
		super(name, descr, descrLong, statusNum);
	}

	@Override
	public Status[] loadEnchantments(PlayState state, final BodyData b) {
		enchantment[0] = new Status(state, name, descr, b) {
			
			private float procCdCount;
			private float procCd = 2.0f;			
			
			private boolean echoing;
			private float echoCdCount;
			private float echoCd = 0.25f;
			private Equipable echoTool;
			private Vector2 angle = new Vector2();
			
			@Override
			public void timePassing(float delta) {
				if (procCdCount < procCd) {
					procCdCount += delta;
				}
				if (echoing) {
					echoCdCount -= delta;
					
					if (echoCdCount <= 0) {
						echoing = false;
						
						echoTool.fire(state, inflicted.getSchmuck(), inflicted.getSchmuck().getPixelPosition(), angle, inflicted.getSchmuck().getHitboxfilter());
					}
				}
			}

			@Override
			public void onShoot(Equipable tool) {
				
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
