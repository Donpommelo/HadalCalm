package com.mygdx.hadal.equip.artifacts;

import com.badlogic.gdx.math.MathUtils;
import com.mygdx.hadal.equip.Equippable;
import com.mygdx.hadal.equip.RangedWeapon;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.StatChangeStatus;
import com.mygdx.hadal.statuses.Status;
import com.mygdx.hadal.statuses.StatusComposite;
import com.mygdx.hadal.constants.Stats;

public class Piffler extends Artifact {

	private static final int SLOT_COST = 1;
	
	private static final int SPREAD = 30;
	private static final float PROJ_SPEED_REDUCTION = -0.6f;
	private static final float PROJ_LIFE_REDUCTION = 0.6f;
	private static final float PROJ_RECOIL_REDUCTION = -0.75f;
	private static final float DAMAGE_REDUCTION = -0.25f;
	private static final float PROJECTILE_SIZE_REDUCTION = -0.4f;
	private static final float BONUS_ATK_SPD = 0.6f;
	private static final float BONUS_CLIP = 1.0f;

	public Piffler() {
		super(SLOT_COST);
	}

	@Override
	public void loadEnchantments(PlayState state, PlayerBodyData p) {
		enchantment = new StatusComposite(state, p,
				new StatChangeStatus(state, Stats.RANGED_PROJ_SPD, PROJ_SPEED_REDUCTION, p),
				new StatChangeStatus(state, Stats.RANGED_ATK_SPD, BONUS_ATK_SPD, p),
				new StatChangeStatus(state, Stats.RANGED_CLIP, BONUS_CLIP, p),
				new StatChangeStatus(state, Stats.RANGED_PROJ_LIFESPAN, PROJ_LIFE_REDUCTION, p),
				new StatChangeStatus(state, Stats.RANGED_PROJ_SIZE, PROJECTILE_SIZE_REDUCTION, p),
				new StatChangeStatus(state, Stats.RANGED_RECOIL, PROJ_RECOIL_REDUCTION, p),
				new StatChangeStatus(state, Stats.DAMAGE_AMP, DAMAGE_REDUCTION, p),
				new Status(state, p) {
			
			@Override
			public void onShoot(Equippable tool) {
				float newDegrees = tool.getWeaponVelo().angleDeg() + MathUtils.random(-SPREAD, SPREAD + 1);
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
