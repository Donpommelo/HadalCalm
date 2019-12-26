package com.mygdx.hadal.equip.artifacts;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.equip.Equipable;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.StatChangeStatus;
import com.mygdx.hadal.statuses.Status;
import com.mygdx.hadal.statuses.StatusComposite;
import com.mygdx.hadal.utils.Stats;

public class EmaudelinesPrism extends Artifact {

	private final static int statusNum = 1;
	private final static int slotCost = 3;
	
	private final static int spread = 20;
	private final static float damageReduction = -0.3f;
	
	public EmaudelinesPrism() {
		super(slotCost, statusNum);
	}

	@Override
	public Status[] loadEnchantments(PlayState state, BodyData b) {
		enchantment[0] = new StatusComposite(state, b, 
				new StatChangeStatus(state, Stats.DAMAGE_AMP, damageReduction, b),
				new Status(state, b) {
			
			private Vector2 projVelo = new Vector2();
			private Vector2 projAngle1 = new Vector2();
			private Vector2 projAngle2 = new Vector2();
			@Override
			public void onShoot(Equipable tool) {	
				projVelo.set(tool.getWeaponVelo());
				projAngle1.set(projVelo).setAngle(projVelo.angle() + spread);
				projAngle2.set(projVelo).setAngle(projVelo.angle() - spread);
				inflicted.getCurrentTool().fire(state, inflicted.getSchmuck(), inflicted.getSchmuck().getProjectileOrigin(projAngle1, tool.getAmmoSize()), projAngle1, inflicted.getSchmuck().getHitboxfilter());
				inflicted.getCurrentTool().fire(state, inflicted.getSchmuck(), inflicted.getSchmuck().getProjectileOrigin(projAngle2, tool.getAmmoSize()), projAngle2, inflicted.getSchmuck().getHitboxfilter());
			}
		});
		return enchantment;
	}
}
