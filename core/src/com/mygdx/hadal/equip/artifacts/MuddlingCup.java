package com.mygdx.hadal.equip.artifacts;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.equip.RangedWeapon;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Status;
import com.mygdx.hadal.statuses.StatusComposite;

public class MuddlingCup extends Artifact {

	private static final int slotCost = 1;
	
	public MuddlingCup() {
		super(slotCost);
	}

	@Override
	public void loadEnchantments(PlayState state, PlayerBodyData p) {
		enchantment = new StatusComposite(state, p, new Status(state, p) {
			
			private final Vector2 projAngle = new Vector2();
			@Override
			public void onAirBlast(Vector2 airblastDirection) {

				if (p.getCurrentTool() instanceof RangedWeapon ranged) {
					float projectileSpeed = ranged.getProjectileSpeed();
					
					p.getCurrentTool().fire(state, p.getPlayer(), p.getPlayer().getProjectileOrigin(projAngle, p.getCurrentTool().getAmmoSize()),
							projAngle.set(airblastDirection).nor().scl(projectileSpeed), p.getPlayer().getHitboxFilter());
				}
			}
		});
	}
}
