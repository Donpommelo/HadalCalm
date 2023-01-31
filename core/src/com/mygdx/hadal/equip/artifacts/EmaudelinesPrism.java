package com.mygdx.hadal.equip.artifacts;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.equip.Equippable;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.StatChangeStatus;
import com.mygdx.hadal.statuses.Status;
import com.mygdx.hadal.statuses.StatusComposite;
import com.mygdx.hadal.constants.Stats;

public class EmaudelinesPrism extends Artifact {

	private static final int slotCost = 3;
	
	private static final int spread = 30;
	private static final float atkSpdReduction = -0.75f;
	private static final float reloadSpdReduction = -0.5f;
	
	public EmaudelinesPrism() {
		super(slotCost);
	}

	@Override
	public void loadEnchantments(PlayState state, PlayerBodyData p) {
		enchantment = new StatusComposite(state, p,
				new StatChangeStatus(state, Stats.RANGED_ATK_SPD, atkSpdReduction, p),
				new StatChangeStatus(state, Stats.RANGED_RELOAD, reloadSpdReduction, p),
				new Status(state, p) {
			
			private final Vector2 projVelo = new Vector2();
			private final Vector2 projAngle1 = new Vector2();
			private final Vector2 projAngle2 = new Vector2();
			@Override
			public void onShoot(Equippable tool) {
				projVelo.set(tool.getWeaponVelo());
				projAngle1.set(projVelo).setAngleDeg(projVelo.angleDeg() + spread);
				projAngle2.set(projVelo).setAngleDeg(projVelo.angleDeg() - spread);
				inflicted.getCurrentTool().fire(state, p.getSchmuck(), p.getSchmuck().getProjectileOrigin(projAngle1, tool.getAmmoSize()), projAngle1, p.getSchmuck().getHitboxFilter());
				inflicted.getCurrentTool().fire(state, p.getSchmuck(), p.getSchmuck().getProjectileOrigin(projAngle2, tool.getAmmoSize()), projAngle2, p.getSchmuck().getHitboxFilter());
			}
		});
	}

	@Override
	public String[] getDescFields() {
		return new String[] {
				String.valueOf((int) -(atkSpdReduction * 100)),
				String.valueOf((int) -(reloadSpdReduction * 100))};
	}
}
