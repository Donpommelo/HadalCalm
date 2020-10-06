package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.equip.Equippable;
import com.mygdx.hadal.equip.RangedWeapon;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.StatChangeStatus;
import com.mygdx.hadal.statuses.Status;
import com.mygdx.hadal.statuses.StatusComposite;
import com.mygdx.hadal.utils.Stats;

import java.util.concurrent.ThreadLocalRandom;

public class Piffler extends Artifact {

	private static final int statusNum = 1;
	private static final int slotCost = 1;
	
	private static final int spread = 30;
	private static final float projSpeedReduction = -0.6f;
	private static final float projLifeReduction = -0.6f;
	private static final float projRecoilReduction = -0.75f;
	private static final float damageReduction = -0.25f;
	private static final float projectileSizeReduction = -0.4f;
	private static final float bonusAtkSpd = 0.6f;
	private static final float bonusClip = 1.0f;

	public Piffler() {
		super(slotCost, statusNum);
	}

	@Override
	public Status[] loadEnchantments(PlayState state, final BodyData b) {
		enchantment[0] = new StatusComposite(state, b, 
				new StatChangeStatus(state, Stats.RANGED_PROJ_SPD, projSpeedReduction, b),
				new StatChangeStatus(state, Stats.RANGED_ATK_SPD, bonusAtkSpd, b),
				new StatChangeStatus(state, Stats.RANGED_CLIP, bonusClip, b),
				new StatChangeStatus(state, Stats.RANGED_PROJ_LIFESPAN, projLifeReduction, b),
				new StatChangeStatus(state, Stats.RANGED_PROJ_SIZE, projectileSizeReduction, b),
				new StatChangeStatus(state, Stats.RANGED_RECOIL, projRecoilReduction, b),
				new StatChangeStatus(state, Stats.DAMAGE_AMP, damageReduction, b),
				new Status(state, b) {
			
			@Override
			public void onShoot(Equippable tool) {
				float newDegrees = tool.getWeaponVelo().angle() + (ThreadLocalRandom.current().nextInt(-spread, spread + 1));
				tool.setWeaponVelo(tool.getWeaponVelo().setAngle(newDegrees));
			}
			
			@Override
			public void onReload(Equippable tool) {
				if (this.inflicted instanceof PlayerBodyData) {
					if (this.inflicted.getCurrentTool() instanceof RangedWeapon) {
						RangedWeapon weapon = (RangedWeapon) this.inflicted.getCurrentTool();
						weapon.gainAmmo(1.0f);
					}
				}
			}
		});
		return enchantment;
	}
}
