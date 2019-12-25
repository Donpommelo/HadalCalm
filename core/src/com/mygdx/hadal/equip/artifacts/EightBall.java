package com.mygdx.hadal.equip.artifacts;

import java.util.concurrent.ThreadLocalRandom;

import com.mygdx.hadal.equip.Equipable;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.StatChangeStatus;
import com.mygdx.hadal.statuses.Status;
import com.mygdx.hadal.statuses.StatusComposite;
import com.mygdx.hadal.utils.Stats;

public class EightBall extends Artifact {

	private final static int statusNum = 1;
	private final static int slotCost = 1;
	
	private final static int spread = 20;
	private final static float damageAmp = 0.25f;
	
	public EightBall() {
		super(slotCost, statusNum);
	}

	@Override
	public Status[] loadEnchantments(PlayState state, final BodyData b) {
		enchantment[0] = new StatusComposite(state, b, 
				new StatChangeStatus(state, Stats.DAMAGE_AMP, damageAmp, b),
				new Status(state, b) {
					
					@Override
					public void onShoot(Equipable tool) {
						float newDegrees = (float) (tool.getWeaponVelo().angle() + (ThreadLocalRandom.current().nextInt(-spread, spread + 1)));
						tool.setWeaponVelo(tool.getWeaponVelo().setAngle(newDegrees));
					}
					
				});
		return enchantment;
	}
}
