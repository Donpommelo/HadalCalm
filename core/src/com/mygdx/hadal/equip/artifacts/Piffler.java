package com.mygdx.hadal.equip.artifacts;

import com.badlogic.gdx.math.MathUtils;
import com.mygdx.hadal.equip.Equippable;
import com.mygdx.hadal.equip.RangedWeapon;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.StatChangeStatus;
import com.mygdx.hadal.statuses.Status;
import com.mygdx.hadal.statuses.StatusComposite;
import com.mygdx.hadal.utils.Stats;

public class Piffler extends Artifact {

	private static final int slotCost = 1;
	
	private static final int spread = 30;
	private static final float projSpeedReduction = -0.6f;
	private static final float projLifeReduction = 0.6f;
	private static final float projRecoilReduction = -0.75f;
	private static final float damageReduction = -0.25f;
	private static final float projectileSizeReduction = -0.4f;
	private static final float bonusAtkSpd = 0.6f;
	private static final float bonusClip = 1.0f;

	public Piffler() {
		super(slotCost);
	}

	@Override
	public void loadEnchantments(PlayState state, PlayerBodyData p) {
		enchantment = new StatusComposite(state, p,
				new StatChangeStatus(state, Stats.RANGED_PROJ_SPD, projSpeedReduction, p),
				new StatChangeStatus(state, Stats.RANGED_ATK_SPD, bonusAtkSpd, p),
				new StatChangeStatus(state, Stats.RANGED_CLIP, bonusClip, p),
				new StatChangeStatus(state, Stats.RANGED_PROJ_LIFESPAN, projLifeReduction, p),
				new StatChangeStatus(state, Stats.RANGED_PROJ_SIZE, projectileSizeReduction, p),
				new StatChangeStatus(state, Stats.RANGED_RECOIL, projRecoilReduction, p),
				new StatChangeStatus(state, Stats.DAMAGE_AMP, damageReduction, p),
				new Status(state, p) {
			
			@Override
			public void onShoot(Equippable tool) {
				float newDegrees = tool.getWeaponVelo().angleDeg() + MathUtils.random(-spread, spread + 1);
				tool.setWeaponVelo(tool.getWeaponVelo().setAngleDeg(newDegrees));
			}
			
			@Override
			public void onReloadFinish(Equippable tool) {
				if (p.getCurrentTool() instanceof RangedWeapon weapon) {
					weapon.gainAmmo(1.0f);
				}
			}
		});
	}
}
