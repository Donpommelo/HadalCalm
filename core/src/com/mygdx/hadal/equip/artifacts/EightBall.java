package com.mygdx.hadal.equip.artifacts;

import com.badlogic.gdx.math.MathUtils;
import com.mygdx.hadal.equip.Equippable;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.StatChangeStatus;
import com.mygdx.hadal.statuses.Status;
import com.mygdx.hadal.statuses.StatusComposite;
import com.mygdx.hadal.constants.Stats;

public class EightBall extends Artifact {

	private static final int SLOT_COST = 1;
	
	private static final int SPREAD = 20;
	private static final float DAMAGE_AMP = 0.3f;
	
	public EightBall() {
		super(SLOT_COST);
	}

	@Override
	public void loadEnchantments(PlayState state, PlayerBodyData p) {
		enchantment = new StatusComposite(state, p,
				new StatChangeStatus(state, Stats.DAMAGE_AMP, DAMAGE_AMP, p),
				new Status(state, p) {
					
					@Override
					public void onShoot(Equippable tool) {
						float newDegrees = tool.getWeaponVelo().angleDeg() + (MathUtils.random(-SPREAD, SPREAD + 1));
						tool.setWeaponVelo(tool.getWeaponVelo().setAngleDeg(newDegrees));
					}
				}).setUserOnly(true);
	}

	@Override
	public String[] getDescFields() {
		return new String[] {
				String.valueOf((int) (DAMAGE_AMP * 100)),
				String.valueOf(SPREAD)};
	}
}
