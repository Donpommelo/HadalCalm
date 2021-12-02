package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.equip.Equippable;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.StatChangeStatus;
import com.mygdx.hadal.statuses.Status;
import com.mygdx.hadal.statuses.StatusComposite;
import com.mygdx.hadal.utils.Stats;

import java.util.concurrent.ThreadLocalRandom;

public class EightBall extends Artifact {

	private static final int slotCost = 1;
	
	private static final int spread = 20;
	private static final float damageAmp = 0.3f;
	
	public EightBall() {
		super(slotCost);
	}

	@Override
	public void loadEnchantments(PlayState state, PlayerBodyData p) {
		enchantment = new StatusComposite(state, p,
				new StatChangeStatus(state, Stats.DAMAGE_AMP, damageAmp, p),
				new Status(state, p) {
					
					@Override
					public void onShoot(Equippable tool) {
						float newDegrees = tool.getWeaponVelo().angleDeg() + (ThreadLocalRandom.current().nextInt(-spread, spread + 1));
						tool.setWeaponVelo(tool.getWeaponVelo().setAngleDeg(newDegrees));
					}
				});
	}
}
