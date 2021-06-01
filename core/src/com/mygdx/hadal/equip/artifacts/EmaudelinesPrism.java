package com.mygdx.hadal.equip.artifacts;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.equip.Equippable;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.StatChangeStatus;
import com.mygdx.hadal.statuses.Status;
import com.mygdx.hadal.statuses.StatusComposite;
import com.mygdx.hadal.utils.Stats;

public class EmaudelinesPrism extends Artifact {

	private static final int statusNum = 1;
	private static final int slotCost = 3;
	
	private static final int spread = 20;
	private static final float atkSpdReduction = -0.5f;
	private static final float reloadSpdReduction = -0.4f;
	
	public EmaudelinesPrism() {
		super(slotCost, statusNum);
	}

	@Override
	public Status[] loadEnchantments(PlayState state, BodyData b) {
		enchantment[0] = new StatusComposite(state, b, 
				new StatChangeStatus(state, Stats.RANGED_ATK_SPD, atkSpdReduction, b),
				new StatChangeStatus(state, Stats.RANGED_RELOAD, reloadSpdReduction, b),
				new Status(state, b) {
			
			private final Vector2 projVelo = new Vector2();
			private final Vector2 projAngle1 = new Vector2();
			private final Vector2 projAngle2 = new Vector2();
			@Override
			public void onShoot(Equippable tool) {
				projVelo.set(tool.getWeaponVelo());
				projAngle1.set(projVelo).setAngleDeg(projVelo.angleDeg() + spread);
				projAngle2.set(projVelo).setAngleDeg(projVelo.angleDeg() - spread);
				inflicted.getCurrentTool().fire(state, inflicted.getSchmuck(), inflicted.getSchmuck().getProjectileOrigin(projAngle1, tool.getAmmoSize()), projAngle1, inflicted.getSchmuck().getHitboxfilter());
				inflicted.getCurrentTool().fire(state, inflicted.getSchmuck(), inflicted.getSchmuck().getProjectileOrigin(projAngle2, tool.getAmmoSize()), projAngle2, inflicted.getSchmuck().getHitboxfilter());
			}
		});
		return enchantment;
	}
}
