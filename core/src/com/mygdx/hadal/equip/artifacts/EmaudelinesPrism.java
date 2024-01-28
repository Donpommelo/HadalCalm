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

	private static final int SLOT_COST = 3;
	
	private static final int SPREAD = 30;
	private static final float ATK_SPD_REDUCTION = -0.75f;
	private static final float RELOAD_SPD_REDUCTION = -0.5f;
	
	public EmaudelinesPrism() {
		super(SLOT_COST);
	}

	@Override
	public void loadEnchantments(PlayState state, PlayerBodyData p) {
		enchantment = new StatusComposite(state, p,
				new StatChangeStatus(state, Stats.RANGED_ATK_SPD, ATK_SPD_REDUCTION, p),
				new StatChangeStatus(state, Stats.RANGED_RELOAD, RELOAD_SPD_REDUCTION, p),
				new Status(state, p) {
			
			private final Vector2 projVelo = new Vector2();
			private final Vector2 projAngle1 = new Vector2();
			private final Vector2 projAngle2 = new Vector2();
			@Override
			public void onShoot(Equippable tool) {
				projVelo.set(tool.getWeaponVelo());
				projAngle1.set(projVelo).setAngleDeg(projVelo.angleDeg() + SPREAD);
				projAngle2.set(projVelo).setAngleDeg(projVelo.angleDeg() - SPREAD);
				p.getPlayer().getEquipHelper().getCurrentTool().fire(state, p.getPlayer(), p.getPlayer().getProjectileOrigin(projAngle1, tool.getAmmoSize()), projAngle1, p.getSchmuck().getHitboxFilter());
				p.getPlayer().getEquipHelper().getCurrentTool().fire(state, p.getPlayer(), p.getPlayer().getProjectileOrigin(projAngle2, tool.getAmmoSize()), projAngle2, p.getSchmuck().getHitboxFilter());
			}
		});
	}

	@Override
	public String[] getDescFields() {
		return new String[] {
				String.valueOf((int) -(ATK_SPD_REDUCTION * 100)),
				String.valueOf((int) -(RELOAD_SPD_REDUCTION * 100))};
	}
}
