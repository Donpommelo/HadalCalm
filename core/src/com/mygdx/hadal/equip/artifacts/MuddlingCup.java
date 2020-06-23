package com.mygdx.hadal.equip.artifacts;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.equip.Equipable;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Status;
import com.mygdx.hadal.statuses.StatusComposite;

public class MuddlingCup extends Artifact {

	private final static int statusNum = 1;
	private final static int slotCost = 1;
	
	private final static float projSpeed = 12.0f;
	
	public MuddlingCup() {
		super(slotCost, statusNum);
	}

	@Override
	public Status[] loadEnchantments(PlayState state, BodyData b) {
		enchantment[0] = new StatusComposite(state, b, 
				new Status(state, b) {
			
			private Vector2 projAngle = new Vector2();
			@Override
			public void onAirBlast(Equipable tool) {
				inflicted.getCurrentTool().fire(state, inflicted.getSchmuck(), inflicted.getSchmuck().getProjectileOrigin(projAngle, tool.getAmmoSize()), projAngle.set(tool.getWeaponVelo()).scl(projSpeed),
						inflicted.getSchmuck().getHitboxfilter());
			}
		});
		return enchantment;
	}
}
