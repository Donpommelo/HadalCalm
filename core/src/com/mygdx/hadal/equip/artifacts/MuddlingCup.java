package com.mygdx.hadal.equip.artifacts;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.equip.Equippable;
import com.mygdx.hadal.equip.RangedWeapon;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Status;
import com.mygdx.hadal.statuses.StatusComposite;

public class MuddlingCup extends Artifact {

	private static final int statusNum = 1;
	private static final int slotCost = 1;
	
	public MuddlingCup() {
		super(slotCost, statusNum);
	}

	@Override
	public Status[] loadEnchantments(PlayState state, BodyData b) {
		enchantment[0] = new StatusComposite(state, b, 
				new Status(state, b) {
			
			private final Vector2 projAngle = new Vector2();
			@Override
			public void onAirBlast(Equippable tool) {
				
				if (inflicted.getCurrentTool() instanceof RangedWeapon) {
					float projectileSpeed = ((RangedWeapon) inflicted.getCurrentTool()).getProjectileSpeed();
					
					inflicted.getCurrentTool().fire(state, inflicted.getSchmuck(), inflicted.getSchmuck().getProjectileOrigin(projAngle, tool.getAmmoSize()), projAngle.set(tool.getWeaponVelo()).nor().scl(projectileSpeed),
							inflicted.getSchmuck().getHitboxfilter());
				}
			}
		});
		return enchantment;
	}
}
